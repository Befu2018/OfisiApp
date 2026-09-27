package com.example.csoms.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.csoms.data.model.BranchEntity
import com.example.csoms.data.model.ClientEntity
import com.example.csoms.data.model.EmployeeEntity
import com.example.csoms.data.model.InventoryItemEntity
import com.example.csoms.data.model.InvoiceEntity
import com.example.csoms.data.model.PaymentMethod
import com.example.csoms.data.model.UserRole
import com.example.csoms.ui.CsomsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RoleSelectorDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Switch User Role",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Text(
                    text = "Select an operational persona to view role-specific dashboards and privileges:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(380.dp)
                ) {
                    items(UserRole.values()) { role ->
                        val isSelected = role == currentRole
                        val roleIcon = when (role) {
                            UserRole.SYSTEM_ADMIN -> Icons.Default.Shield
                            UserRole.MANAGEMENT -> Icons.Default.Engineering
                            UserRole.HR_OFFICER -> Icons.Default.People
                            UserRole.BRANCH_SUPERVISOR -> Icons.Default.Person
                            UserRole.STORE_KEEPER -> Icons.Default.Inventory
                            UserRole.ACCOUNTANT -> Icons.Default.MonetizationOn
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRoleSelected(role)
                                    onDismiss()
                                }
                                .testTag("role_option_${role.name}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = roleIcon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = role.displayName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${role.badgeTitle}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = role.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterEmployeeDialog(
    branches: List<BranchEntity>,
    onRegister: (
        fullName: String,
        phone: String,
        nidaNumber: String,
        branch: BranchEntity,
        roleTitle: String,
        classification: String,
        dailyRateTzs: Double,
        monthlySalaryTzs: Double,
        documentsStatus: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+255 ") }
    var nidaNumber by remember { mutableStateOf("") }
    var selectedBranch by remember { mutableStateOf(branches.firstOrNull()) }
    var roleTitle by remember { mutableStateOf("Cleaning Specialist") }
    var classification by remember { mutableStateOf("CONTRACT") }
    var rateOrSalary by remember { mutableStateOf("500000") }
    var docsStatus by remember { mutableStateOf("NIDA Verified • Employment Contract Attached") }
    var errorMsg by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Register Employee",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Add contract or non-contract cleaning personnel to Mangasi workforce",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Legal Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (+255)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nidaNumber,
                    onValueChange = { nidaNumber = it },
                    label = { Text("NIDA National ID Number") },
                    placeholder = { Text("e.g. 19900101-11101-00001-20") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = { roleTitle = it },
                    label = { Text("Job Role Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Classification (Tanzanian Labor Law):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                classification = "CONTRACT"
                                if (rateOrSalary == "15000") rateOrSalary = "500000"
                            }
                    ) {
                        RadioButton(
                            selected = classification == "CONTRACT",
                            onClick = {
                                classification = "CONTRACT"
                                if (rateOrSalary == "15000") rateOrSalary = "500000"
                            }
                        )
                        Text("Contract (Monthly)", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                classification = "NON_CONTRACT"
                                if (rateOrSalary == "500000") rateOrSalary = "15000"
                            }
                    ) {
                        RadioButton(
                            selected = classification == "NON_CONTRACT",
                            onClick = {
                                classification = "NON_CONTRACT"
                                if (rateOrSalary == "500000") rateOrSalary = "15000"
                            }
                        )
                        Text("Non-Contract (Daily)", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = rateOrSalary,
                    onValueChange = { rateOrSalary = it },
                    label = {
                        Text(
                            if (classification == "CONTRACT") "Monthly Base Salary (TZS)"
                            else "Daily Casual Wage Rate (TZS)"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Assigned Branch:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    branches.take(3).forEach { branch ->
                        FilterChip(
                            selected = selectedBranch?.id == branch.id,
                            onClick = { selectedBranch = branch },
                            label = { Text(branch.region, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = docsStatus,
                    onValueChange = { docsStatus = it },
                    label = { Text("Documents Status & Notes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMsg = "Please enter employee full name"
                                return@Button
                            }
                            val branch = selectedBranch ?: branches.firstOrNull()
                            if (branch == null) {
                                errorMsg = "No branch selected"
                                return@Button
                            }
                            val amount = rateOrSalary.toDoubleOrNull() ?: 0.0
                            val daily = if (classification == "NON_CONTRACT") amount else 0.0
                            val monthly = if (classification == "CONTRACT") amount else 0.0

                            onRegister(
                                fullName,
                                phone,
                                nidaNumber.ifBlank { "NIDA Pending" },
                                branch,
                                roleTitle,
                                classification,
                                daily,
                                monthly,
                                docsStatus
                            )
                            onDismiss()
                        },
                        modifier = Modifier.testTag("submit_employee_button")
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }
}

@Composable
fun RecordAttendanceDialog(
    employees: List<EmployeeEntity>,
    onRecord: (
        date: String,
        employee: EmployeeEntity,
        status: String,
        checkInTime: String,
        checkOutTime: String,
        notes: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedEmployee by remember { mutableStateOf(employees.firstOrNull()) }
    var status by remember { mutableStateOf("PRESENT") }
    var checkInTime by remember { mutableStateOf("07:00 AM") }
    var checkOutTime by remember { mutableStateOf("04:30 PM") }
    var notes by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Daily Attendance") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Date: $today • Branch Supervisor Entry",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Select Employee:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Employee selector list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    employees.forEach { emp ->
                        val isSelected = selectedEmployee?.id == emp.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .clickable { selectedEmployee = emp }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedEmployee = emp }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = emp.fullName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${emp.employeeCode} • ${emp.classification.replace("_", "-")} • ${emp.branchName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Attendance Status:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("PRESENT", "HALF_DAY", "ABSENT", "SICK_LEAVE").forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.replace("_", " "), fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = checkInTime,
                        onValueChange = { checkInTime = it },
                        label = { Text("Check-In") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = checkOutTime,
                        onValueChange = { checkOutTime = it },
                        label = { Text("Check-Out") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Supervisor Shift Notes") },
                    placeholder = { Text("e.g. Assigned to deep scrub banks lobby") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedEmployee?.classification == "NON_CONTRACT") {
                    Spacer(modifier = Modifier.height(6.dp))
                    val wage = when (status) {
                        "PRESENT" -> selectedEmployee!!.dailyRateTzs
                        "HALF_DAY" -> selectedEmployee!!.dailyRateTzs * 0.5
                        else -> 0.0
                    }
                    Text(
                        text = "Calculated Casual Daily Wage: ${CsomsViewModel.formatTzs(wage)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (errorMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val emp = selectedEmployee
                    if (emp == null) {
                        errorMsg = "Please choose an employee"
                        return@Button
                    }
                    onRecord(today, emp, status, checkInTime, checkOutTime, notes)
                    onDismiss()
                },
                modifier = Modifier.testTag("submit_attendance_btn")
            ) {
                Text("Save Attendance")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EquipmentRequestDialog(
    branches: List<BranchEntity>,
    inventory: List<InventoryItemEntity>,
    onSubmit: (
        branch: BranchEntity,
        item: InventoryItemEntity,
        quantity: Int,
        urgency: String,
        remarks: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBranch by remember { mutableStateOf(branches.firstOrNull()) }
    var selectedItem by remember { mutableStateOf(inventory.firstOrNull()) }
    var quantityText by remember { mutableStateOf("2") }
    var urgency by remember { mutableStateOf("NORMAL") }
    var remarks by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Equipment / Supplies") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Request supplies or tools from Head Office Store Keeper",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Branch:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    branches.take(3).forEach { b ->
                        FilterChip(
                            selected = selectedBranch?.id == b.id,
                            onClick = { selectedBranch = b },
                            label = { Text(b.name.take(16), fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Item Needed:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    inventory.forEach { item ->
                        val isSelected = selectedItem?.id == item.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedItem = item }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { selectedItem = item })
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${item.name} (${item.unit})",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity Needed") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Urgency Level:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("NORMAL", "URGENT", "CRITICAL").forEach { urg ->
                        FilterChip(
                            selected = urgency == urg,
                            onClick = { urgency = urg },
                            label = { Text(urg, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Operational Justification") },
                    placeholder = { Text("e.g. For new bank cleaning contract deep scrub") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val branch = selectedBranch ?: branches.firstOrNull()
                    val item = selectedItem ?: inventory.firstOrNull()
                    val qty = quantityText.toIntOrNull() ?: 1
                    if (branch == null || item == null) {
                        errorMsg = "Please select branch and item"
                        return@Button
                    }
                    onSubmit(branch, item, qty, urgency, remarks)
                    onDismiss()
                }
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RestockItemDialog(
    item: InventoryItemEntity,
    onRestock: (itemId: Long, addHQ: Int, addBranch: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var addHQ by remember { mutableStateOf("10") }
    var addBranch by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restock: ${item.name}") },
        text = {
            Column {
                Text(
                    text = "Current Stock: HQ = ${item.headOfficeStock}, Branches = ${item.branchStock} (${item.unit})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = addHQ,
                    onValueChange = { addHQ = it },
                    label = { Text("Add to Head Office Central Warehouse") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = addBranch,
                    onValueChange = { addBranch = it },
                    label = { Text("Add to Branch Local Store") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hq = addHQ.toIntOrNull() ?: 0
                    val br = addBranch.toIntOrNull() ?: 0
                    onRestock(item.id, hq, br)
                    onDismiss()
                }
            ) {
                Text("Apply Restock")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun GenerateInvoiceDialog(
    clients: List<ClientEntity>,
    onGenerate: (
        client: ClientEntity,
        contractId: Long,
        subtotalTzs: Double,
        dueDate: String,
        notes: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
    var subtotalText by remember { mutableStateOf("4500000") }
    var dueDate by remember { mutableStateOf("2026-10-15") }
    var notes by remember { mutableStateOf("Monthly commercial cleaning & janitorial services") }
    val subtotal = subtotalText.toDoubleOrNull() ?: 0.0
    val vatAmount = subtotal * 0.18 // 18% VAT
    val totalAmount = subtotal + vatAmount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate VAT Invoice") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Includes mandatory 18% Tanzanian Value Added Tax (VAT)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Client:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    clients.forEach { client ->
                        val isSelected = selectedClient?.id == client.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { selectedClient = client }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { selectedClient = client })
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(client.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = subtotalText,
                    onValueChange = { subtotalText = it },
                    label = { Text("Service Subtotal (TZS)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Service Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", style = MaterialTheme.typography.bodySmall)
                            Text(CsomsViewModel.formatTzs(subtotal), style = MaterialTheme.typography.bodySmall)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("VAT (18%):", style = MaterialTheme.typography.bodySmall)
                            Text(CsomsViewModel.formatTzs(vatAmount), style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total Invoiced:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                CsomsViewModel.formatTzs(totalAmount),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val client = selectedClient ?: clients.firstOrNull() ?: return@Button
                    onGenerate(client, 1, subtotal, dueDate, notes)
                    onDismiss()
                }
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RecordPaymentDialog(
    invoices: List<InvoiceEntity>,
    onRecord: (invoice: InvoiceEntity, amount: Double, method: String, reference: String) -> Unit,
    onDismiss: () -> Unit
) {
    val unpaid = remember(invoices) { invoices.filter { it.status != "PAID" } }
    var selectedInvoice by remember { mutableStateOf(unpaid.firstOrNull() ?: invoices.firstOrNull()) }
    var amountText by remember { mutableStateOf("") }
    var method by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER.label) }
    var refNumber by remember { mutableStateOf("TXN-2026-") }

    // Update default amount when invoice chosen
    selectedInvoice?.let { inv ->
        val balance = (inv.totalAmountTzs - inv.paidAmountTzs).coerceAtLeast(0.0)
        if (amountText.isBlank()) {
            amountText = balance.toLong().toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Client Payment") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Select Invoice to Pay:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    invoices.forEach { inv ->
                        val isSelected = selectedInvoice?.id == inv.id
                        val balance = inv.totalAmountTzs - inv.paidAmountTzs
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable {
                                    selectedInvoice = inv
                                    amountText = balance.toLong().toString()
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = {
                                selectedInvoice = inv
                                amountText = balance.toLong().toString()
                            })
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "${inv.invoiceNumber} - ${inv.clientName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Due: ${CsomsViewModel.formatTzs(balance)} (${inv.status})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Payment Amount (TZS)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Payment Channel:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Column {
                    PaymentMethod.values().forEach { pm ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { method = pm.label }
                        ) {
                            RadioButton(selected = method == pm.label, onClick = { method = pm.label })
                            Text(pm.label, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = refNumber,
                    onValueChange = { refNumber = it },
                    label = { Text("Bank / M-Pesa Reference No.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inv = selectedInvoice ?: return@Button
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onRecord(inv, amt, method, refNumber)
                        onDismiss()
                    }
                }
            ) {
                Text("Record Payment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CreateBranchDialog(
    onCreate: (name: String, region: String, address: String, supervisor: String, phone: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Dar es Salaam") }
    var address by remember { mutableStateOf("") }
    var supervisor by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+255 ") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Branch") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Branch Name") },
                    placeholder = { Text("e.g. Mbeya Commercial Hub") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Region") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Office Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = supervisor,
                    onValueChange = { supervisor = it },
                    label = { Text("Assigned Supervisor Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, region, address, supervisor, phone)
                        onDismiss()
                    }
                }
            ) {
                Text("Create Branch")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
