package com.motomeet.mobile.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.motomeet.mobile.R
import com.motomeet.mobile.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        bottomNav.setOnItemSelectedListener { item ->
            val section = when (item.itemId) {
                R.id.nav_feed -> ContentSection.FEED
                R.id.nav_marketplace -> ContentSection.MARKETPLACE
                R.id.nav_rides -> ContentSection.RIDES
                R.id.nav_meetups -> ContentSection.MEETUPS
                R.id.nav_profile -> ContentSection.PROFILE
                else -> ContentSection.FEED
            }
            
            replaceFragment(ContentFragment.newInstance(section))
            true
        }

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_feed
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun logout() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }
}
