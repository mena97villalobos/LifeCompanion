package com.mena97villalobos.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mena97villalobos.local.entities.WarrantyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarrantyDao {

    @Query("SELECT * FROM warranties ORDER BY expiryDate ASC")
    fun getAll(): Flow<List<WarrantyEntity>>

    @Insert
    suspend fun insert(entity: WarrantyEntity)

    @Update
    suspend fun update(entity: WarrantyEntity)

    @Query("DELETE FROM warranties WHERE id = :id")
    suspend fun delete(id: Long)
}
