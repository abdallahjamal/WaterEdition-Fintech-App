package com.abood.wateredition.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abood.wateredition.domain.model.Customer
import java.math.BigDecimal

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val currentBalance: BigDecimal, // stored as String via TypeConverter
    val createdAt: Long
) {
    fun toDomain(): Customer = Customer(
        id = id,
        name = name,
        phone = phone,
        currentBalance = currentBalance,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(customer: Customer) = CustomerEntity(
            id = customer.id,
            name = customer.name,
            phone = customer.phone,
            currentBalance = customer.currentBalance,
            createdAt = customer.createdAt
        )
    }
}
