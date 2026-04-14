package com.abood.wateredition.domain.repository

import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.WaterTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactionsForCustomer(customerId: Long): Flow<List<WaterTransaction>>

    /**
     * Atomically inserts the transaction AND updates the customer balance in a single
     * Room @Transaction, ensuring no partial writes.
     */
    suspend fun addTransactionAndUpdateBalance(
        transaction: WaterTransaction,
        updatedCustomer: Customer
    )

    suspend fun deleteTransactionAndUpdateBalance(
        transaction: WaterTransaction,
        updatedCustomer: Customer
    )

    suspend fun updateTransactionAndUpdateBalance(
        transaction: WaterTransaction,
        updatedCustomer: Customer
    )

    // Basic transaction methods for use with external synchronization logic
    suspend fun deleteTransaction(transaction: WaterTransaction)
    suspend fun updateTransaction(transaction: WaterTransaction)
}
