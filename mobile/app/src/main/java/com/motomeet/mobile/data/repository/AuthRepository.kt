package com.motomeet.mobile.data.repository

import com.motomeet.mobile.data.model.AuthResponse
import com.motomeet.mobile.data.model.LoginRequest
import com.motomeet.mobile.data.model.RegisterRequest
import com.motomeet.mobile.data.network.RetrofitClient
import java.io.IOException

class AuthRepository {

    private val authApi = RetrofitClient.authApi

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = authApi.register(request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: AuthResponse(message = "Registration successful"))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: Check your connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = authApi.login(request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: AuthResponse(message = "Login successful"))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Invalid credentials"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: Check your connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
