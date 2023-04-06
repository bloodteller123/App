package com.example.androidapplicationproject.database

import androidx.room.*

data class PropertyLookUp(

    @Embedded val user: UserTable,
    @Embedded val property: PropertyTable
//    @Relation(
//        parentColumn = "userId",
//        entityColumn = "ownerId"
//    )
//    val properties: List<Property>
)