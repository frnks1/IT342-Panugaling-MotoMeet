package com.motomeet.mobile.ui.main

enum class ContentSection(val title: String, val endpoint: String) {
    FEED("Feed", "feed"),
    MARKETPLACE("Marketplace", "marketplace"),
    RIDES("Rides", "rides"),
    MEETUPS("Meetups", "meetups"),
    NOTIFICATIONS("Notifications", "notifications"),
    PROFILE("Profile", "profile")
}