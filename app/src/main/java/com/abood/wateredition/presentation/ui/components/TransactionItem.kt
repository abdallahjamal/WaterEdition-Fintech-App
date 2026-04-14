package com.abood.wateredition.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abood.wateredition.domain.model.TransactionType
import com.abood.wateredition.domain.model.WaterTransaction
import com.abood.wateredition.ui.theme.CreditGreen
import com.abood.wateredition.ui.theme.DebtRed
import com.abood.wateredition.ui.theme.DividerColor
import com.abood.wateredition.ui.theme.FillBlue
import com.abood.wateredition.ui.theme.NavyCard
import com.abood.wateredition.ui.theme.PaymentGold
import com.abood.wateredition.ui.theme.TextPrimary
import com.abood.wateredition.ui.theme.TextSecondary
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ripple.rememberRipple

@Composable
fun TransactionItem(
    transaction: WaterTransaction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isFill = transaction.type == TransactionType.FILL
    val iconColor = if (isFill) FillBlue else PaymentGold
    val amountColor = if (isFill) DebtRed else CreditGreen
    val amountPrefix = if (isFill) "+" else "-"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFill) Icons.Default.WaterDrop else Icons.Default.AttachMoney,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isFill) "Water Fill" else "Payment",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (isFill) {
                val kwh = transaction.voltsUsed.divide(BigDecimal("10"), 2, RoundingMode.HALF_UP)
                Text(
                    text = "${transaction.voltsUsed.toPlainString()}V → ${kwh}kWh · ₪${transaction.kwhPriceAtTime.toPlainString()}/kWh",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = transaction.note ?: "Payment received",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatTimestamp(transaction.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.6f)
            )
        }

        // Amount
        Text(
            text = "$amountPrefix₪${transaction.finalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()}",
            style = MaterialTheme.typography.titleMedium,
            color = amountColor,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatTimestamp(ts: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(ts))
}
