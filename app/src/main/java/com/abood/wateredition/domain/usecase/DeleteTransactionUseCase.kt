package com.abood.wateredition.domain.usecase

import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.TransactionType
import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: WaterTransaction, customer: Customer) {
        // Revert balance impact:
        // If it was a FILL (Debt), removing it reduces debt (-amount)
        // If it was a PAYMENT (Credit), removing it increases debt (+amount)
        val newBalance = when (transaction.type) {
            TransactionType.FILL -> customer.currentBalance.subtract(transaction.finalAmount)
            TransactionType.PAYMENT -> customer.currentBalance.add(transaction.finalAmount)
        }
        
        val updatedCustomer = customer.copy(currentBalance = newBalance)
        repository.deleteTransactionAndUpdateBalance(transaction, updatedCustomer)
    }
}
