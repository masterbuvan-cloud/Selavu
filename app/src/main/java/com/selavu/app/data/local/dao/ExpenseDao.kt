package com.selavu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.selavu.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id AND user_id = :userId")
    suspend fun deleteExpenseById(id: Long, userId: Long)

    @Query("SELECT * FROM expenses WHERE id = :id AND user_id = :userId LIMIT 1")
    suspend fun getExpenseById(id: Long, userId: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE user_id = :userId ORDER BY date DESC, id DESC")
    fun getAllExpenses(userId: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE user_id = :userId AND date >= :startTime AND date <= :endTime ORDER BY date DESC, id DESC")
    fun getExpensesInRange(userId: Long, startTime: Long, endTime: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE user_id = :userId AND date >= :startTime AND date <= :endTime")
    fun getTotalExpenseInRange(userId: Long, startTime: Long, endTime: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM expenses WHERE user_id = :userId")
    fun getTotalTransactionCount(userId: Long): Flow<Int>
}

