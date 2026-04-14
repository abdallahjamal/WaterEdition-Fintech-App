package com.abood.wateredition.presentation.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.hilt.navigation.compose.hiltViewModel
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.presentation.ui.components.AddFillDialog
import com.abood.wateredition.presentation.ui.components.AddPaymentDialog
import com.abood.wateredition.presentation.ui.components.BalanceStatus
import com.abood.wateredition.presentation.ui.components.TransactionItem
import com.abood.wateredition.presentation.ui.components.balanceStatus
import com.abood.wateredition.presentation.ui.components.formatBalance
import com.abood.wateredition.presentation.viewmodel.CustomerDetailViewModel
import com.abood.wateredition.ui.theme.CreditGreen
import com.abood.wateredition.ui.theme.CreditGreenMuted
import com.abood.wateredition.ui.theme.CyanPrimary
import com.abood.wateredition.ui.theme.DebtRed
import com.abood.wateredition.ui.theme.DebtRedMuted
import com.abood.wateredition.ui.theme.DividerColor
import com.abood.wateredition.ui.theme.FillBlue
import com.abood.wateredition.ui.theme.NavyBackground
import com.abood.wateredition.ui.theme.NavyCard
import com.abood.wateredition.ui.theme.NavySurface
import com.abood.wateredition.ui.theme.NeutralGrey
import com.abood.wateredition.ui.theme.NeutralGreyMuted
import com.abood.wateredition.ui.theme.PaymentGold
import com.abood.wateredition.ui.theme.TextPrimary
import com.abood.wateredition.ui.theme.TextSecondary
import java.math.BigDecimal

@Composable
fun CustomerDetailScreen(
    onBack: () -> Unit,
    viewModel: CustomerDetailViewModel = hiltViewModel()
) {
    val customer by viewModel.customer.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showFillDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    val transactionToEdit by viewModel.selectedTransaction.collectAsState()
    var transactionToDelete by remember { mutableStateOf<WaterTransaction?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavySurface)
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CyanPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = customer?.name ?: "Customer",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Balance Hero Card ─────────────────────────────────────────
            item {
                customer?.let { c -> BalanceHeroCard(customer = c) }
            }

            // ── Transaction History Header ─────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction History",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${transactions.size} records",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }

            // ── Transactions ──────────────────────────────────────────────
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No transactions yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyCard)
                    ) {
                        transactions.forEachIndexed { i, tx ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        transactionToDelete = tx
                                        false // logic handled by dialog
                                    } else if (value == SwipeToDismissBoxValue.StartToEnd) {
                                        viewModel.onTransactionSelected(tx)
                                        false
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val direction = dismissState.dismissDirection
                                    val color = when (direction) {
                                        SwipeToDismissBoxValue.EndToStart -> DebtRed
                                        SwipeToDismissBoxValue.StartToEnd -> CyanPrimary
                                        else -> Color.Transparent
                                    }
                                    val icon = when (direction) {
                                        SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                                        else -> null
                                    }
                                    val alignment = when (direction) {
                                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                        else -> Alignment.Center
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = alignment
                                    ) {
                                        icon?.let { Icon(it, contentDescription = null, tint = TextPrimary) }
                                    }
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(NavyCard)
                                        .fillMaxWidth()
                                ) {
                                    TransactionItem(
                                        transaction = tx,
                                        onClick = { viewModel.onTransactionSelected(tx) }
                                    )
                                }
                            }

                            if (i < transactions.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = DividerColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Action Buttons (pinned bottom) ────────────────────────────────
        customer?.let { _ ->
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(NavySurface)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showFillDialog = true },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FillBlue),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.WaterDrop, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Fill", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showPaymentDialog = true },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PaymentGold),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Payment", fontWeight = FontWeight.Bold, color = NavyBackground)
                }
            }
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────────
    customer?.let { c ->
        if (showFillDialog) {
            AddFillDialog(
                customerName = c.name,
                onConfirm = { volts, price ->
                    viewModel.addFill(volts, price)
                    showFillDialog = false
                },
                onDismiss = { showFillDialog = false }
            )
        }
        if (showPaymentDialog) {
            AddPaymentDialog(
                customerName = c.name,
                currentBalance = c.currentBalance,
                onConfirm = { amount ->
                    viewModel.addPayment(amount)
                    showPaymentDialog = false
                },
                onDismiss = { showPaymentDialog = false }
            )
        }
    }

    // Delete Confirmation
    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction", color = TextPrimary) },
            text = { Text("Are you sure you want to delete this transaction? The customer balance will be updated automatically.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DebtRed)
                ) {
                    Text("Delete", color = TextPrimary)
                }
            },
            dismissButton = {
                Button(onClick = { transactionToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = NavySurface)) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = NavyCard
        )
    }

    // ── Edit Transaction Dialog ───────────────────────────────────────────
    transactionToEdit?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            onConfirm = { volts, price, amount ->
                // Note: The ViewModel now handles state reset (nullifying selection) 
                // ONLY after the database operation completes successfully.
                viewModel.editTransaction(tx, volts, price, amount)
            },
            onDismiss = { viewModel.onTransactionSelected(null) }
        )
    }
}

