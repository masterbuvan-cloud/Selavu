package com.selavu.app

import com.selavu.app.data.security.PasswordHasher
import com.selavu.app.model.ExpenseCategory
import com.selavu.app.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityAndModelUnitTest {

    @Test
    fun testPasswordHashingAndVerification() {
        val password = "StrongPassword@123"
        val (hash1, salt1) = PasswordHasher.hashPassword(password)
        val (hash2, salt2) = PasswordHasher.hashPassword(password)

        // Salts must be cryptographically unique
        assertNotEquals(salt1, salt2)
        assertNotEquals(hash1, hash2)

        // Verification must succeed with correct password
        assertTrue(PasswordHasher.verifyPassword(password, hash1, salt1))
        assertTrue(PasswordHasher.verifyPassword(password, hash2, salt2))

        // Verification must fail with incorrect password
        assertFalse(PasswordHasher.verifyPassword("WrongPassword!", hash1, salt1))
    }

    @Test
    fun testCategoryResolution() {
        assertEquals(ExpenseCategory.FOOD, ExpenseCategory.fromName("FOOD"))
        assertEquals(ExpenseCategory.FOOD, ExpenseCategory.fromName("Food"))
        assertEquals(ExpenseCategory.TRANSPORTATION, ExpenseCategory.fromName("Transportation"))
        assertEquals(ExpenseCategory.OTHERS, ExpenseCategory.fromName("UnknownCategory"))
    }

    @Test
    fun testPaymentMethodResolution() {
        assertEquals(PaymentMethod.CASH, PaymentMethod.fromName("CASH"))
        assertEquals(PaymentMethod.UPI, PaymentMethod.fromName("UPI"))
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.fromName("Debit Card"))
        assertEquals(PaymentMethod.CASH, PaymentMethod.fromName("NonExistent"))
    }

    @Test
    fun testExpenseModelDefaults() {
        val expense = com.selavu.app.model.Expense(
            id = "exp123",
            userId = "userABC",
            title = "Lunch",
            amount = 150.0,
            category = "FOOD"
        )
        assertEquals("exp123", expense.id)
        assertEquals("userABC", expense.userId)
        assertEquals("Lunch", expense.title)
        assertEquals(150.0, expense.amount, 0.0)
    }
}

