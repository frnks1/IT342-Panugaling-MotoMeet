package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
