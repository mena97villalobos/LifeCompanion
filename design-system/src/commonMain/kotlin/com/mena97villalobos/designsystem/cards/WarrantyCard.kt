package com.mena97villalobos.designsystem.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mena97villalobos.designsystem.theme.NegativeColor
import com.mena97villalobos.designsystem.theme.PositiveColor
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens
import com.mena97villalobos.domain.model.Warranty

@Composable
fun WarrantyCard(
    warranty: Warranty,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetColor = if (warranty.isExpired.not()) PositiveColor else NegativeColor
    val trendColor by animateColorAsState(targetColor)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignSystemDimens.ExchangeRateCardElevation,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystemDimens.Margin2x),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = warranty.description,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = warranty.storeName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDelete) {
                    Text(
                        text = "Delete",
                        color = NegativeColor,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(DesignSystemDimens.Radius40),
                    color = trendColor.copy(alpha = 0.12f),
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = DesignSystemDimens.Margin,
                            vertical = DesignSystemDimens.MarginHalf,
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Expires: ${warranty.expiryDate}",
                            color = trendColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
