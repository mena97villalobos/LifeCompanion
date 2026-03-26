package com.mena97villalobos.lifecompanion.ui.warranty.list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun WarrantyFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Warranty",
            )
        },
        text = {
            Text("Add Warranty")
        },
    )
}
