package com.android.fintech.models

data class User(
    var id : String,
    var portfolio : Array<Stock>,
    var expenses : Array<Expense>
)
