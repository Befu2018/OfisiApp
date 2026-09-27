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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.csoms.data.model.AttendanceEntity
import com.example.csoms.data.model.BranchEntity
import com.example.csoms.data.model.EmployeeEntity
import com.example.csoms.ui.CsomsViewModel
import com.example.csoms.ui.components.EmptyStateCard
import com.example.csoms.ui.components.StatusBadge
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusPendingAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkforceScreen(
    employees: List<EmployeeEntity>,
    attendance: List<AttendanceEntity>,
    branches: List<BranchEntity>,
    onOpenRegisterEmployee: () -> Unit,
    onOpenRecordAttendance: () -> Unit,
    onToggleEmployeeStatus: (EmployeeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Employees Directory", "Daily Attendance", "Classification & Compliance")

    var searchQuery by remember { mutableStateOf("") }
    var selectedClassificationFilter by remember { mutableStateOf("ALL") }
    var selectedBranchFilter by remember { mutableStateOf<BranchEntity?>(null) }

    val filteredEmployees = remember(employees, searchQuery, selectedClassificationFilter, selectedBranchFilter) {
        employees.filter { emp ->
            val matchesQuery = emp.fullName.contains(searchQuery, ignoreCase = true) ||
                    emp.employeeCode.contains(searchQuery, ignoreCase = true) ||
                    emp.nidaNumber.contains(searchQuery, ignoreCase = true) ||
                    emp.roleTitle.contains(searchQuery, ignoreCase = true)
            val matchesClassification = when (selectedClassificationFilter) {
                "CONTRACT" -> emp.classification == "CONTRACT"
                "NON_CONTRACT" -> emp.classification == "NON_CONTRACT"
                else -> true
            }
            val matchesBranch = selectedBranchFilter == null || emp.branchId == selectedBranchFilter?.id
            matchesQuery && matchesClassification && matchesBranch
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Header
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
                // Tab 0: Employees Directory
                0 -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search bar & Filters
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search by name, code, NIDA, role...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("workforce_search_bar")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Classification chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = selectedClassificationFilter == "ALL",
                                    onClick = { selectedClassificationFilter = "ALL" },
                                    label = { Text("All (${employees.size})", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = selectedClassificationFilter == "CONTRACT",
                                    onClick = { selectedClassificationFilter = "CONTRACT" },
                                    label = { Text("Contract (${employees.count { it.classification == "CONTRACT" }})", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = selectedClassificationFilter == "NON_CONTRACT",
                                    onClick = { selectedClassificationFilter = "NON_CONTRACT" },
                                    label = { Text("Daily Casuals (${employees.count { it.classification == "NON_CONTRACT" }})", fontSize = 11.sp) }
                                )
                            }

                            // Branch filter chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                item {
                                    FilterChip(
                                        selected = selectedBranchFilter == null,
                                        onClick = { selectedBranchFilter = null },
                                        label = { Text("All Branches", fontSize = 10.sp) }
                                    )
                                }
                                items(branches) { b ->
                                    FilterChip(
                                        selected = selectedBranchFilter?.id == b.id,
                                        onClick = { selectedBranchFilter = b },
                                        label = { Text(b.region, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }

                        // Employee List
                        if (filteredEmployees.isEmpty()) {
                            EmptyStateCard(
                                title = "No Employees Found",
                                message = "No workforce records match your current filter criteria."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredEmployees, key = { it.id }) { emp ->
                                    EmployeeCard(
                                        employee = emp,
                                        onToggleStatus = { onToggleEmployeeStatus(emp) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 1: Daily Attendance
                1 -> {
                    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
                    val todayAttendance = remember(attendance, today) {
                        attendance.filter { it.date == today }
                    }
                    val presentCount = todayAttendance.count { it.status == "PRESENT" }
                    val halfDayCount = todayAttendance.count { it.status == "HALF_DAY" }
                    val absentCount = todayAttendance.count { it.status == "ABSENT" }
                    val sickCount = todayAttendance.count { it.status == "SICK_LEAVE" }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Attendance Overview Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
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
                                                text = "Today's Attendance Sheet",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Date: $today • Branch Supervisor Entry",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Button(
                                            onClick = onOpenRecordAttendance,
                                            modifier = Modifier.testTag("record_attendance_header_btn")
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Record", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Counter Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        AttendanceCounterItem("Present", "$presentCount", StatusActiveGreen)
                                        AttendanceCounterItem("Half Day", "$halfDayCount", StatusPendingAmber)
                                        AttendanceCounterItem("Absent", "$absentCount", MaterialTheme.colorScheme.error)
                                        AttendanceCounterItem("Sick Leave", "$sickCount", MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }

                        // Duplicate prevention note
                        item {
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Constraint Active: Duplicate attendance for the same employee on the same calendar date is strictly prevented.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        // Attendance entries
                        item {
                            Text(
                                text = "Recorded Logs (${attendance.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(attendance, key = { it.id }) { record ->
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
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AssignmentTurnedIn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = record.employeeName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            StatusBadge(status = record.status)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${record.date} • ${record.branchName}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Hours: ${record.checkInTime} - ${record.checkOutTime}" +
                                                    if (record.wageEarnedTzs > 0) " • Casual Wage: ${CsomsViewModel.formatTzs(record.wageEarnedTzs)}" else "",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (record.notes.isNotBlank()) {
                                            Text(
                                                text = "Note: ${record.notes}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: Classification & Compliance
                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Tanzanian Employment & Labor Relations Act Compliance",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Mangasi Investment enforces compliant workforce classifications:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    ComplianceFeatureItem(
                                        title = "1. Contract Employees (Monthly Salary)",
                                        desc = "Includes 10% statutory NSSF contribution, PAYE deduction, statutory paid leave, and severance benefits."
                                    )
                                    ComplianceFeatureItem(
                                        title = "2. Non-Contract / Casual Cleaners (Daily Wage)",
                                        desc = "Paid daily or weekly based strictly on verified attendance records. Zero deductions, payable per approved shift."
                                    )
                                    ComplianceFeatureItem(
                                        title = "3. Mandatory NIDA National ID Registration",
                                        desc = "All deployed cleaning operatives must provide a verified NIDA citizen ID number or local government introduction letter."
                                    )
                                    ComplianceFeatureItem(
                                        title = "4. Occupational Safety & Health (OSHA Tanzania)",
                                        desc = "Sanitation safety certificates and personal protective equipment (PPE) records logged per worker."
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                if (selectedTab == 1) onOpenRecordAttendance() else onOpenRegisterEmployee()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .testTag("workforce_fab")
        ) {
            Icon(
                imageVector = if (selectedTab == 1) Icons.Default.AssignmentTurnedIn else Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}

@Composable
private fun EmployeeCard(
    employee: EmployeeEntity,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (employee.isActive) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                                if (employee.classification == "CONTRACT") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (employee.classification == "CONTRACT") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = employee.fullName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${employee.employeeCode} • ${employee.roleTitle}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StatusBadge(status = if (employee.isActive) "ACTIVE" else "INACTIVE")
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Branch: ${employee.branchName}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "NIDA: ${employee.nidaNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (employee.classification == "CONTRACT") "Monthly Salary" else "Daily Casual Wage",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (employee.classification == "CONTRACT") CsomsViewModel.formatTzs(employee.monthlySalaryTzs)
                        else "${CsomsViewModel.formatTzs(employee.dailyRateTzs)} / day",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                    text = employee.documentsStatus,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (employee.isActive) "Deactivate" else "Activate",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (employee.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { onToggleStatus() }
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun AttendanceCounterItem(
    label: String,
    count: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ComplianceFeatureItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
