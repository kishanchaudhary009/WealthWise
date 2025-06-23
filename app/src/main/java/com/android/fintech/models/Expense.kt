package com.android.fintech.models

import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("expenseCategory")
    val category: String,

    @SerializedName("expenseAmount")
    val amount: Float,

    @SerializedName("expenseDate")
    val date: String
)
