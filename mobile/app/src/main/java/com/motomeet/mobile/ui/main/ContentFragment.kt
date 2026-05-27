package com.motomeet.mobile.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.motomeet.mobile.BuildConfig
import com.motomeet.mobile.R
import com.motomeet.mobile.data.model.MobileContentItem
import com.motomeet.mobile.data.model.RideRequest
import com.motomeet.mobile.data.model.User
import com.motomeet.mobile.data.network.TokenManager
import com.motomeet.mobile.data.repository.MobileRepository
import com.motomeet.mobile.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class ContentFragment : Fragment() {

    private val repository = MobileRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var fabAdd: FloatingActionButton
    
    // Profile Header Views
    private lateinit var profileHeader: View
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvFirstName: TextView
    private lateinit var tvLastName: TextView
    private lateinit var ivUserProfile: ImageView
    private lateinit var tvCommunityTitle: TextView
    private lateinit var btnLogout: com.google.android.material.button.MaterialButton
    
    private var currentUser: User? = null
    
    private val adapter = SectionContentAdapter(
        onLike = { item -> handleLike(item) },
        onComment = { item -> showCommentDialog(item) },
        onShare = { item -> handleShare(item) },
        onItemClick = { item -> handleItemClick(item) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyView = view.findViewById(R.id.tvEmpty)
        fabAdd = view.findViewById(R.id.fabAdd)
        
        // Initialize Profile Views
        profileHeader = view.findViewById(R.id.profileHeader)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvFirstName = view.findViewById(R.id.tvFirstName)
        tvLastName = view.findViewById(R.id.tvLastName)
        ivUserProfile = view.findViewById(R.id.ivUserProfile)
        tvCommunityTitle = view.findViewById(R.id.tvCommunityTitle)
        btnLogout = view.findViewById(R.id.btnLogout)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val sectionName = arguments?.getString(ARG_SECTION) ?: ContentSection.FEED.name
        val section = runCatching { ContentSection.valueOf(sectionName) }.getOrDefault(ContentSection.FEED)

        setupFab(section)
        
        if (section == ContentSection.PROFILE) {
            loadUserProfile()
            setupProfileButtons()
        } else {
            profileHeader.visibility = View.GONE
            tvCommunityTitle.visibility = View.GONE
            loadSection(section)
        }
    }

    private fun loadUserProfile() {
        profileHeader.visibility = View.VISIBLE
        tvCommunityTitle.visibility = View.VISIBLE
        
        viewLifecycleOwner.lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            repository.getCurrentUser().onSuccess { user ->
                currentUser = user
                updateProfileUI(user)
                // Now load section with knowledge of the current user for filtering
                loadSection(ContentSection.PROFILE)
            }.onFailure { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error loading profile: ${error.message}", Toast.LENGTH_SHORT).show()
                loadSection(ContentSection.PROFILE)
            }
        }
    }
    
    private fun updateProfileUI(user: User) {
        val firstName = user.firstname?.trim().orEmpty()
        val lastName = user.lastname?.trim().orEmpty()
        val fullName = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

        tvUserName.text = if (fullName.isBlank()) {
            user.email?.trim().orEmpty().ifBlank { "MotoMeet Rider" }
        } else {
            fullName
        }
        tvUserEmail.text = user.email?.trim().orEmpty().ifBlank { "No email available" }
        tvFirstName.text = firstName.ifBlank { "N/A" }
        tvLastName.text = lastName.ifBlank { "N/A" }
        
        if (!user.profileImageUrl.isNullOrBlank()) {
            ivUserProfile.load(user.profileImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }
        }
    }

    private fun setupProfileButtons() {
        btnLogout.setOnClickListener {
            TokenManager.getInstance(requireContext()).clearToken()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun setupFab(section: ContentSection) {
        if (section == ContentSection.FEED || section == ContentSection.RIDES) {
            fabAdd.visibility = View.VISIBLE
            fabAdd.setOnClickListener {
                if (section == ContentSection.RIDES) {
                    showCreateRideDialog()
                } else {
                    showCreatePostDialog()
                }
            }
        } else {
            fabAdd.visibility = View.GONE
        }
    }

    private fun loadSection(section: ContentSection) {
        viewLifecycleOwner.lifecycleScope.launch {
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
                var displayItems = items
                
                // FILTER: Only show my posts on my profile
                if (section == ContentSection.PROFILE && currentUser != null) {
                    val myName = "${currentUser?.firstname} ${currentUser?.lastname}".trim()
                    displayItems = items.filter { item ->
                        // Show it if it's authored by me or if it's a community overview item
                        val isMine = item.subtitle?.contains(myName, ignoreCase = true) == true
                        val isOverview = item.badge?.contains("Overview", ignoreCase = true) == true
                        isMine || isOverview
                    }
                }

                adapter.submit(displayItems)
                if (displayItems.isEmpty()) {
                    emptyView.text = "No ${section.title.lowercase()} yet."
                    emptyView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                }
            }.onFailure { error ->
                emptyView.text = error.message ?: "Unable to load ${section.title.lowercase()}"
                emptyView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), emptyView.text, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleLike(item: MobileContentItem) {
        val itemId = item.id ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            repository.like(itemId).onSuccess {
                Toast.makeText(requireContext(), "Liked!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Error liking post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCommentDialog(item: MobileContentItem) {
        val itemId = item.id ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_comment, null)
        val etComment = dialogView.findViewById<EditText>(R.id.etComment)
        
        AlertDialog.Builder(requireContext(), R.style.MotoMeetDialog)
            .setTitle("Add Comment")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val comment = etComment.text.toString().trim()
                if (comment.isNotBlank()) {
                    postComment(itemId, comment)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun postComment(id: Long, text: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.comment(id, text).onSuccess {
                Toast.makeText(requireContext(), "Comment posted!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Error posting comment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleShare(item: MobileContentItem) {
        val itemId = item.id ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            repository.share(itemId).onSuccess {
                Toast.makeText(requireContext(), "Shared to your profile!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleItemClick(item: MobileContentItem) {
        if (!item.section.equals("marketplace", ignoreCase = true) || item.id == null) {
            return
        }

        startActivity(ListingDetailActivity.newIntent(requireContext(), item.id))
    }

    private fun showCreatePostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_post, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDescription)
        
        AlertDialog.Builder(requireContext(), R.style.MotoMeetDialog)
            .setTitle("New Feed Post")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val title = etTitle.text.toString().trim()
                val desc = etDesc.text.toString().trim()
                if (title.isNotBlank()) {
                    createNewFeedPost(title, desc)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreateRideDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_ride, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etRoute = dialogView.findViewById<EditText>(R.id.etRoute)
        val etDistance = dialogView.findViewById<EditText>(R.id.etDistance)
        val etDuration = dialogView.findViewById<EditText>(R.id.etDuration)
        val etSpeed = dialogView.findViewById<EditText>(R.id.etSpeed)
        val etRideDate = dialogView.findViewById<EditText>(R.id.etRideDate)
        val etImageUrl = dialogView.findViewById<EditText>(R.id.etImageUrl)

        AlertDialog.Builder(requireContext(), R.style.MotoMeetDialog)
            .setTitle("New Ride Log")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val title = etTitle.text.toString().trim()
                val route = etRoute.text.toString().trim()
                val distance = etDistance.text.toString().toIntOrNull()
                val duration = etDuration.text.toString().toIntOrNull()
                val speed = etSpeed.text.toString().toIntOrNull()
                val rideDate = etRideDate.text.toString().trim()
                val imageUrl = etImageUrl.text.toString().trim()

                if (title.isBlank() || route.isBlank() || distance == null || duration == null || speed == null || rideDate.isBlank()) {
                    Toast.makeText(requireContext(), "Please fill all required fields correctly.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                createNewRide(
                    RideRequest(
                        title = title,
                        route = route,
                        distanceMiles = distance,
                        durationMinutes = duration,
                        avgSpeedMph = speed,
                        rideDate = rideDate,
                        imageUrl = imageUrl.ifBlank { null }
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNewFeedPost(title: String, description: String) {
        val newItem = MobileContentItem(
            title = title,
            description = description,
            section = ContentSection.FEED.name,
            timestamp = "Just now"
        )
        
        viewLifecycleOwner.lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            repository.createPost(newItem).onSuccess {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Posted successfully!", Toast.LENGTH_SHORT).show()
                loadSection(ContentSection.FEED)
            }.onFailure {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it.message ?: "Failed to post", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNewRide(rideRequest: RideRequest) {
        viewLifecycleOwner.lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            repository.createRide(rideRequest).onSuccess {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Ride logged successfully!", Toast.LENGTH_SHORT).show()
                loadSection(ContentSection.RIDES)
            }.onFailure {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it.message ?: "Failed to post ride", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_SECTION = "arg_section"

        fun newInstance(section: ContentSection): ContentFragment {
            return ContentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SECTION, section.name)
                }
            }
        }
    }
}
