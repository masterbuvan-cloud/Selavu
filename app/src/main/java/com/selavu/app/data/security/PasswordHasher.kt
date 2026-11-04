package com.selavu.app.data.security

import android.util.Base64
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val FALLBACK_ALGORITHM = "PBKDF2WithHmacSHA1"
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    /**
     * Generates a cryptographically secure random salt and hashes the password with PBKDF2.
     * Returns Pair(passwordHash, salt)
     */
    fun hashPassword(password: String): Pair<String, String> {
        val random = SecureRandom()
        val saltBytes = ByteArray(SALT_LENGTH)
        random.nextBytes(saltBytes)
        val saltString = Base64.encodeToString(saltBytes, Base64.NO_WRAP)

        val hashBytes = pbkdf2(password.toCharArray(), saltBytes)
        val hashString = Base64.encodeToString(hashBytes, Base64.NO_WRAP)

        return Pair(hashString, saltString)
    }

    /**
     * Verifies an input password against the stored PBKDF2 hash and salt.
     */
    fun verifyPassword(password: String, storedHash: String, storedSalt: String): Boolean {
        return try {
            val saltBytes = Base64.decode(storedSalt, Base64.NO_WRAP)
            val hashBytes = pbkdf2(password.toCharArray(), saltBytes)
            val computedHash = Base64.encodeToString(hashBytes, Base64.NO_WRAP)
            // Constant-time comparison to prevent timing attacks
            constantTimeEquals(computedHash, storedHash)
        } catch (e: Exception) {
            false
        }
    }

    private fun pbkdf2(password: CharArray, salt: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        val factory = try {
            SecretKeyFactory.getInstance(ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            SecretKeyFactory.getInstance(FALLBACK_ALGORITHM)
        }
        return factory.generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        val aBytes = a.toByteArray()
        val bBytes = b.toByteArray()
        if (aBytes.size != bBytes.size) return false
        var result = 0
        for (i in aBytes.indices) {
            result = result or (aBytes[i].toInt() xor bBytes[i].toInt())
        }
        return result == 0
    }
}

