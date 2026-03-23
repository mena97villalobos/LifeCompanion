package com.mena97villalobos.lifecompanion.ui.warranty.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.imagepicker.ImagePicker
import org.koin.androidx.compose.koinViewModel

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

        ImagePicker {
            onIntent(WarrantyFormIntent.ImageSelected(it))
        }

        Button(
            onClick = { onIntent(WarrantyFormIntent.Save) },
        ) {
            Text("Save")
        }
    }
}
