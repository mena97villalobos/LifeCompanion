package com.mena97villalobos.designsystem.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mena97villalobos.designsystem.progress.WarrantyHealthProgressBar
import com.mena97villalobos.designsystem.theme.NegativeColor
import com.mena97villalobos.designsystem.tokens.DesignSystemDimens
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Composable
fun WarrantyCard(
    description: String,
    purchaseDate: LocalDate,
    expiryDate: LocalDate,
    imageUrl: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysUntilExpiry = today.daysUntil(expiryDate)
    val isNearExpiry = daysUntilExpiry <= 30

    val targetColor = if (isNearExpiry) NegativeColor else MaterialTheme.colorScheme.primary
    val trendColor by animateColorAsState(targetColor)

    Card(
        modifier = modifier
            .fillMaxWidth(),
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                var expanded by remember { mutableStateOf(false) }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                    )

                    Spacer(modifier = Modifier.padding(top = DesignSystemDimens.MarginHalf))

                    Text(
                        text = "Purchased: $purchaseDate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = { expanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Warranty actions",
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                expanded = false
                                onEdit()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expanded = false
                                onDelete()
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = DesignSystemDimens.Margin))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystemDimens.Margin),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(DesignSystemDimens.Radius12)),
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(DesignSystemDimens.Radius12),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    ) {}
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(DesignSystemDimens.MarginHalf),
                ) {
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
                                text = "Expires: $expiryDate",
                                color = trendColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    WarrantyHealthProgressBar(
                        purchaseDate = purchaseDate,
                        expiryDate = expiryDate,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewWarrantyCard() = MaterialTheme {
    val previewToday = Clock.System.todayIn(TimeZone.currentSystemDefault())
    // Avoid LocalDate arithmetic helpers for preview stability across kotlinx-datetime versions.
    val purchaseDate = LocalDate(2020, 1, 1)
    val expiresSoon = previewToday // <= 30 days -> red (0 days remaining)
    val expiresLater = LocalDate(2099, 1, 1) // always "healthy" for the preview
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        WarrantyCard(
            description = "Laptop Warranty",
            purchaseDate = purchaseDate,
            expiryDate = expiresSoon,
            imageUrl = null,
            onEdit = {},
            onDelete = {},
        )

        WarrantyCard(
            description = "Laptop Warranty",
            purchaseDate = purchaseDate,
            expiryDate = expiresLater,
            imageUrl = null,
            onEdit = {},
            onDelete = {},
        )
    }
}