@Composable
fun EditTransactionDialog(
    transaction: WaterTransaction,
    onConfirm: (BigDecimal, BigDecimal, BigDecimal) -> Unit,
    onDismiss: () -> Unit
) {
    var volts by remember { mutableStateOf(transaction.voltsUsed.toPlainString()) }
    var price by remember { mutableStateOf(transaction.kwhPriceAtTime.toPlainString()) }
    var amount by remember { mutableStateOf(transaction.finalAmount.toPlainString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (transaction.type == com.abood.wateredition.domain.model.TransactionType.FILL) "Edit Fill" else "Edit Payment",
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (transaction.type == com.abood.wateredition.domain.model.TransactionType.FILL) {
                    OutlinedTextField(
                        value = volts,
                        onValueChange = { volts = it },
                        label = { Text("Volts") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = DividerColor
                        )
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price per KWh") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = DividerColor
                        )
                    )
                } else {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Payment Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = DividerColor
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = volts.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val p = price.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val a = amount.toBigDecimalOrNull() ?: BigDecimal.ZERO

                    if (transaction.type == com.abood.wateredition.domain.model.TransactionType.FILL) {
                        if (v > BigDecimal.ZERO && p > BigDecimal.ZERO) {
                            onConfirm(v, p, BigDecimal.ZERO)
                        }
                    } else {
                        if (a > BigDecimal.ZERO) {
                            onConfirm(BigDecimal.ZERO, BigDecimal.ZERO, a)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
            ) {
                Text("Update", color = NavyBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NavySurface)
            ) {
                Text("Cancel", color = TextPrimary)
            }
        },
        containerColor = NavyCard
    )
}

@Composable
private fun BalanceHeroCard(customer: Customer) {
    val status = customer.balanceStatus()
    val balanceColor by animateColorAsState(
        targetValue = when (status) {
            BalanceStatus.DEBT    -> DebtRed
            BalanceStatus.CREDIT -> CreditGreen
            BalanceStatus.SETTLED -> NeutralGrey
        },
        animationSpec = tween(600), label = ""
    )
    val bgColor by animateColorAsState(
        targetValue = when (status) {
            BalanceStatus.DEBT    -> DebtRedMuted
            BalanceStatus.CREDIT -> CreditGreenMuted
            BalanceStatus.SETTLED -> NeutralGreyMuted
        },
        animationSpec = tween(600), label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(NavyCard)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Current Balance", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(formatBalance(customer.currentBalance), style = MaterialTheme.typography.displayMedium, color = balanceColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bgColor).padding(horizontal = 16.dp, vertical = 6.dp)) {
            Text(
                when (status) {
                    BalanceStatus.DEBT    -> "⚠ Outstanding Debt"
                    BalanceStatus.CREDIT -> "✓ Customer has Credit"
                    BalanceStatus.SETTLED -> "✓ Fully Settled"
                },
                color = balanceColor, style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
