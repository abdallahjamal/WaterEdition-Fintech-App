package com.abood.wateredition.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.abood.wateredition.ui.theme.CyanPrimary
import com.abood.wateredition.ui.theme.DebtRed
import com.abood.wateredition.ui.theme.FillBlue
import com.abood.wateredition.ui.theme.NavyCard
import com.abood.wateredition.ui.theme.NavyCardBorder
import com.abood.wateredition.ui.theme.NavySurface
import com.abood.wateredition.ui.theme.TextPrimary
import com.abood.wateredition.ui.theme.TextSecondary
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun AddFillDialog(
    customerName: String,
    onConfirm: (volts: BigDecimal, pricePerKwh: BigDecimal) -> Unit,
    onDismiss: () -> Unit
) {
    var voltsInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }

    val volts by remember { derivedStateOf { voltsInput.toBigDecimalOrNull() } }
    val price by remember { derivedStateOf { priceInput.toBigDecimalOrNull() } }

    val kwh by remember {
        derivedStateOf {
            volts?.let { v ->
                if (v > BigDecimal.ZERO)
                    v.divide(BigDecimal("10"), 2, RoundingMode.HALF_UP)
                else null
            }
        }
    }
    val cost by remember {
        derivedStateOf {
            if (kwh != null && price != null && price!! > BigDecimal.ZERO)
                (kwh!! * price!!).setScale(2, RoundingMode.HALF_UP)
            else null
        }
    }

    val isValid = volts != null && volts!! > BigDecimal.ZERO &&
            price != null && price!! > BigDecimal.ZERO
    val voltsError = voltsInput.isNotBlank() && (volts == null || volts!! <= BigDecimal.ZERO)
    val priceError = priceInput.isNotBlank() && (price == null || price!! <= BigDecimal.ZERO)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Title
                Text(
                    text = "Add Water Fill",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Customer: $customerName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Volts input
                OutlinedTextField(
                    value = voltsInput,
                    onValueChange = { voltsInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Volts Used") },
                    suffix = { Text("V", color = TextSecondary) },
                    isError = voltsError,
                    supportingText = if (voltsError) ({ Text("Enter a value > 0", color = DebtRed) }) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogTextFieldColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Price input
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Price per kWh") },
                    prefix = { Text("₪ ", color = TextSecondary) },
                    isError = priceError,
                    supportingText = if (priceError) ({ Text("Enter a value > 0", color = DebtRed) }) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = dialogTextFieldColors()
                )

                // Live preview panel
                AnimatedVisibility(
                    visible = cost != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (kwh != null && cost != null) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyCard, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    PreviewRow(label = "kWh consumed", value = "${kwh}kWh")
                                    HorizontalDivider(color = NavyCardBorder)
                                    PreviewRow(
                                        label = "Total cost",
                                        value = "₪$cost",
                                        valueColor = FillBlue,
                                        bold = true
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            if (isValid) onConfirm(volts!!, price!!)
                        },
                        enabled = isValid,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", color = NavySurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun dialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanPrimary,
    unfocusedBorderColor = NavyCardBorder,
    focusedLabelColor = CyanPrimary,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = CyanPrimary,
    errorBorderColor = DebtRed,
    errorLabelColor = DebtRed
)
