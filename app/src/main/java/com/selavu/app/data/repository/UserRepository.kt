package com.selavu.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.selavu.app.model.UserProfile
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getLoggedInUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun getLoggedInUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                doc.toObject(UserProfile::class.java)?.copy(uid = uid)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(
        email: String,
        ageStr: String,
        password: String,
        confirmPass: String
    ): Result<UserProfile> {
        val trimmedEmail = email.trim()
        val trimmedAge = ageStr.trim()

        if (trimmedEmail.isEmpty()) {
            return Result.failure(IllegalArgumentException("Email ID is required."))
        }
        if (!emailPattern.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (trimmedAge.isEmpty()) {
            return Result.failure(IllegalArgumentException("Age is required."))
        }
        val age = trimmedAge.toIntOrNull()
        if (age == null || age < 1 || age > 120) {
            return Result.failure(IllegalArgumentException("Please enter a valid age between 1 and 120."))
        }
        if (password.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters long."))
        }
        if (password != confirmPass) {
            return Result.failure(IllegalArgumentException("Passwords do not match."))
        }

        return try {
            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val user = authResult.user
                ?: return Result.failure(IllegalStateException("User registration failed."))

            val profile = UserProfile(
                uid = user.uid,
                email = trimmedEmail,
                age = age,
                registeredAt = System.currentTimeMillis()
            )

            // Save user profile under users/{uid} in Cloud Firestore
            val userData = hashMapOf<String, Any>(
                "uid" to profile.uid,
                "email" to profile.email,
                "age" to profile.age,
                "registeredAt" to profile.registeredAt
            )
            firestore.collection("users")
                .document(user.uid)
                .set(userData)
                .await()

            Result.success(profile)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(IllegalArgumentException("The password provided is too weak."))
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(IllegalStateException("An account with this email already exists."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(IllegalArgumentException("Invalid email format."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Registration failed. Please try again."))
        }
    }

    suspend fun login(email: String, password: String): Result<UserProfile> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            return Result.failure(IllegalArgumentException("Email ID is required."))
        }
        if (password.isEmpty()) {
            return Result.failure(IllegalArgumentException("Password is required."))
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(trimmedEmail, password).await()
            val user = authResult.user
                ?: return Result.failure(IllegalStateException("Login failed."))

            val profile = getUserProfile(user.uid) ?: UserProfile(
                uid = user.uid,
                email = user.email ?: trimmedEmail,
                age = 0,
                registeredAt = System.currentTimeMillis()
            )

            Result.success(profile)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(IllegalArgumentException("Invalid email or password."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Login failed. Please check your credentials."))
        }
    }

    fun logout() {
        auth.signOut()
    }
}
