package com.motomeet.mobile.data.model

import com.google.gson.annotations.SerializedName

data class MarketplaceItem(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("title", alternate = ["name"])
    val title: String? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("price")
    val price: Double? = null,
    
    @SerializedName("category")
    val category: String? = null,
    
    @SerializedName("imageUrl", alternate = ["image_url"])
    val imageUrl: String? = null,
    
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("createdAt", alternate = ["created_at"])
    val createdAt: String? = null,
    
    @SerializedName("updatedAt", alternate = ["updated_at"])
    val updatedAt: String? = null,

    @SerializedName("seller")
    val seller: User? = null
)
