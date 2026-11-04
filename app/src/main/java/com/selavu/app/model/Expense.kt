package com.selavu.app.model

import com.google.firebase.firestore.PropertyName

data class Expense(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("amount")
    @set:PropertyName("amount")
    var amount: Double = 0.0,
    @get:PropertyName("category")
    @set:PropertyName("category")
    var category: String = "FOOD",
    @get:PropertyName("date")
    @set:PropertyName("date")
    var date: Long = System.currentTimeMillis(),
    @get:PropertyName("paymentMethod")
    @set:PropertyName("paymentMethod")
    var paymentMethod: String = "CASH",
    @get:PropertyName("notes")
    @set:PropertyName("notes")
    var notes: String? = null,
    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis(),
)
