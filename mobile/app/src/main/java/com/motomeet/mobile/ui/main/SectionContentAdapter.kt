package com.motomeet.mobile.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton
import com.motomeet.mobile.R
import com.motomeet.mobile.data.model.MobileContentItem

class SectionContentAdapter(
    private val onLike: (MobileContentItem) -> Unit,
    private val onComment: (MobileContentItem) -> Unit,
    private val onShare: (MobileContentItem) -> Unit,
    private val onItemClick: (MobileContentItem) -> Unit
) : ListAdapter<MobileContentItem, SectionContentAdapter.ContentViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<MobileContentItem>() {
        override fun areItemsTheSame(oldItem: MobileContentItem, newItem: MobileContentItem): Boolean {
            return oldItem.id == newItem.id && oldItem.section == newItem.section
        }

        override fun areContentsTheSame(oldItem: MobileContentItem, newItem: MobileContentItem): Boolean {
            return oldItem == newItem
        }
    }

    fun submit(items: List<MobileContentItem>) {
        submitList(items.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mobile_content, parent, false)
        return ContentViewHolder(view, onLike, onComment, onShare, onItemClick)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContentViewHolder(
        itemView: View,
        private val onLike: (MobileContentItem) -> Unit,
        private val onComment: (MobileContentItem) -> Unit,
        private val onShare: (MobileContentItem) -> Unit,
        private val onItemClick: (MobileContentItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvBadge: TextView = itemView.findViewById(R.id.tvBadge)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvMetaLeft: TextView = itemView.findViewById(R.id.tvMetaLeft)
        private val tvMetaRight: TextView = itemView.findViewById(R.id.tvMetaRight)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        private val ivPrimary: ImageView = itemView.findViewById(R.id.ivPrimary)
        private val ivSecondary: ImageView = itemView.findViewById(R.id.ivSecondary)
        private val btnLike: MaterialButton = itemView.findViewById(R.id.btnLike)
        private val btnComment: MaterialButton = itemView.findViewById(R.id.btnComment)
        private val btnShare: MaterialButton = itemView.findViewById(R.id.btnShare)

        fun bind(item: MobileContentItem) {
            tvBadge.text = item.badge ?: item.section.orEmpty()
            tvTitle.text = item.title.orEmpty()
            tvSubtitle.text = item.subtitle.orEmpty()
            tvDescription.text = item.description.orEmpty()
            
            // Filter out unwanted "Supabase" and "Updated live" text
            val metaLeft = item.metaLeft.orEmpty()
            val metaRight = item.metaRight.orEmpty()
            
            if (metaLeft.contains("Supabase", ignoreCase = true) || metaLeft.isBlank()) {
                tvMetaLeft.visibility = View.GONE
            } else {
                tvMetaLeft.visibility = View.VISIBLE
                tvMetaLeft.text = metaLeft
            }
            
            if (metaRight.contains("Updated live", ignoreCase = true) || metaRight.isBlank()) {
                tvMetaRight.visibility = View.GONE
            } else {
                tvMetaRight.visibility = View.VISIBLE
                tvMetaRight.text = metaRight
            }

            tvTimestamp.text = item.timestamp.orEmpty()

            val hasPrimary = !item.imageUrl.isNullOrBlank()
            ivPrimary.visibility = if (hasPrimary) View.VISIBLE else View.GONE
            if (hasPrimary) {
                ivPrimary.load(item.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.bg_input)
                    error(R.drawable.bg_input)
                }
            }

            val hasSecondary = !item.secondaryImageUrl.isNullOrBlank()
            ivSecondary.visibility = if (hasSecondary) View.VISIBLE else View.GONE
            if (hasSecondary) {
                ivSecondary.load(item.secondaryImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.bg_input)
                    error(R.drawable.bg_input)
                }
            }

            val isFeedItem = item.section.equals("feed", ignoreCase = true)
            val isRideItem = item.section.equals("rides", ignoreCase = true)
            val isMarketplaceItem = item.section.equals("marketplace", ignoreCase = true)
            val isCommunityOverview = item.badge == "Community Overview"

            if (isCommunityOverview) {
                btnLike.visibility = View.GONE
                btnComment.visibility = View.GONE
                btnShare.visibility = View.GONE
                tvTimestamp.visibility = View.GONE
            } else {
                // Only show actions for regular feed items, not rides
                btnLike.visibility = if (isFeedItem) View.VISIBLE else View.GONE
                btnComment.visibility = if (isFeedItem) View.VISIBLE else View.GONE
                btnShare.visibility = if (isFeedItem) View.VISIBLE else View.GONE
                tvTimestamp.visibility = View.VISIBLE
            }

            btnLike.setOnClickListener { onLike(item) }
            btnComment.setOnClickListener { onComment(item) }
            btnShare.setOnClickListener { onShare(item) }

            // Set click listener for the entire card (including marketplace items)
            itemView.setOnClickListener {
                if (isMarketplaceItem || isFeedItem || isRideItem) {
                    onItemClick(item)
                }
            }
        }
    }
}
