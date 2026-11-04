package com.selavu.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.selavu.app.data.local.dao.ExpenseDao
import com.selavu.app.data.local.dao.UserDao
import com.selavu.app.data.local.entity.ExpenseEntity
import com.selavu.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SelavuDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: SelavuDatabase? = null

        fun getDatabase(context: Context): SelavuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SelavuDatabase::class.java,
                    "selavu_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

