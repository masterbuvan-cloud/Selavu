package com.selavu.app.model

import com.google.firebase.firestore.PropertyName

data class UserProfile(
    @get:PropertyName("uid")
    @set:PropertyName("uid")
    var uid: String = "",
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",
    @get:PropertyName("age")
    @set:PropertyName("age")
    var age: Int = 0,
    @get:PropertyName("registeredAt")
    @set:PropertyName("registeredAt")
    var registeredAt: Long = System.currentTimeMillis()
)

