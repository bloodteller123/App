package com.example.androidapplicationproject.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LookUpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserTable)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyTable)

    @Query("SELECT * FROM property_table WHERE property_table.ownerId = :userId")
    fun loadPropertiesFrom(userId: Int): Flow<List<PropertyTable>>

    @Query("Select * from user_table where username = :username and password = :password")
    fun checkuserpass(username: String, password:String): List<UserTable>

    @Query("DELETE FROM property_table WHERE latitude = :latitude AND longitude =:longitude")
    suspend fun deleteProperty(latitude: Double, longitude: Double)
    @Query("SELECT * FROM user_table ORDER BY userId ASC")
    fun loadAllLandlords():  Flow<List<UserTable>>

    @Query("Select * from user_table where userId = :userId")
    fun loadusers(userId: Int): List<UserTable>
}