package com.abood.wateredition.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abood.wateredition.data.local.entity.WaterTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterTransactionDao {

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Long): Flow<List<WaterTransactionEntity>>

    @Insert
    suspend fun insert(transaction: WaterTransactionEntity): Long

    @Update
    suspend fun update(transaction: WaterTransactionEntity)

    @Delete
    suspend fun delete(transaction: WaterTransactionEntity)
}
