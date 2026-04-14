package com.abood.wateredition.domain.repository

import com.abood.wateredition.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getAllCustomers(): Flow<List<Customer>>
    suspend fun getCustomerById(id: Long): Customer?
    suspend fun upsertCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
}
