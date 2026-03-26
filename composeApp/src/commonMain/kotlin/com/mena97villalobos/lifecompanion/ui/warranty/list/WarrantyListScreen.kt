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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mena97villalobos.designsystem.cards.WarrantyCard
import com.mena97villalobos.designsystem.search.WarrantySearchBar
import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.remote.BuildKonfig
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

private fun resolveMinioImageUrl(
    minioEndpoint: String,
    bucketName: String,
    objectId: String,
): String {
    val raw = minioEndpoint.trim()
    val withScheme = if (!raw.contains("://")) "http://$raw" else raw
    val prefix = withScheme.trimEnd('/')
    return "$prefix/$bucketName/$objectId"
}

@Composable
fun WarrantyListScreen(
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WarrantyListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
                warrantySection(
                    title = "Active Warranties",
                    items = active,
                    onEditClick = onEditClick,
                    onDeleteClick = { id -> viewModel.handleIntent(WarrantyListIntent.Delete(id)) },
                )
                warrantySection(
                    title = "Expired Warranties",
                    items = expired,
                    onEditClick = onEditClick,
                    onDeleteClick = { id -> viewModel.handleIntent(WarrantyListIntent.Delete(id)) },
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.warrantySection(
    title: String,
    items: List<Warranty>,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    if (items.isEmpty()) return

    stickyHeader {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }

    items(
        items = items,
        key = { it.id ?: 0 },
    ) { warranty ->
        val imageUrl =
            warranty.imageObjectId?.let {
                resolveMinioImageUrl(
                    minioEndpoint = BuildKonfig.MINIO_ENDPOINT,
                    bucketName = BuildKonfig.MINIO_BUCKET_NAME,
                    objectId = it,
                )
            }

        WarrantyCard(
            description = warranty.description,
            purchaseDate = warranty.purchaseDate,
            expiryDate = warranty.expiryDate,
            imageUrl = imageUrl,
            onEdit = { warranty.id?.let(onEditClick) },
            onDelete = { warranty.id?.let(onDeleteClick) },
        )
    }
}
