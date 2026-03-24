package com.mena97villalobos.lifecompanion.ui.warranty.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.imagepicker.ImagePicker
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun AddEditWarrantyScreen(
    viewModel: AddEditWarrantyViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    WarrantyForm(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
private fun WarrantyForm(
    state: WarrantyFormState,
    onIntent: (WarrantyFormIntent) -> Unit,
) {
    var purchaseDateText by rememberSaveable { mutableStateOf(state.purchaseDate?.toString().orEmpty()) }
    var expiryDateText by rememberSaveable { mutableStateOf(state.expiryDate?.toString().orEmpty()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = state.description,
            onValueChange = {
                onIntent(WarrantyFormIntent.DescriptionChanged(it))
            },
            label = { Text("Description") },
        )

        OutlinedTextField(
            value = state.storeName,
            onValueChange = {
                onIntent(WarrantyFormIntent.StoreChanged(it))
            },
            label = { Text("Store") },
        )

        OutlinedTextField(
            value = state.notes,
            onValueChange = {
                onIntent(WarrantyFormIntent.NotesChanged(it))
            },
            label = { Text("Notes") },
        )

        OutlinedTextField(
            value = purchaseDateText,
            onValueChange = {
                purchaseDateText = it
                onIntent(
                    WarrantyFormIntent.PurchaseDateChanged(
                        runCatching { LocalDate.parse(it) }.getOrNull(),
                    ),
                )
            },
            label = { Text("Purchase Date (yyyy-MM-dd)") },
        )

        OutlinedTextField(
            value = expiryDateText,
            onValueChange = {
                expiryDateText = it
                onIntent(
                    WarrantyFormIntent.ExpiryDateChanged(
                        runCatching { LocalDate.parse(it) }.getOrNull(),
                    ),
                )
            },
            label = { Text("Expiry Date (yyyy-MM-dd)") },
        )

        ImagePicker(
            onSelectImage = {
                onIntent(WarrantyFormIntent.ImageSelected(it))
            },
        )

        Button(
            onClick = { onIntent(WarrantyFormIntent.Save) },
        ) {
            Text("Save")
        }
    }
}
