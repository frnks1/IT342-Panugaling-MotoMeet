package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class CommentRequest(
    @SerializedName("message") val message: String
)