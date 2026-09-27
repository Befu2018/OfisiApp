package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.csoms.data.model.InventoryItemEntity
import com.example.csoms.data.model.UserRole
import com.example.csoms.ui.CsomsViewModel
import com.example.csoms.ui.components.CreateBranchDialog
import com.example.csoms.ui.components.EquipmentRequestDialog
import com.example.csoms.ui.components.GenerateInvoiceDialog
import com.example.csoms.ui.components.RecordAttendanceDialog
import com.example.csoms.ui.components.RecordPaymentDialog
import com.example.csoms.ui.components.RegisterEmployeeDialog
import com.example.csoms.ui.components.RestockItemDialog
import com.example.csoms.ui.components.RoleBadgeChip
import com.example.csoms.ui.components.RoleSelectorDialog
import com.example.csoms.ui.screens.DashboardScreen
import com.example.csoms.ui.screens.FinanceScreen
import com.example.csoms.ui.screens.InventoryScreen
import com.example.csoms.ui.screens.ReportsScreen
import com.example.csoms.ui.screens.WorkforceScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class Screen(val title: String, val icon: ImageVector) {
    object Dashboard : Screen("Dashboard", Icons.Default.Dashboard)
    object Workforce : Screen("Workforce", Icons.Default.People)
    object Inventory : Screen("Inventory", Icons.Default.Inventory)
    object Finance : Screen("Finance", Icons.Default.MonetizationOn)
    object Reports : Screen("Reports", Icons.Default.Assessment)
}

class MainActivity : ComponentActivity() {
    private val viewModel: CsomsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: CsomsViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // States from ViewModel
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val branches by viewModel.branches.collectAsStateWithLifecycle()
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val attendance by viewModel.attendance.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val payroll by viewModel.payroll.collectAsStateWithLifecycle()
    val kpis by viewModel.dashboardKpis.collectAsStateWithLifecycle()

    var currentScreenIndex by remember { mutableIntStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Dialog states
    var showRoleSelector by remember { mutableStateOf(false) }
    var showRegisterEmployee by remember { mutableStateOf(false) }
    var showRecordAttendance by remember { mutableStateOf(false) }
    var showEquipmentRequest by remember { mutableStateOf(false) }
    var itemToRestock by remember { mutableStateOf<InventoryItemEntity?>(null) }
    var showGenerateInvoice by remember { mutableStateOf(false) }
    var showRecordPayment by remember { mutableStateOf(false) }
    var showCreateBranch by remember { mutableStateOf(false) }

    // Handle back button when not on dashboard
    BackHandler(enabled = currentScreenIndex != 0) {
        currentScreenIndex = 0
    }

    // Listen to ViewModel snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_mangasi_logo),
                            contentDescription = "Mangasi Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mangasi CSOMS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    RoleBadgeChip(
                        currentRole = currentRole,
                        onClick = { showRoleSelector = true }
                    )
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Switch User Role") },
                                onClick = {
                                    menuExpanded = false
                                    showRoleSelector = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Register Employee") },
                                onClick = {
                                    menuExpanded = false
                                    showRegisterEmployee = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Record Attendance") },
                                onClick = {
                                    menuExpanded = false
                                    showRecordAttendance = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Reset Sample Data") },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.resetDemoData()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    Screen.Dashboard,
                    Screen.Workforce,
                    Screen.Inventory,
                    Screen.Finance,
                    Screen.Reports
                )
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentScreenIndex == index,
                        onClick = { currentScreenIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreenIndex) {
                0 -> DashboardScreen(
                    currentRole = currentRole,
                    kpis = kpis,
                    branches = branches,
                    inventory = inventory,
                    requests = requests,
                    invoices = invoices,
                    attendance = attendance,
                    onOpenRoleSelector = { showRoleSelector = true },
                    onNavigateToWorkforce = { currentScreenIndex = 1 },
                    onNavigateToInventory = { currentScreenIndex = 2 },
                    onNavigateToFinance = { currentScreenIndex = 3 },
                    onNavigateToReports = { currentScreenIndex = 4 },
                    onOpenRegisterEmployee = { showRegisterEmployee = true },
                    onOpenRecordAttendance = { showRecordAttendance = true },
                    onOpenEquipmentRequest = { showEquipmentRequest = true },
                    onOpenGenerateInvoice = { showGenerateInvoice = true },
                    onOpenRecordPayment = { showRecordPayment = true }
                )
                1 -> WorkforceScreen(
                    employees = employees,
                    attendance = attendance,
                    branches = branches,
                    onOpenRegisterEmployee = { showRegisterEmployee = true },
                    onOpenRecordAttendance = { showRecordAttendance = true },
                    onToggleEmployeeStatus = { viewModel.toggleEmployeeActive(it) }
                )
                2 -> InventoryScreen(
                    inventory = inventory,
                    requests = requests,
                    onOpenRestock = { itemToRestock = it },
                    onOpenEquipmentRequest = { showEquipmentRequest = true },
                    onUpdateRequestStatus = { req, st -> viewModel.updateEquipmentRequestStatus(req, st) }
                )
                3 -> FinanceScreen(
                    invoices = invoices,
                    payments = payments,
                    payroll = payroll,
                    onOpenGenerateInvoice = { showGenerateInvoice = true },
                    onOpenRecordPayment = { showRecordPayment = true },
                    onRunPayroll = { period -> viewModel.generatePayroll(period) },
                    onUpdatePayrollStatus = { record, st -> viewModel.updatePayrollStatus(record, st) }
                )
                4 -> ReportsScreen(
                    branches = branches,
                    employees = employees,
                    attendance = attendance,
                    inventory = inventory,
                    invoices = invoices,
                    payroll = payroll,
                    onOpenCreateBranch = { showCreateBranch = true },
                    onExportReport = { reportText ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export CSOMS Report"))
                    }
                )
            }
        }
    }

