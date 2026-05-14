package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminEntity): Long

    @Update
    suspend fun updateAdmin(admin: AdminEntity)

    @Query("SELECT COUNT(*) FROM admins")
    fun getAdminCount(): Flow<Int>

    @Query("SELECT * FROM admins ORDER BY id ASC LIMIT 1")
    fun getFirstAdmin(): Flow<AdminEntity?>

    @Query("SELECT * FROM admins WHERE phoneNumber = :phoneNumber AND password = :password LIMIT 1")
    suspend fun getAdminByCredentials(
        phoneNumber: String,
        password: String
    ): AdminEntity?
}
