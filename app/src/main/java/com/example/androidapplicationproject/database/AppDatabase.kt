package com.example.androidapplicationproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserTable::class, PropertyTable::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun landlordDao(): LookUpDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appDatabase.db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}