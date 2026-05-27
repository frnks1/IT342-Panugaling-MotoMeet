package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("content") val content: String,
    @SerializedName("sender_id") val senderId: Long? = null,
    @SerializedName("thread_id") val threadId: Long? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("sender") val sender: User? = null
)

data class SendMessageRequest(
    @SerializedName("content") val content: String,
    @SerializedName("recipient_id") val recipientId: Long? = null,
    @SerializedName("thread_id") val threadId: Long? = null,
    @SerializedName("item_id") val itemId: Long? = null
)

data class ChatThread(
    @SerializedName("id") val id: Long,
    @SerializedName("participant1") val participant1: User? = null,
    @SerializedName("participant2") val participant2: User? = null,
    @SerializedName("last_message") val lastMessage: ChatMessage? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ChatPayload(
    @SerializedName("thread_id") val threadId: Long? = null,
    @SerializedName("item_title") val itemTitle: String? = null,
    @SerializedName("opponent_id") val opponentId: Long? = null,
    @SerializedName("opponent_name") val opponentName: String? = null,
    @SerializedName("messages") val messages: List<ChatMessage> = emptyList()
)
