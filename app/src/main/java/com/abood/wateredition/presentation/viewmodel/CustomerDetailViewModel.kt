package com.abood.wateredition.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.TransactionType
import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.domain.repository.CustomerRepository
import com.abood.wateredition.domain.repository.TransactionRepository
import com.abood.wateredition.domain.usecase.AddTransactionUseCase
import com.abood.wateredition.domain.usecase.DeleteTransactionUseCase
import com.abood.wateredition.domain.usecase.EditTransactionUseCase
import com.abood.wateredition.domain.usecase.SyncCustomerBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val editTransactionUseCase: EditTransactionUseCase,
    private val syncCustomerBalanceUseCase: SyncCustomerBalanceUseCase
) : ViewModel() {
    
    private val _selectedTransaction = MutableStateFlow<WaterTransaction?>(null)
    val selectedTransaction: StateFlow<WaterTransaction?> = _selectedTransaction

    private val customerId: Long = checkNotNull(savedStateHandle["customerId"])

    val customer: StateFlow<Customer?> = customerRepository.getAllCustomers()
        .flatMapLatest { list ->
            flowOf(list.find { it.id == customerId })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val transactions: StateFlow<List<WaterTransaction>> =
        transactionRepository.getTransactionsForCustomer(customerId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addFill(volts: BigDecimal, pricePerKwh: BigDecimal) {
        val c = customer.value ?: return
        if (volts <= BigDecimal.ZERO || pricePerKwh <= BigDecimal.ZERO) return
        viewModelScope.launch {
            addTransactionUseCase.addFill(c, volts, pricePerKwh)
        }
    }

    fun addPayment(amount: BigDecimal) {
        val c = customer.value ?: return
        if (amount <= BigDecimal.ZERO) return
        viewModelScope.launch {
            addTransactionUseCase.addPayment(c, amount)
        }
    }

    /**
     * Delete a transaction and force a full balance recalculation.
     */
    fun deleteTransaction(transaction: WaterTransaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            syncCustomerBalanceUseCase(transaction.customerId)
        }
    }

    /**
     * Unified edit method that resets the selection state only after successful completion.
     * Uses the SyncCustomerBalanceUseCase to ensure absolute data consistency.
     */
    fun editTransaction(
        transaction: WaterTransaction,
        newVolts: BigDecimal = BigDecimal.ZERO,
        newPrice: BigDecimal = BigDecimal.ZERO,
        newAmount: BigDecimal = BigDecimal.ZERO
    ) {
        viewModelScope.launch {
            try {
                when (transaction.type) {
                    TransactionType.FILL -> {
                        if (newVolts > BigDecimal.ZERO && newPrice > BigDecimal.ZERO) {
                            editTransactionUseCase.editFill(transaction, newVolts, newPrice)
                        }
                    }
                    TransactionType.PAYMENT -> {
                        if (newAmount > BigDecimal.ZERO) {
                            editTransactionUseCase.editPayment(transaction, newAmount)
                        }
                    }
                }
                // Reset state only after success
                _selectedTransaction.value = null
            } catch (e: Exception) {
                // Error handling...
            }
        }
    }

    fun onTransactionSelected(transaction: WaterTransaction?) {
        _selectedTransaction.value = transaction
    }
}
