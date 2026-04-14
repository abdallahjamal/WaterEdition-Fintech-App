package com.abood.wateredition.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.presentation.ui.components.AddCustomerDialog
import com.abood.wateredition.presentation.ui.components.CustomerCard
import com.abood.wateredition.presentation.ui.components.dialogTextFieldColors
import com.abood.wateredition.presentation.viewmodel.CustomerListViewModel
import com.abood.wateredition.presentation.ui.components.formatBalance
import com.abood.wateredition.ui.theme.CreditGreen
import com.abood.wateredition.ui.theme.DebtRed
import com.abood.wateredition.ui.theme.CyanPrimary
import com.abood.wateredition.ui.theme.NavyBackground
import com.abood.wateredition.ui.theme.NavyCard
import com.abood.wateredition.ui.theme.NavySurface
import com.abood.wateredition.ui.theme.TextPrimary
import com.abood.wateredition.ui.theme.TextSecondary
import java.math.BigDecimal

@Composable
fun CustomerListScreen(
    onCustomerClick: (Long) -> Unit,
    viewModel: CustomerListViewModel = hiltViewModel()
) {
    val customers by viewModel.customers.collectAsState()
    val totalDebts by viewModel.totalDebts.collectAsState()
    val totalCredits by viewModel.totalCredits.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Header ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavySurface)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "💧 Water Edition",
                            style = MaterialTheme.typography.headlineMedium,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${customers.size} customer${if (customers.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Debt Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyCard)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Total Debts",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatBalance(totalDebts),
                                style = MaterialTheme.typography.titleMedium,
                                color = DebtRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Credit Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyCard)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Total Credits",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatBalance(totalCredits),
                                style = MaterialTheme.typography.titleMedium,
                                color = CreditGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Search customers…", color = TextSecondary) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = dialogTextFieldColors().copy(
                        unfocusedContainerColor = NavyCard,
                        focusedContainerColor = NavyCard
                    )
                )
            }

            // ── Customer List ─────────────────────────────────────────────
            if (customers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💧", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "No customers yet" else "No results found",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        if (searchQuery.isBlank()) {
                            Text(
                                text = "Tap + to add your first customer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        items = customers,
                        key = { _, c -> c.id }
                    ) { index, customer ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                        ) {
                            CustomerCard(
                                customer = customer,
                                onClick = { onCustomerClick(customer.id) },
                                onLongClick = { customerToDelete = customer }
                            )
                        }
                    }
                }
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = CircleShape,
            containerColor = CyanPrimary,
            contentColor = NavyBackground,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Customer",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(
            onConfirm = { name, phone ->
                viewModel.addCustomer(name, phone)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if (customerToDelete != null) {
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Delete Customer", color = TextPrimary) },
            text = { Text("Are you sure you want to delete ${customerToDelete?.name}? This will permanently remove all their transaction history.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        customerToDelete?.let { viewModel.deleteCustomer(it) }
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = com.abood.wateredition.ui.theme.DebtRed)
                ) {
                    Text("Delete", color = TextPrimary)
                }
            },
            dismissButton = {
                Button(
                    onClick = { customerToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = NavyCard
        )
    }
}
