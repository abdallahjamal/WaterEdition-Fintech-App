package com.abood.wateredition.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val customers: StateFlow<List<Customer>> = combine(
        customerRepository.getAllCustomers(),
        searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }.stateIn( // تأكد أن القوس هنا يغلق الـ combine بشكل صحيح
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val totalDebts: StateFlow<BigDecimal> = customers.map { list ->
        list.filter { it.currentBalance > BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { acc, c -> acc + c.currentBalance }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BigDecimal.ZERO
    )

    val totalCredits: StateFlow<BigDecimal> = customers.map { list ->
        list.filter { it.currentBalance < BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { acc, c -> acc + c.currentBalance.abs() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BigDecimal.ZERO
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun addCustomer(name: String, phone: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            customerRepository.upsertCustomer(
                Customer(
                    name = name.trim(),
                    phone = phone?.trim()?.ifBlank { null },
                    currentBalance = BigDecimal.ZERO,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            customerRepository.deleteCustomer(customer)
        }
    }
}
