package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class MobileContentItem(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("section") val section: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("secondaryImageUrl") val secondaryImageUrl: String? = null,
    @SerializedName("metaLeft") val metaLeft: String? = null,
    @SerializedName("metaRight") val metaRight: String? = null,
    @SerializedName("badge") val badge: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)