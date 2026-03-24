package com.mena97villalobos.lifecompanion.ui.warranty.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.cards.WarrantyCard
import com.mena97villalobos.designsystem.search.WarrantySearchBar
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock

@Composable
fun WarrantyListScreen(
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WarrantyListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val filtered = state.warranties.filter {
        it.description.contains(state.searchQuery, ignoreCase = true) ||
            it.storeName.contains(state.searchQuery, ignoreCase = true)
    }
    val active = filtered
        .filter { it.expiryDate >= today }
        .sortedBy { it.expiryDate }
    val expired = filtered
        .filter { it.expiryDate < today }
        .sortedByDescending { it.expiryDate }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            WarrantyFAB(onClick = onAddClick)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
        ) {
            WarrantySearchBar(
                query = state.searchQuery,
                onQueryChange = {
                    viewModel.handleIntent(WarrantyListIntent.Search(it))
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                if (active.isNotEmpty()) {
                    stickyHeader {
                        Text(
                            text = "Active Warranties",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    items(
                        items = active,
                        key = { it.id ?: 0 },
                    ) { warranty ->
                        WarrantyCard(
                            warranty = warranty,
                            onClick = {
                                warranty.id?.let { onEditClick(it) }
                            },
                            onDelete = {
                                warranty.id?.let {
                                    viewModel.handleIntent(WarrantyListIntent.Delete(it))
                                }
                            },
                        )
                    }
                }

                if (expired.isNotEmpty()) {
                    stickyHeader {
                        Text(
                            text = "Expired Warranties",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    items(
                        items = expired,
                        key = { it.id ?: 0 },
                    ) { warranty ->
                        WarrantyCard(
                            warranty = warranty,
                            onClick = {
                                warranty.id?.let { onEditClick(it) }
                            },
                            onDelete = {
                                warranty.id?.let {
                                    viewModel.handleIntent(WarrantyListIntent.Delete(it))
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
