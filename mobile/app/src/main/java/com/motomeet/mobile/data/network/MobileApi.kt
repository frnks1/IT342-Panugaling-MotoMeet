package com.motomeet.mobile.data.network

import com.motomeet.mobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MobileApi {
    @GET("api/v1/mobile/feed")
    suspend fun feed(): Response<List<MobileContentItem>>

    @GET("api/v1/mobile/marketplace")
    suspend fun marketplace(): Response<List<MobileContentItem>>

    @GET("api/v1/mobile/marketplace/{id}")
    suspend fun getMarketplaceItem(@Path("id") id: Long): Response<MarketplaceItem>

    @GET("api/v1/mobile/rides")
    suspend fun rides(): Response<List<MobileContentItem>>

    @GET("api/v1/mobile/meetups")
    suspend fun meetups(): Response<List<MobileContentItem>>

    @GET("api/v1/mobile/notifications")
    suspend fun notifications(): Response<List<MobileContentItem>>

    @GET("api/v1/mobile/profile")
    suspend fun profile(): Response<List<MobileContentItem>>
    
    @GET("api/v1/mobile/user/me")
    suspend fun getCurrentUser(): Response<User>

    @POST("api/v1/mobile/feed")
    suspend fun createPost(@Body item: MobileContentItem): Response<MobileContentItem>

    @POST("api/v1/mobile/rides")
    suspend fun createRide(@Body ride: RideRequest): Response<MobileContentItem>

    @POST("api/v1/mobile/feed/{id}/like")
    suspend fun likeItem(@Path("id") id: Long): Response<Unit>

    @POST("api/v1/mobile/feed/{id}/comment")
    suspend fun commentItem(@Path("id") id: Long, @Body body: CommentRequest): Response<Unit>

    @POST("api/v1/mobile/feed/{id}/share")
    suspend fun shareItem(@Path("id") id: Long): Response<Unit>

    // Chat Endpoints
    @GET("api/v1/mobile/chat/messages/{recipientId}/{itemId}")
    suspend fun getChatMessages(@Path("recipientId") recipientId: Long, @Path("itemId") itemId: Long): Response<ChatPayload>

    @POST("api/v1/mobile/chat/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ChatMessage>
}
