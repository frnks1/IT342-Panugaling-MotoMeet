package com.motomeet.mobile.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.button.MaterialButton
import com.motomeet.mobile.R
import com.motomeet.mobile.data.model.MarketplaceItem
import com.motomeet.mobile.data.network.RetrofitClient
import kotlinx.coroutines.launch

class ListingDetailActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var itemImage: ImageView
    private lateinit var itemTitle: TextView
    private lateinit var itemPrice: TextView
    private lateinit var itemCategory: TextView
    private lateinit var itemStatus: TextView
    private lateinit var itemListed: TextView
    private lateinit var itemDescription: TextView
    private lateinit var sellerName: TextView
    private lateinit var chatButton: MaterialButton

    private var currentItem: MarketplaceItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_detail)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        itemImage = findViewById(R.id.itemImage)
        itemTitle = findViewById(R.id.itemTitle)
        itemPrice = findViewById(R.id.itemPrice)
        itemCategory = findViewById(R.id.itemCategory)
        itemStatus = findViewById(R.id.itemStatus)
        itemListed = findViewById(R.id.itemListed)
        itemDescription = findViewById(R.id.itemDescription)
        sellerName = findViewById(R.id.sellerName)
        chatButton = findViewById(R.id.chatButton)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "Listing Details"

        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, -1)
        if (itemId == -1L) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadItemDetails(itemId)

        chatButton.setOnClickListener {
            currentItem?.let { item ->
                val sellerId = item.seller?.id
                if (sellerId != null) {
                    val name = if (!item.seller.firstname.isNullOrBlank()) {
                        "${item.seller.firstname} ${item.seller.lastname.orEmpty()}".trim()
                    } else {
                        "Seller"
                    }
                    
                    val displayTitle = if (!item.title.isNullOrBlank()) item.title else item.description?.take(30) ?: "Item"
                    
                    startActivity(ChatActivity.newIntent(this, sellerId, name, displayTitle, item.id ?: -1))
                } else {
                    Toast.makeText(this, "Seller information not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadItemDetails(itemId: Long) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.mobileApi.getMarketplaceItem(itemId)
                if (response.isSuccessful) {
                    val item = response.body()
                    if (item != null) {
                        currentItem = item
                        displayItem(item)
                    } else {
                        Toast.makeText(this@ListingDetailActivity, "Item details empty", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to load item details"
                    Toast.makeText(this@ListingDetailActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ListingDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayItem(item: MarketplaceItem) {
        val displayTitle = if (!item.title.isNullOrBlank()) item.title else item.description?.take(50)
        itemTitle.text = displayTitle ?: "Marketplace Item"
        
        itemPrice.text = String.format("$%.2f", item.price ?: 0.0)
        itemCategory.text = item.category ?: "Uncategorized"
        itemStatus.text = item.status ?: "Available"
        itemDescription.text = item.description ?: "No description provided."
        
        val sellerFullName = if (item.seller != null) {
            "${item.seller.firstname.orEmpty()} ${item.seller.lastname.orEmpty()}".trim()
        } else ""
        
        sellerName.text = sellerFullName.ifBlank { "MotoMeet User" }

        if (!item.createdAt.isNullOrBlank()) {
            val displayDate = if (item.createdAt.contains("T")) {
                item.createdAt.split("T")[0]
            } else if (item.createdAt.contains(" ")) {
                item.createdAt.split(" ")[0]
            } else {
                item.createdAt
            }
            itemListed.text = displayDate
        } else {
            itemListed.text = "Recently"
        }

        if (!item.imageUrl.isNullOrBlank()) {
            itemImage.load(item.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_input)
                error(R.drawable.bg_input)
            }
        } else {
            itemImage.setImageResource(R.drawable.bg_input)
        }
    }

    companion object {
        private const val EXTRA_ITEM_ID = "extra_item_id"

        fun newIntent(context: Context, itemId: Long): Intent {
            return Intent(context, ListingDetailActivity::class.java).apply {
                putExtra(EXTRA_ITEM_ID, itemId)
            }
        }
    }
}
