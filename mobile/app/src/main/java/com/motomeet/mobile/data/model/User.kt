package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("firstname", alternate = ["firstName", "first_name", "first", "name"]) val firstname: String? = null,
    @SerializedName("lastname", alternate = ["lastName", "last_name", "last"]) val lastname: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("profileImageUrl", alternate = ["profile_image_url", "avatar_url"]) val profileImageUrl: String? = null
)
