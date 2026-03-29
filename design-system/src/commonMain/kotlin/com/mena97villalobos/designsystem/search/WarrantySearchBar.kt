package com.mena97villalobos.designsystem.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.PreviewDesignSystem
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme

@Composable
fun WarrantySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search warranties") },
        singleLine = true,
    )
}

@PreviewDesignSystem
@Composable
private fun PreviewWarrantySearchBar() {
    LifeCompanionTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WarrantySearchBar(
                query = "laptop",
                onQueryChange = {},
            )
        }
    }
}

