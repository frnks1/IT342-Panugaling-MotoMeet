package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class RideRequest(
    @SerializedName("title") val title: String,
    @SerializedName("route") val route: String,
    @SerializedName("distanceMiles") val distanceMiles: Int,
    @SerializedName("durationMinutes") val durationMinutes: Int,
    @SerializedName("avgSpeedMph") val avgSpeedMph: Int,
    @SerializedName("rideDate") val rideDate: String,
    @SerializedName("imageUrl") val imageUrl: String?
)
