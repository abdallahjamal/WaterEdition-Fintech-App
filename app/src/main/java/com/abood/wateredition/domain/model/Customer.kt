package com.abood.wateredition.domain.model

import java.math.BigDecimal

data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val currentBalance: BigDecimal, // +Debt / -Credit
    val createdAt: Long
)
