package com.example.csoms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.csoms.data.model.EquipmentRequestEntity
import com.example.csoms.data.model.InventoryItemEntity
import com.example.csoms.ui.CsomsViewModel
import com.example.csoms.ui.components.EmptyStateCard
import com.example.csoms.ui.components.StatusBadge
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusUrgentRed

@Composable
fun InventoryScreen(
    inventory: List<InventoryItemEntity>,
    requests: List<EquipmentRequestEntity>,
    onOpenRestock: (InventoryItemEntity) -> Unit,
    onOpenEquipmentRequest: () -> Unit,
    onUpdateRequestStatus: (request: EquipmentRequestEntity, newStatus: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tools & Consumables", "Branch Equipment Requests")
    var categoryFilter by remember { mutableStateOf("ALL") }

    val filteredItems = remember(inventory, categoryFilter) {
        when (categoryFilter) {
            "EQUIPMENT" -> inventory.filter { it.category == "EQUIPMENT" }
            "CONSUMABLE" -> inventory.filter { it.category == "CONSUMABLE" }
            "LOW_STOCK" -> inventory.filter { (it.headOfficeStock + it.branchStock) <= it.minStockThreshold }
            else -> inventory
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                // Tab 0: Stock & Inventory
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Filters row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = categoryFilter == "ALL",
                                onClick = { categoryFilter = "ALL" },
                                label = { Text("All (${inventory.size})", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = categoryFilter == "EQUIPMENT",
                                onClick = { categoryFilter = "EQUIPMENT" },
                                label = { Text("Tools/Machines", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = categoryFilter == "CONSUMABLE",
                                onClick = { categoryFilter = "CONSUMABLE" },
                                label = { Text("Consumables", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = categoryFilter == "LOW_STOCK",
                                onClick = { categoryFilter = "LOW_STOCK" },
                                label = {
                                    val count = inventory.count { (it.headOfficeStock + it.branchStock) <= it.minStockThreshold }
                                    Text("Low Stock ($count)", fontSize = 11.sp, color = if (count > 0) StatusUrgentRed else Color.Unspecified)
                                }
                            )
                        }

                        if (filteredItems.isEmpty()) {
                            EmptyStateCard(
                                title = "No Items in Inventory",
                                message = "No cleaning tools or supplies match the selected filter."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredItems, key = { it.id }) { item ->
                                    InventoryCard(
                                        item = item,
                                        onRestock = { onOpenRestock(item) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 1: Equipment Requests Workflow
                1 -> {
                    if (requests.isEmpty()) {
                        EmptyStateCard(
                            title = "No Equipment Requests",
                            message = "No branch requisitions are currently logged."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(requests, key = { it.id }) { req ->
                                EquipmentRequestCard(
                                    request = req,
                                    onApprove = { onUpdateRequestStatus(req, "APPROVED") },
                                    onDispatch = { onUpdateRequestStatus(req, "DISPATCHED") },
                                    onReceive = { onUpdateRequestStatus(req, "RECEIVED") },
                                    onReject = { onUpdateRequestStatus(req, "REJECTED") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB to submit new supply request
        FloatingActionButton(
            onClick = onOpenEquipmentRequest,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .testTag("inventory_request_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Request")
        }
    }
}

@Composable
private fun InventoryCard(
    item: InventoryItemEntity,
    onRestock: () -> Unit
) {
    val totalStock = item.headOfficeStock + item.branchStock
    val isLowStock = totalStock <= item.minStockThreshold

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLowStock) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isLowStock) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.category == "EQUIPMENT") Icons.Default.CleaningServices else Icons.Default.Inventory,
                            contentDescription = null,
                            tint = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${if (item.category == "EQUIPMENT") "Tool/Equipment" else "Consumable Chemical"} • ${item.unit}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (isLowStock) {
                    StatusBadge(status = "URGENT")
                } else {
                    StatusBadge(status = "ACTIVE")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Stock breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Head Office HQ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.headOfficeStock} ${item.unit}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column {
                    Text(
                        text = "Branch Stores",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.branchStock} ${item.unit}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column {
                    Text(
                        text = "Threshold",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Min: ${item.minStockThreshold}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Unit Cost",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CsomsViewModel.formatTzs(item.unitCostTzs),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Restocked: ${item.lastRestockedDate}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.outline
                )
                Button(
                    onClick = onRestock,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Restock / Adjust", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun EquipmentRequestCard(
    request: EquipmentRequestEntity,
    onApprove: () -> Unit,
    onDispatch: () -> Unit,
    onReceive: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${request.requestCode} • ${request.itemName}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Req by: ${request.requestedBy} (${request.branchName})",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Qty Requested: ${request.quantityRequested}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Urgency: ${request.urgency}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (request.urgency == "CRITICAL" || request.urgency == "URGENT") StatusUrgentRed else MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Date: ${request.requestDate}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (request.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${request.remarks}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action workflow buttons based on status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (request.status) {
                    "PENDING" -> {
                        OutlinedButton(onClick = onReject) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reject", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onApprove) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve", fontSize = 11.sp)
                        }
                    }
                    "APPROVED" -> {
                        Button(onClick = onDispatch) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch from HQ", fontSize = 11.sp)
                        }
                    }
                    "DISPATCHED" -> {
                        Button(onClick = onReceive) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirm Received at Branch", fontSize = 11.sp)
                        }
                    }
                    "RECEIVED" -> {
                        Text(
                            text = "✓ Stock received and added to branch store",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = StatusActiveGreen
                        )
                    }
                    else -> {
                        Text(
                            text = "Request Closed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}
