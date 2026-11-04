package com.selavu.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.selavu.app.data.local.database.SelavuDatabase
import com.selavu.app.data.repository.ExpenseRepository
import com.selavu.app.data.repository.UserRepository
import com.selavu.app.data.security.SessionManager

class SelavuApplication : Application() {

    lateinit var database: SelavuDatabase
        private set

    lateinit var sessionManager: SessionManager
        private set

    lateinit var userRepository: UserRepository
        private set

    lateinit var expenseRepository: ExpenseRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        database = SelavuDatabase.getDatabase(this)
        sessionManager = SessionManager.getInstance(this)

        userRepository = UserRepository()
        expenseRepository = ExpenseRepository()
    }

    companion object {
        lateinit var instance: SelavuApplication
            private set
    }
}
