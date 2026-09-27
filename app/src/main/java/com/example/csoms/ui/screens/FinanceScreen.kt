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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.csoms.data.model.InvoiceEntity
import com.example.csoms.data.model.PaymentEntity
import com.example.csoms.data.model.PayrollRecordEntity
import com.example.csoms.ui.CsomsViewModel
import com.example.csoms.ui.components.EmptyStateCard
import com.example.csoms.ui.components.MetricCard
import com.example.csoms.ui.components.StatusBadge
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusUrgentRed

@Composable
fun FinanceScreen(
    invoices: List<InvoiceEntity>,
    payments: List<PaymentEntity>,
    payroll: List<PayrollRecordEntity>,
    onOpenGenerateInvoice: () -> Unit,
    onOpenRecordPayment: () -> Unit,
    onRunPayroll: (period: String) -> Unit,
    onUpdatePayrollStatus: (record: PayrollRecordEntity, newStatus: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("18% VAT Invoices", "Payment Receipts", "Workforce Payroll")

    val totalInvoiced = remember(invoices) { invoices.sumOf { it.totalAmountTzs } }
    val totalPaid = remember(invoices) { invoices.sumOf { it.paidAmountTzs } }
    val totalDebtors = (totalInvoiced - totalPaid).coerceAtLeast(0.0)

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
                // Tab 0: Invoicing with 18% VAT
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Financial KPI summary
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Invoicing & Revenue Status (TZS)",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Total Invoiced", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(CsomsViewModel.formatTzs(totalInvoiced), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Column {
                                            Text("Collected", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(CsomsViewModel.formatTzs(totalPaid), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = StatusActiveGreen))
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Outstanding", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(CsomsViewModel.formatTzs(totalDebtors), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = StatusUrgentRed))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Monthly Service Invoices (${invoices.size})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Button(
                                    onClick = onOpenGenerateInvoice,
                                    modifier = Modifier.testTag("generate_invoice_header_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Invoice", fontSize = 11.sp)
                                }
                            }
                        }

                        items(invoices, key = { it.id }) { inv ->
                            InvoiceCard(
                                invoice = inv,
                                onPay = onOpenRecordPayment
                            )
                        }
                    }
                }

                // Tab 1: Payment Receipts
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Client Payment Receipts (${payments.size})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Button(onClick = onOpenRecordPayment) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Payment", fontSize = 11.sp)
                                }
                            }
                        }

                        if (payments.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    title = "No Payments Logged",
                                    message = "No client payment transactions recorded yet."
                                )
                            }
                        } else {
                            items(payments, key = { it.id }) { pmt ->
                                PaymentReceiptCard(payment = pmt)
                            }
                        }
                    }
                }

                // Tab 2: Payroll Processing
                2 -> {
                    val period = "September 2026"
                    val totalPayroll = remember(payroll) { payroll.sumOf { it.netPayTzs } }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
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
                                                text = "Payroll Period: $period",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Total Net Payout: ${CsomsViewModel.formatTzs(totalPayroll)}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                        }
                                        Button(
                                            onClick = { onRunPayroll(period) },
                                            modifier = Modifier.testTag("run_payroll_btn")
                                        ) {
                                            Text("Recalculate Run", fontSize = 11.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "• Contract Staff: Base Salary + 40k Transport - 10% NSSF Statutory\n• Non-Contract / Casual: Attended Shifts × Daily Wage Rate",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Employee Pay Slips (${payroll.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        items(payroll, key = { it.id }) { rec ->
                            PayrollSlipCard(
                                record = rec,
                                onApprove = { onUpdatePayrollStatus(rec, "APPROVED") },
                                onPay = { onUpdatePayrollStatus(rec, "PAID") }
                            )
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                if (selectedTab == 0) onOpenGenerateInvoice()
                else if (selectedTab == 1) onOpenRecordPayment()
                else onRunPayroll("September 2026")
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .testTag("finance_fab")
        ) {
            Icon(
                imageVector = if (selectedTab == 0) Icons.Default.Receipt else Icons.Default.MonetizationOn,
                contentDescription = "Action"
            )
        }
    }
}

@Composable
private fun InvoiceCard(
    invoice: InvoiceEntity,
    onPay: () -> Unit
) {
    val balance = invoice.totalAmountTzs - invoice.paidAmountTzs

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
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = invoice.clientName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                StatusBadge(status = invoice.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Financial breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Subtotal", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CsomsViewModel.formatTzs(invoice.subtotalTzs), style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text("VAT (18%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CsomsViewModel.formatTzs(invoice.vatAmountTzs), style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        CsomsViewModel.formatTzs(invoice.totalAmountTzs),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due: ${invoice.dueDate} • Paid: ${CsomsViewModel.formatTzs(invoice.paidAmountTzs)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (invoice.status != "PAID") {
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Record Payment", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentReceiptCard(payment: PaymentEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(StatusActiveGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusActiveGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = payment.receiptNumber,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = CsomsViewModel.formatTzs(payment.amountTzs),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = StatusActiveGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${payment.clientName} • For ${payment.invoiceNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${payment.paymentMethod} • Ref: ${payment.referenceNumber} • Date: ${payment.paymentDate}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PayrollSlipCard(
    record: PayrollRecordEntity,
    onApprove: () -> Unit,
    onPay: () -> Unit
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
                        text = record.employeeName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${record.employeeCode} • ${record.branchName} • ${record.classification.replace("_", "-")}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = record.paymentStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Wage breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Days Worked", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${record.daysAttended} Shifts", style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text("Base Pay", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CsomsViewModel.formatTzs(record.basePayTzs), style = MaterialTheme.typography.bodySmall)
                }
                Column {
                    Text("Deductions (NSSF)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("-${CsomsViewModel.formatTzs(record.deductionsTzs)}", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Net Pay", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        CsomsViewModel.formatTzs(record.netPayTzs),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (record.paymentStatus == "PENDING") {
                    Button(onClick = onApprove) {
                        Text("Approve Pay Slip", fontSize = 11.sp)
                    }
                } else if (record.paymentStatus == "APPROVED") {
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusActiveGreen)
                    ) {
                        Text("Disburse Payment", fontSize = 11.sp)
                    }
                } else {
                    Text(
                        text = "✓ Payout Cleared",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusActiveGreen
                    )
                }
            }
        }
    }
}
