package com.motomeet.mobile.data.network

import com.motomeet.mobile.data.model.AuthResponse
import com.motomeet.mobile.data.model.LoginRequest
import com.motomeet.mobile.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
