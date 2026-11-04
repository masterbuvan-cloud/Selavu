package com.selavu.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.selavu.app.model.Expense
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExpenseRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    private fun getExpensesCollection(userId: String) =
        firestore.collection("users").document(userId).collection("expenses")

    /**
     * Real-time stream of expenses for the given user, ordered by date descending.
     */
    fun getAllExpenses(userId: String): Flow<List<Expense>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listenerRegistration = getExpensesCollection(userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val expenses = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Expense::class.java)?.copy(id = doc.id)
                        } catch (_: Exception) {
                            null
                        }
                    }
                    trySend(expenses)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun getExpenseById(expenseId: String, userId: String): Expense? {
        if (expenseId.isBlank() || userId.isBlank()) return null
        return try {
            val doc = getExpensesCollection(userId).document(expenseId).get().await()
            if (doc.exists()) {
                doc.toObject(Expense::class.java)?.copy(id = doc.id)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun insertExpense(expense: Expense, userId: String): String {
        require(expense.title.isNotBlank()) { "Expense title cannot be empty." }
        require(expense.amount > 0) { "Amount must be greater than zero." }
        require(userId.isNotBlank()) { "User ID is required." }

        val docRef = getExpensesCollection(userId).document()
        val expenseToSave = expense.copy(id = docRef.id, userId = userId)

        val data = hashMapOf<String, Any?>(
            "id" to docRef.id,
            "userId" to userId,
            "title" to expenseToSave.title,
            "amount" to expenseToSave.amount,
            "category" to expenseToSave.category,
            "date" to expenseToSave.date,
            "paymentMethod" to expenseToSave.paymentMethod,
            "notes" to expenseToSave.notes,
            "createdAt" to expenseToSave.createdAt,
        )

        docRef.set(data).await()
        return docRef.id
    }

    suspend fun updateExpense(expense: Expense, userId: String) {
        require(expense.title.isNotBlank()) { "Expense title cannot be empty." }
        require(expense.amount > 0) { "Amount must be greater than zero." }
        require(expense.id.isNotBlank()) { "Expense ID cannot be empty." }
        require(userId.isNotBlank()) { "User ID is required." }

        val data = hashMapOf<String, Any?>(
            "id" to expense.id,
            "userId" to userId,
            "title" to expense.title,
            "amount" to expense.amount,
            "category" to expense.category,
            "date" to expense.date,
            "paymentMethod" to expense.paymentMethod,
            "notes" to expense.notes,
            "createdAt" to expense.createdAt,
        )

        getExpensesCollection(userId).document(expense.id).set(data).await()
    }

    suspend fun deleteExpense(expenseId: String, userId: String) {
        if (expenseId.isBlank() || userId.isBlank()) return
        getExpensesCollection(userId).document(expenseId).delete().await()
    }
}
