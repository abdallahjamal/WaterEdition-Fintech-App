package com.abood.wateredition.data.repository

import androidx.room.withTransaction
import com.abood.wateredition.data.local.WaterDatabase
import com.abood.wateredition.data.local.dao.CustomerDao
import com.abood.wateredition.data.local.dao.WaterTransactionDao
import com.abood.wateredition.data.local.entity.CustomerEntity
import com.abood.wateredition.data.local.entity.WaterTransactionEntity
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val database: WaterDatabase,
    private val transactionDao: WaterTransactionDao,
    private val customerDao: CustomerDao
) : TransactionRepository {

    override fun getTransactionsForCustomer(customerId: Long): Flow<List<WaterTransaction>> =
        transactionDao.getTransactionsForCustomer(customerId)
            .map { list -> list.map { it.toDomain() } }

    /**
     * Atomically inserts the transaction and updates the customer balance
     * inside a Room @Transaction — no partial writes possible.
     */
    override suspend fun addTransactionAndUpdateBalance(
        transaction: WaterTransaction,
        updatedCustomer: Customer
    ) {
        database.withTransaction {
            transactionDao.insert(WaterTransactionEntity.fromDomain(transaction))
            customerDao.upsert(CustomerEntity.fromDomain(updatedCustomer))
        }
    }

    override suspend fun deleteTransactionAndUpdateBalance(
        transaction: WaterTransaction,
        updatedCustomer: Customer
    ) {
        database.withTransaction {
            transactionDao.delete(WaterTransactionEntity.fromDomain(transaction))
            customerDao.upsert(CustomerEntity.fromDomain(updatedCustomer))
        }
    }

    override suspend fun updateTransactionAndUpdateBalance(
        transaction: WaterTransaction,
        updatedCustomer: Customer
    ) {
        database.withTransaction {
            transactionDao.update(WaterTransactionEntity.fromDomain(transaction))
            customerDao.upsert(CustomerEntity.fromDomain(updatedCustomer))
        }
    }

    override suspend fun deleteTransaction(transaction: WaterTransaction) {
        transactionDao.delete(WaterTransactionEntity.fromDomain(transaction))
    }

    override suspend fun updateTransaction(transaction: WaterTransaction) {
        transactionDao.update(WaterTransactionEntity.fromDomain(transaction))
    }
}
