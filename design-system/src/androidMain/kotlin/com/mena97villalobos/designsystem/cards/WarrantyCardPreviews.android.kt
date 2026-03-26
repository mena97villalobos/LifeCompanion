package com.mena97villalobos.designsystem.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mena97villalobos.domain.model.Warranty
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Preview
@Composable
private fun PreviewWarrantyCard() = MaterialTheme {
    val previewToday = Clock.System.todayIn(TimeZone.currentSystemDefault())
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        WarrantyCard(
            warranty = Warranty(
                id = 1,
                description = "Laptop Warranty",
                storeName = "Best Buy",
                purchaseDate = previewToday,
                expiryDate = previewToday,
                notes = "Keep the receipt",
                imageObjectId = null,
            ),
            onClick = {},
            onDelete = {},
        )

        WarrantyCard(
            warranty = Warranty(
                id = 1,
                description = "Laptop Warranty",
                storeName = "Best Buy",
                purchaseDate = previewToday,
                expiryDate = LocalDate(2020, 1, 1),
                notes = "Keep the receipt",
                imageObjectId = null,
            ),
            onClick = {},
            onDelete = {},
        )
    }
}
