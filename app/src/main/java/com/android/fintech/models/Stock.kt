package com.android.fintech.models

import com.google.gson.annotations.SerializedName

data class Stock(
    @SerializedName("companyName")
    val name: String,

    @SerializedName("tickerId")
    val ticker: String,

    @SerializedName("stockBuyPrice")
    val buyPrice: Double,

    // This value is not provided in the response, so set a default
    val currentPrice: Double = 0.0,

    @SerializedName("stockQuantity")
    val quantity: Int,

    @SerializedName("companyLogoURL")
    val stockimgurl: String
) {
    val totalValue: Double
        get() = currentPrice * quantity
}
