package com.example.androidapplicationproject

import android.app.Application
import com.example.androidapplicationproject.database.AppDatabase
import com.example.androidapplicationproject.database.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class LandlordApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { Repository(database.landlordDao()) }
}