package com.example.androidapplicationproject.database

import kotlinx.coroutines.flow.Flow

class Repository(private val dao: LookUpDao) {

    suspend fun insertUser(user: UserTable) = dao.insertUser(user)

    suspend fun insertProperty(property: PropertyTable) = dao.insertProperty(property)

    fun loadPropertiesFrom(userId: Int): Flow<List<PropertyTable>> = dao.loadPropertiesFrom(userId)

    fun checkuserpass(userName: String, password:String) = dao.checkuserpass(userName,password)

    suspend fun deleteProperty(latitude: Double, longitude: Double) = dao.deleteProperty(latitude, longitude)
    fun loadAllLandlords() = dao.loadAllLandlords()
    fun loadusers(userId: Int) = dao.loadusers(userId)

}