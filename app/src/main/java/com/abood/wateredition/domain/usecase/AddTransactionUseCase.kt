package com.abood.wateredition.domain.usecase

import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.TransactionType
import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.domain.repository.TransactionRepository
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Core business use case for all financial operations.
 * Wraps the atomic Room @Transaction (insert + balance update) via the repository.
 */
class AddTransactionUseCase @Inject constructor(
    private val calculateCost: CalculateWaterCostUseCase,
    private val updateBalance: UpdateCustomerBalanceUseCase,
    private val transactionRepository: TransactionRepository
) {
    /**
     * Records a water fill transaction.
     * @param volts must be > 0
     * @param pricePerKwh must be > 0
     */
    suspend fun addFill(
        customer: Customer,
        volts: BigDecimal,
        pricePerKwh: BigDecimal
    ) {
        val cost = calculateCost(volts, pricePerKwh)
        val transaction = WaterTransaction(
            customerId = customer.id,
            type = TransactionType.FILL,
            voltsUsed = volts,
            kwhPriceAtTime = pricePerKwh,
            finalAmount = cost,
            note = "Water fill ${volts.toPlainString()}V",
            timestamp = System.currentTimeMillis()
        )
        val updatedCustomer = updateBalance(customer, cost, TransactionType.FILL)
        transactionRepository.addTransactionAndUpdateBalance(transaction, updatedCustomer)
    }

    /**
     * Records a payment transaction.
     * @param amount must be > 0
     */
    suspend fun addPayment(
        customer: Customer,
        amount: BigDecimal
    ) {
        val transaction = WaterTransaction(
            customerId = customer.id,
            type = TransactionType.PAYMENT,
            voltsUsed = BigDecimal.ZERO,
            kwhPriceAtTime = BigDecimal.ZERO,
            finalAmount = amount,
            note = "Payment received",
            timestamp = System.currentTimeMillis()
        )
        val updatedCustomer = updateBalance(customer, amount, TransactionType.PAYMENT)
        transactionRepository.addTransactionAndUpdateBalance(transaction, updatedCustomer)
    }
}