    // Modal Dialogs
    if (showRoleSelector) {
        RoleSelectorDialog(
            currentRole = currentRole,
            onRoleSelected = { viewModel.setRole(it) },
            onDismiss = { showRoleSelector = false }
        )
    }

    if (showRegisterEmployee) {
        RegisterEmployeeDialog(
            branches = branches,
            onRegister = { name, phone, nida, branch, role, classification, daily, monthly, docs ->
                viewModel.registerEmployee(name, phone, nida, branch, role, classification, daily, monthly, docs)
            },
            onDismiss = { showRegisterEmployee = false }
        )
    }

    if (showRecordAttendance) {
        RecordAttendanceDialog(
            employees = employees.filter { it.isActive },
            onRecord = { date, emp, status, inTime, outTime, notes ->
                viewModel.recordDailyAttendance(date, emp, status, inTime, outTime, notes)
            },
            onDismiss = { showRecordAttendance = false }
        )
    }

    if (showEquipmentRequest) {
        EquipmentRequestDialog(
            branches = branches,
            inventory = inventory,
            onSubmit = { branch, item, qty, urgency, remarks ->
                viewModel.submitEquipmentRequest(branch, item, qty, urgency, remarks)
            },
            onDismiss = { showEquipmentRequest = false }
        )
    }

    itemToRestock?.let { item ->
        RestockItemDialog(
            item = item,
            onRestock = { itemId, hq, branch ->
                viewModel.restockItem(itemId, hq, branch)
            },
            onDismiss = { itemToRestock = null }
        )
    }

    if (showGenerateInvoice) {
        GenerateInvoiceDialog(
            clients = clients,
            onGenerate = { client, contractId, subtotal, dueDate, notes ->
                viewModel.generateInvoice(client, contractId, subtotal, dueDate, notes)
            },
            onDismiss = { showGenerateInvoice = false }
        )
    }

    if (showRecordPayment) {
        RecordPaymentDialog(
            invoices = invoices,
            onRecord = { inv, amt, method, ref ->
                viewModel.recordPayment(inv, amt, method, ref)
            },
            onDismiss = { showRecordPayment = false }
        )
    }

    if (showCreateBranch) {
        CreateBranchDialog(
            onCreate = { name, region, addr, supervisor, phone ->
                viewModel.createBranch(name, region, addr, supervisor, phone)
            },
            onDismiss = { showCreateBranch = false }
        )
    }
}
