package com.abood.wateredition.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abood.wateredition.domain.model.Customer
import com.abood.wateredition.ui.theme.CreditGreen
import com.abood.wateredition.ui.theme.CreditGreenMuted
import com.abood.wateredition.ui.theme.DebtRed
import com.abood.wateredition.ui.theme.DebtRedMuted
import com.abood.wateredition.ui.theme.NavyCard
import com.abood.wateredition.ui.theme.NavyCardBorder
import com.abood.wateredition.ui.theme.NeutralGrey
import com.abood.wateredition.ui.theme.NeutralGreyMuted
import com.abood.wateredition.ui.theme.TextPrimary
import com.abood.wateredition.ui.theme.TextSecondary
import java.math.BigDecimal
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
enum class BalanceStatus { DEBT, CREDIT, SETTLED }

fun Customer.balanceStatus(): BalanceStatus = when {
    currentBalance > BigDecimal.ZERO -> BalanceStatus.DEBT
    currentBalance < BigDecimal.ZERO -> BalanceStatus.CREDIT
    else -> BalanceStatus.SETTLED
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomerCard(
    customer: Customer,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val status = customer.balanceStatus()

    val balanceColor by animateColorAsState(
        targetValue = when (status) {
            BalanceStatus.DEBT    -> DebtRed
            BalanceStatus.CREDIT  -> CreditGreen
            BalanceStatus.SETTLED -> NeutralGrey
        },
        animationSpec = tween(durationMillis = 400),
        label = "balanceColor"
    )

    val badgeBg by animateColorAsState(
        targetValue = when (status) {
            BalanceStatus.DEBT    -> DebtRedMuted
            BalanceStatus.CREDIT  -> CreditGreenMuted
            BalanceStatus.SETTLED -> NeutralGreyMuted
        },
        animationSpec = tween(durationMillis = 400),
        label = "badgeBg"
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(balanceColor.copy(alpha = 0.4f), NavyCardBorder)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(width = 1.dp, brush = borderBrush, shape = RoundedCornerShape(16.dp))
            .background(NavyCard)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(balanceColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = balanceColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Name + phone
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                if (!customer.phone.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = customer.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // Balance badge
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = formatBalance(customer.currentBalance),
                        color = balanceColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (status) {
                        BalanceStatus.DEBT    -> "Debt"
                        BalanceStatus.CREDIT  -> "Credit"
                        BalanceStatus.SETTLED -> "Settled"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = balanceColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun formatBalance(balance: BigDecimal): String {
    val abs = balance.abs().setScale(2, java.math.RoundingMode.HALF_UP)
    return if (balance < BigDecimal.ZERO) "-₪$abs" else "₪$abs"
}
