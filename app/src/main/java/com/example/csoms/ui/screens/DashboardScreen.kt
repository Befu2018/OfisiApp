package com.example.csoms.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.csoms.data.model.AttendanceEntity
import com.example.csoms.data.model.BranchEntity
import com.example.csoms.data.model.EquipmentRequestEntity
import com.example.csoms.data.model.InventoryItemEntity
import com.example.csoms.data.model.InvoiceEntity
import com.example.csoms.data.model.UserRole
import com.example.csoms.ui.CsomsViewModel
import com.example.csoms.ui.DashboardKpis
import com.example.csoms.ui.components.MetricCard
import com.example.csoms.ui.components.SectionHeader
import com.example.csoms.ui.components.StatusBadge
import com.example.ui.theme.MangasiDarkNavy
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusUrgentRed

@Composable
fun DashboardScreen(
    currentRole: UserRole,
    kpis: DashboardKpis,
    branches: List<BranchEntity>,
    inventory: List<InventoryItemEntity>,
    requests: List<EquipmentRequestEntity>,
    invoices: List<InvoiceEntity>,
    attendance: List<AttendanceEntity>,
    onOpenRoleSelector: () -> Unit,
    onNavigateToWorkforce: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToReports: () -> Unit,
    onOpenRegisterEmployee: () -> Unit,
    onOpenRecordAttendance: () -> Unit,
    onOpenEquipmentRequest: () -> Unit,
    onOpenGenerateInvoice: () -> Unit,
    onOpenRecordPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lowStockItems = inventory.filter { (it.headOfficeStock + it.branchStock) <= it.minStockThreshold }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Hero Brand Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mangasi_hero),
                    contentDescription = "Mangasi Operations Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MangasiDarkNavy.copy(alpha = 0.78f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mangasi_logo),
                                contentDescription = "Mangasi Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MANGASI INVESTMENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.2.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF64FFDA)
                                )
                                Text(
                                    text = "Cleaning Services OMS",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        // Role badge button
                        Surface(
                            onClick = onOpenRoleSelector,
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = currentRole.badgeTitle,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Welcome role banner
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Active Persona: ${currentRole.displayName}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = currentRole.description,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Switch",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64FFDA)
                                ),
                                modifier = Modifier
                                    .clickable { onOpenRoleSelector() }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Low-Stock Alert Warning Banner (If Any)
        if (lowStockItems.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Low Stock Alert: ${lowStockItems.size} Consumables Critical",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = lowStockItems.take(2).joinToString(", ") { "${it.name} (${it.headOfficeStock + it.branchStock} left)" },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        OutlinedButton(
                            onClick = onNavigateToInventory,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text("Restock", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. Operational Overview KPI Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "Operational Overview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Active Workforce",
                        value = "${kpis.totalEmployees} Staff",
                        subtitle = "${kpis.todayAttendancePresent} on duty today",
                        icon = Icons.Default.People,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Active Contracts",
                        value = "${kpis.activeContractsCount} Clients",
                        subtitle = "CRDB, Serena, NMB+",
                        icon = Icons.Default.Business,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Invoiced Revenue",
                        value = CsomsViewModel.formatTzs(kpis.totalInvoicedTzs),
                        subtitle = "18% VAT Inclusive",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = StatusActiveGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Outstanding Debtors",
                        value = CsomsViewModel.formatTzs(kpis.outstandingDebtorsTzs),
                        subtitle = "Pending client payment",
                        icon = Icons.Default.Receipt,
                        accentColor = StatusUrgentRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Role-Specific Action Center
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = "Quick Actions (${currentRole.badgeTitle})",
                subtitle = "Tasks tailored for your assigned role"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    when (currentRole) {
                        UserRole.BRANCH_SUPERVISOR -> {
                            Text(
                                text = "Branch Field Supervisor Console",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Record attendance to ensure accurate daily casual wages and monthly timesheets.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenRecordAttendance,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Attendance", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = onOpenEquipmentRequest,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Request Supplies", fontSize = 12.sp)
                                }
                            }
                        }

                        UserRole.HR_OFFICER -> {
                            Text(
                                text = "HR Employee Lifecycle Console",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Register cleaners, track NIDA verification and contract agreements.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenRegisterEmployee,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Register Employee", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = onNavigateToWorkforce,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("View Workforce", fontSize = 12.sp)
                                }
                            }
                        }

                        UserRole.STORE_KEEPER -> {
                            Text(
                                text = "Store Keeper Central Inventory",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Manage cleaning chemicals, scrubbers, and branch dispatch requests.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToInventory,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Dispatch / Restock", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = onOpenEquipmentRequest,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("New Supply Requisition", fontSize = 12.sp)
                                }
                            }
                        }

                        UserRole.ACCOUNTANT -> {
                            Text(
                                text = "Finance & Accounts Office",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Issue Tanzanian VAT invoices, record client payments, and process staff payroll.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenGenerateInvoice,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Generate Invoice", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = onOpenRecordPayment,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Payment", fontSize = 12.sp)
                                }
                            }
                        }

                        else -> { // Management or System Admin
                            Text(
                                text = "Executive & Strategic Command",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Monitor nation-wide operational branch performance and financial margins.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToReports,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Executive Reports", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = onNavigateToFinance,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Finance & Payroll", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Branch Network Highlights
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(
                title = "Tanzanian Branch Network",
                subtitle = "Active operational bases",
                actionText = "View All",
                onActionClick = onNavigateToReports
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(branches) { branch ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onNavigateToReports() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = branch.region,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${branch.staffCount} Staff",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = branch.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sup: ${branch.supervisorName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (branch.staffCount / 20f).coerceIn(0.1f, 1.0f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 6. Recent Operational Activity Feed
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(
                title = "Recent Operational Activity",
                subtitle = "Real-time updates across branches"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Item 1: Attendance
                    attendance.firstOrNull()?.let { att ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(StatusActiveGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusActiveGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Attendance: ${att.employeeName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${att.branchName} • In: ${att.checkInTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(status = att.status)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }

                    // Item 2: Equipment Request
                    requests.firstOrNull()?.let { req ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(StatusPendingAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory,
                                    contentDescription = null,
                                    tint = StatusPendingAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Requisition: ${req.itemName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${req.quantityRequested} pcs • ${req.branchName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(status = req.status)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }

                    // Item 3: Invoice
                    invoices.firstOrNull()?.let { inv ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Invoice: ${inv.clientName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${inv.invoiceNumber} • ${CsomsViewModel.formatTzs(inv.totalAmountTzs)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(status = inv.status)
                        }
                    }
                }
            }
        }
    }
}
