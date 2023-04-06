package com.example.androidapplicationproject.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "property_table",
        foreignKeys = [
            ForeignKey( entity = UserTable::class,
                parentColumns = ["userId"],
                childColumns = ["ownerId"])
        ])
data class PropertyTable(

    val ownerId: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val country: String,
    val postCode: String,
    val description: String,
    val price: Int,
    val name: String,

    @PrimaryKey(autoGenerate = true)
    val propertyId: Int = 0,
)