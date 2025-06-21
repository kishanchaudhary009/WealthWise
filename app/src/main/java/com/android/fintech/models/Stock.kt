package com.android.fintech.models

data class Stock(
    val name: String,
    val ticker: String,
    val buyPrice: Double,
    val currentPrice: Double,
    val quantity: Int,
    val stockimgurl: String
){
    val totalValue: Double
        get() = currentPrice * quantity
}