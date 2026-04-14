package com.abood.wateredition.di

import com.abood.wateredition.data.repository.CustomerRepositoryImpl
import com.abood.wateredition.data.repository.TransactionRepositoryImpl
import com.abood.wateredition.domain.repository.CustomerRepository
import com.abood.wateredition.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        impl: CustomerRepositoryImpl
    ): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository
}
