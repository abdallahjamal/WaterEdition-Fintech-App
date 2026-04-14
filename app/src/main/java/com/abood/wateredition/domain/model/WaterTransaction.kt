package com.abood.wateredition.domain.model

import java.math.BigDecimal

data class WaterTransaction(
    val id: Long = 0,
    val customerId: Long,
    val type: TransactionType,
    val voltsUsed: BigDecimal,      // 0 if PAYMENT
    val kwhPriceAtTime: BigDecimal, // 0 if PAYMENT
    val finalAmount: BigDecimal,
    val note: String? = null,
    val timestamp: Long
)
