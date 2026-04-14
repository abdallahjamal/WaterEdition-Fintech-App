package com.abood.wateredition.domain.usecase

import com.abood.wateredition.domain.model.TransactionType
import com.abood.wateredition.domain.repository.CustomerRepository
import com.abood.wateredition.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Single Source of Truth: Recalculates the customer balance by summing all 
 * transactions in the database. 
 * Balance = Sum(FILL.amount) - Sum(PAYMENT.amount)
 */
class SyncCustomerBalanceUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: Long) {
        // 1. Fetch all transactions for this customer
        // We use .first() to get the current snapshot from the Flow
        val transactions = transactionRepository.getTransactionsForCustomer(customerId).first()
        
        // 2. Calculate the theoretical balance
        var calculatedBalance = BigDecimal.ZERO
        for (tx in transactions) {
            if (tx.type == TransactionType.FILL) {
                // FILL adds to the debt (positive balance)
                calculatedBalance = calculatedBalance.add(tx.finalAmount)
            } else {
                // PAYMENT reduces the debt (negative balance)
                calculatedBalance = calculatedBalance.subtract(tx.finalAmount)
            }
        }
        
        // 3. Fetch the customer from repository
        val customer = customerRepository.getCustomerById(customerId) ?: return
        
        // 4. Atomically update the customer with the re-calculated balance
        customerRepository.upsertCustomer(
            customer.copy(currentBalance = calculatedBalance)
        )
    }
}
