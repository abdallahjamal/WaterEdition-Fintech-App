package com.abood.wateredition.domain.usecase

import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.TransactionType
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Pure function: updates customer balance after a transaction.
 * FILL  → increases balance (more debt)
 * PAYMENT → decreases balance (reduces debt; goes negative = credit)
 */
class UpdateCustomerBalanceUseCase @Inject constructor() {
    operator fun invoke(
        customer: Customer,
        amount: BigDecimal,
        type: TransactionType
    ): Customer {
        val newBalance = when (type) {
            TransactionType.FILL    -> customer.currentBalance + amount
            TransactionType.PAYMENT -> customer.currentBalance - amount
        }
        return customer.copy(currentBalance = newBalance)
    }
}
