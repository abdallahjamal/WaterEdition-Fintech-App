package com.abood.wateredition.data.repository

import com.abood.wateredition.data.local.dao.CustomerDao
import com.abood.wateredition.data.local.entity.CustomerEntity
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao
) : CustomerRepository {

    override fun getAllCustomers(): Flow<List<Customer>> =
        customerDao.getAllCustomers().map { list -> list.map { it.toDomain() } }

    override suspend fun getCustomerById(id: Long): Customer? =
        customerDao.getCustomerById(id)?.toDomain()

    override suspend fun upsertCustomer(customer: Customer) =
        customerDao.upsert(CustomerEntity.fromDomain(customer))

    override suspend fun deleteCustomer(customer: Customer) =
        customerDao.delete(CustomerEntity.fromDomain(customer))
}
