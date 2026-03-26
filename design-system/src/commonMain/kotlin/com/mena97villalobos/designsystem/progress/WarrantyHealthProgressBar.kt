package com.mena97villalobos.designsystem.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.PreviewDesignSystem
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme
import com.mena97villalobos.designsystem.theme.NegativeColor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private const val DefaultNearExpiryDays: Int = 30

/**
 * Visualizes how "close" a warranty is to its expiry date.
 *
 * - When expiry is within [nearExpiryDays], the bar turns red.
 * - Otherwise it uses the app primary color.
 */
@Composable
fun WarrantyHealthProgressBar(
    purchaseDate: LocalDate,
    expiryDate: LocalDate,
    modifier: Modifier = Modifier,
    nearExpiryDays: Int = DefaultNearExpiryDays,
    barHeight: Dp = 6.dp,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val daysUntilExpiry = today.daysUntil(expiryDate)
    val totalDays = purchaseDate.daysUntil(expiryDate)

    val nearExpiry = daysUntilExpiry <= nearExpiryDays
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    // Progress is based on remaining time until expiry vs total warranty duration.
    // Clamp to keep the UI stable for already-expired warranties or invalid date ranges.
    val progress =
        if (totalDays <= 0) {
            0f
        } else {
            (daysUntilExpiry.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)
        }

    val barColor = if (nearExpiry) NegativeColor else MaterialTheme.colorScheme.primary

    LinearProgressIndicator(
        modifier = modifier.height(barHeight),
        progress = { progress },
        color = barColor,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
    )
}

@PreviewDesignSystem
@Composable
private fun PreviewWarrantyHealthProgressBarNearExpiry() {
    LifeCompanionTheme {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        // Remaining <= 30 days -> red bar
        val purchaseDate = LocalDate(2020, 1, 1)
        val expiryDate = today

        Column(modifier = Modifier.padding(16.dp)) {
            WarrantyHealthProgressBar(
                purchaseDate = purchaseDate,
                expiryDate = expiryDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewWarrantyHealthProgressBarHealthy() {
    LifeCompanionTheme {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        // Remaining > 30 days -> primary bar
        val purchaseDate = LocalDate(2020, 1, 1)
        val expiryDate = LocalDate(2099, 1, 1)

        Column(modifier = Modifier.padding(16.dp)) {
            WarrantyHealthProgressBar(
                purchaseDate = purchaseDate,
                expiryDate = expiryDate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
