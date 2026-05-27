package com.motomeet.mobile.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.motomeet.mobile.R
import com.motomeet.mobile.data.model.MobileContentItem
import com.motomeet.mobile.data.repository.MobileRepository
import kotlinx.coroutines.launch

class SectionActivity : AppCompatActivity() {

    private val repository = MobileRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var titleView: TextView

    private val adapter = SectionContentAdapter(
        onLike = { item -> handleLike(item) },
        onComment = { item -> handleComment(item) },
        onShare = { item -> handleShare(item) },
        onItemClick = { _ -> }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.tvEmpty)
        titleView = findViewById(R.id.tvSectionTitle)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val section = intent.getStringExtra(EXTRA_SECTION) ?: ContentSection.FEED.name
        val resolvedSection = runCatching { ContentSection.valueOf(section) }.getOrDefault(ContentSection.FEED)

        titleView.text = resolvedSection.title
        loadSection(resolvedSection)
    }

    private fun loadSection(section: ContentSection) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.GONE

            val result = when (section) {
                ContentSection.FEED -> repository.feed()
                ContentSection.MARKETPLACE -> repository.marketplace()
                ContentSection.RIDES -> repository.rides()
                ContentSection.MEETUPS -> repository.meetups()
                ContentSection.NOTIFICATIONS -> repository.notifications()
                ContentSection.PROFILE -> repository.profile()
            }

            progressBar.visibility = View.GONE
            result.onSuccess { items ->
                adapter.submit(items)
                if (items.isEmpty()) {
                    emptyView.text = "No ${section.title.lowercase()} yet."
                    emptyView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                }
            }.onFailure { error ->
                emptyView.text = error.message ?: "Unable to load ${section.title.lowercase()}"
                emptyView.visibility = View.VISIBLE
                Toast.makeText(this@SectionActivity, emptyView.text, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleLike(item: MobileContentItem) {
        val id = item.id ?: return
        lifecycleScope.launch {
            repository.like(id).onSuccess {
                Toast.makeText(this@SectionActivity, "Liked!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@SectionActivity, "Error liking post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleComment(item: MobileContentItem) {
        Toast.makeText(this, "Comment feature coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun handleShare(item: MobileContentItem) {
        val id = item.id ?: return
        lifecycleScope.launch {
            repository.share(id).onSuccess {
                Toast.makeText(this@SectionActivity, "Shared!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@SectionActivity, "Error sharing post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val EXTRA_SECTION = "extra_section"

        fun newIntent(context: Context, section: ContentSection): Intent {
            return Intent(context, SectionActivity::class.java).apply {
                putExtra(EXTRA_SECTION, section.name)
            }
        }
    }
}
