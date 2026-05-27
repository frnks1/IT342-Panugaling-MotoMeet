package com.motomeet.mobile.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.motomeet.mobile.R
import com.motomeet.mobile.data.model.ChatMessage
import com.motomeet.mobile.data.model.User
import com.motomeet.mobile.data.repository.MobileRepository
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView

    private val repository = MobileRepository()
    private var sellerId: Long = -1
    private var sellerName: String = "Seller"
    private var itemTitle: String? = null
    private var itemId: Long = -1
    private var currentUser: User? = null

    private lateinit var chatAdapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sellerId = intent.getLongExtra(EXTRA_SELLER_ID, -1)
        sellerName = intent.getStringExtra(EXTRA_SELLER_NAME) ?: "Seller"
        itemTitle = intent.getStringExtra(EXTRA_ITEM_TITLE)
        itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1)

        if (sellerId == -1L) {
            Toast.makeText(this, "Seller not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (itemId == -1L) {
            // itemId is optional but warn if missing
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.messagesRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Use item title as the primary title if available, otherwise seller name
        toolbar.title = itemTitle ?: "Chat with $sellerName"
        if (itemTitle != null) {
            toolbar.subtitle = "Message $sellerName about this listing"
        }

        chatAdapter = ChatMessageAdapter(sellerName)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotBlank()) {
                sendMessage(message)
                messageInput.text.clear()
            }
        }

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            
            // First get current user to identify 'sent' vs 'received'
            repository.getCurrentUser().onSuccess { user ->
                currentUser = user
                chatAdapter.setCurrentUserId(user.id)
                loadMessages()
            }.onFailure {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ChatActivity, "Failed to load user info", Toast.LENGTH_SHORT).show()
                loadMessages() // Try loading messages anyway
            }
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            val effectiveItemId = if (itemId != -1L) itemId else 0L
            repository.getChatMessages(sellerId, effectiveItemId).onSuccess { messages ->
                progressBar.visibility = View.GONE
                if (messages.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = "Start a conversation with $sellerName"
                } else {
                    emptyView.visibility = View.GONE
                    chatAdapter.submitMessages(messages)
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }.onFailure {
                progressBar.visibility = View.GONE
                // Removed toast error to avoid showing it when starting a new chat
                if (chatAdapter.itemCount == 0) {
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = "Start a conversation with $sellerName"
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun sendMessage(content: String) {
        lifecycleScope.launch {
            val sendItemId = if (itemId != -1L) itemId else null
            repository.sendMessage(sellerId, content, sendItemId).onSuccess {
                loadMessages() // Refresh list
            }.onFailure {
                Toast.makeText(this@ChatActivity, "Failed to send: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val EXTRA_SELLER_ID = "extra_seller_id"
        private const val EXTRA_SELLER_NAME = "extra_seller_name"
        private const val EXTRA_ITEM_TITLE = "extra_item_title"
        private const val EXTRA_ITEM_ID = "extra_item_id"

        fun newIntent(context: Context, sellerId: Long, sellerName: String, itemTitle: String? = null, itemId: Long = -1): Intent {
            return Intent(context, ChatActivity::class.java).apply {
                putExtra(EXTRA_SELLER_ID, sellerId)
                putExtra(EXTRA_SELLER_NAME, sellerName)
                putExtra(EXTRA_ITEM_TITLE, itemTitle)
                putExtra(EXTRA_ITEM_ID, itemId)
            }
        }
    }
}

class ChatMessageAdapter(private val sellerName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages = mutableListOf<ChatMessage>()
    private var currentUserId: Long? = null

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    fun setCurrentUserId(id: Long?) {
        this.currentUserId = id
    }

    fun submitMessages(newMessages: List<ChatMessage>) {
        this.messages = newMessages.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        // Compare with currentUserId to decide if it's sent or received
        return if (message.senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_received, parent, false)
            ReceivedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedViewHolder) {
            holder.bind(message, sellerName)
        }
    }

    override fun getItemCount() = messages.size

    class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvContent: TextView = view.findViewById(R.id.tvContent)
        private val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        
        fun bind(message: ChatMessage) {
            tvContent.text = message.content
            tvTimestamp.text = formatTimestamp(message.createdAt)
        }
    }

    class ReceivedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvContent: TextView = view.findViewById(R.id.tvContent)
        private val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        private val tvSender: TextView = view.findViewById(R.id.tvSender)

        fun bind(message: ChatMessage, sellerName: String) {
            tvContent.text = message.content
            tvTimestamp.text = formatTimestamp(message.createdAt)
            tvSender.text = message.sender?.firstname ?: sellerName
        }
    }
}

private fun formatTimestamp(raw: String?): String {
    if (raw == null) return ""
    return try {
        // Expected format: 2024-05-27 05:07:16.39553
        // Just extract HH:mm
        if (raw.contains(" ")) {
            val timePart = raw.split(" ")[1]
            timePart.substring(0, 5)
        } else if (raw.contains("T")) {
            val timePart = raw.split("T")[1]
            timePart.substring(0, 5)
        } else {
            raw
        }
    } catch (e: Exception) {
        raw
    }
}
