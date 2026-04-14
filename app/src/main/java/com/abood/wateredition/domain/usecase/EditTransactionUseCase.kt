package com.abood.wateredition.domain.usecase

import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.domain.repository.TransactionRepository
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Refactored EditTransactionUseCase: No longer performs manual balance calculations.
 * It updates the transaction record and delegates the balance correction to 
 * the SyncCustomerBalanceUseCase for absolute consistency.
 */
class EditTransactionUseCase @Inject constructor(
    private val calculateCost: CalculateWaterCostUseCase,
    private val transactionRepository: TransactionRepository,
    private val syncCustomerBalanceUseCase: SyncCustomerBalanceUseCase
) {
    suspend fun editFill(
        oldTx: WaterTransaction,
        newVolts: BigDecimal,
        newPrice: BigDecimal
    ) {
        val newAmount = calculateCost(newVolts, newPrice)
        
        val updatedTx = oldTx.copy(
            voltsUsed = newVolts,
            kwhPriceAtTime = newPrice,
            finalAmount = newAmount,
            note = "Edited: Water fill ${newVolts.toPlainString()}V"
        )
        
        // 1. Update the transaction record only
        transactionRepository.updateTransaction(updatedTx)
        
        // 2. Forced Recalculation: Sync the total balance from all transactions
        syncCustomerBalanceUseCase(oldTx.customerId)
    }

    suspend fun editPayment(
        oldTx: WaterTransaction,
        newAmount: BigDecimal
    ) {
        val updatedTx = oldTx.copy(
            finalAmount = newAmount,
            note = "Edited: Payment received"
        )
        
        // 1. Update the transaction record only
        transactionRepository.updateTransaction(updatedTx)
        
        // 2. Forced Recalculation: Sync the total balance from all transactions
        syncCustomerBalanceUseCase(oldTx.customerId)
    }
}
