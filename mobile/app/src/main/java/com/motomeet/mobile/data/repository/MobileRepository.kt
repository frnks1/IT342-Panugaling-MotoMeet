package com.motomeet.mobile.data.repository

import com.motomeet.mobile.data.model.*
import com.motomeet.mobile.data.network.MobileApi
import com.motomeet.mobile.data.network.RetrofitClient

class MobileRepository {

    private val mobileApi: MobileApi = RetrofitClient.retrofit.create(MobileApi::class.java)

    suspend fun feed(): Result<List<MobileContentItem>> = fetchList { mobileApi.feed() }

    suspend fun marketplace(): Result<List<MobileContentItem>> = fetchList { mobileApi.marketplace() }

    suspend fun rides(): Result<List<MobileContentItem>> = fetchList { mobileApi.rides() }

    suspend fun meetups(): Result<List<MobileContentItem>> = fetchList { mobileApi.meetups() }

    suspend fun notifications(): Result<List<MobileContentItem>> = fetchList { mobileApi.notifications() }

    suspend fun profile(): Result<List<MobileContentItem>> = fetchList { mobileApi.profile() }
    
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = mobileApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(item: MobileContentItem): Result<MobileContentItem> {
        return try {
            val response = mobileApi.createPost(item)
            if (response.isSuccessful) {
                Result.success(response.body() ?: item)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRide(ride: RideRequest): Result<MobileContentItem> {
        return try {
            val response = mobileApi.createRide(ride)
            if (response.isSuccessful) {
                Result.success(response.body() ?: MobileContentItem(section = "rides", title = ride.title, subtitle = ride.route))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to post ride"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun like(id: Long): Result<Unit> = performAction { mobileApi.likeItem(id) }
    suspend fun comment(id: Long, text: String): Result<Unit> = performAction { mobileApi.commentItem(id, com.motomeet.mobile.data.model.CommentRequest(text)) }
    suspend fun share(id: Long): Result<Unit> = performAction { mobileApi.shareItem(id) }

    // Chat methods
    suspend fun getChatMessages(recipientId: Long, itemId: Long): Result<List<ChatMessage>> {
        return try {
            val response = mobileApi.getChatMessages(recipientId, itemId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.messages)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(recipientId: Long, content: String, itemId: Long?): Result<ChatMessage> {
        return try {
            val request = SendMessageRequest(content = content, recipientId = recipientId, itemId = itemId)
            val response = mobileApi.sendMessage(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun fetchList(call: suspend () -> retrofit2.Response<List<MobileContentItem>>): Result<List<MobileContentItem>> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unable to load content"))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun performAction(call: suspend () -> retrofit2.Response<Unit>): Result<Unit> {
        return try {
            val response = call()
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(response.errorBody()?.string() ?: "Action failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
