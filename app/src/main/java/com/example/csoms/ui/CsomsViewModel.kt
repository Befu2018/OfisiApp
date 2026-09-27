package com.example.csoms.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.csoms.data.local.CsomsDatabase
import com.example.csoms.data.model.AttendanceEntity
import com.example.csoms.data.model.BranchEntity
import com.example.csoms.data.model.ClientEntity
import com.example.csoms.data.model.ContractEntity
import com.example.csoms.data.model.EmployeeEntity
import com.example.csoms.data.model.EquipmentRequestEntity
import com.example.csoms.data.model.InventoryItemEntity
import com.example.csoms.data.model.InvoiceEntity
import com.example.csoms.data.model.PayrollRecordEntity
import com.example.csoms.data.model.UserRole
import com.example.csoms.data.repository.CsomsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardKpis(
    val totalEmployees: Int = 0,
    val activeContractsCount: Int = 0,
    val todayAttendancePresent: Int = 0,
    val lowStockCount: Int = 0,
    val pendingRequestsCount: Int = 0,
    val totalInvoicedTzs: Double = 0.0,
    val totalCollectedTzs: Double = 0.0,
    val outstandingDebtorsTzs: Double = 0.0,
    val currentPayrollEstimateTzs: Double = 0.0
)

class CsomsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CsomsRepository

    init {
        val db = CsomsDatabase.getDatabase(application, viewModelScope)
        repository = CsomsRepository(db.csomsDao())
    }

    // Role state
    private val _currentRole = MutableStateFlow(UserRole.SYSTEM_ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Branch filter
    private val _selectedBranch = MutableStateFlow<BranchEntity?>(null)
    val selectedBranch: StateFlow<BranchEntity?> = _selectedBranch.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Notification / Toast Message
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Flows from repository
    val branches: StateFlow<List<BranchEntity>> = repository.allBranches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val employees: StateFlow<List<EmployeeEntity>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItemEntity>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val requests: StateFlow<List<EquipmentRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contracts: StateFlow<List<ContractEntity>> = repository.allContracts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payroll: StateFlow<List<PayrollRecordEntity>> = repository.allPayroll
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-time Dashboard KPIs computation
    val dashboardKpis: StateFlow<DashboardKpis> = combine(
        employees, contracts, attendance, inventory
    ) { emps, cnts, atts, invs ->
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayPresent = atts.count { it.date == today && it.status == "PRESENT" }
        val lowStock = invs.count { (it.headOfficeStock + it.branchStock) <= it.minStockThreshold }
        val pendingReqs = requests.value.count { it.status == "PENDING" }
        val totalInvoiced = invoices.value.sumOf { it.totalAmountTzs }
        val totalPaid = invoices.value.sumOf { it.paidAmountTzs }
        val outstanding = totalInvoiced - totalPaid
        val payrollTotal = payroll.value.sumOf { it.netPayTzs }

        DashboardKpis(
            totalEmployees = emps.count { it.isActive },
            activeContractsCount = cnts.count { it.status == "ACTIVE" },
            todayAttendancePresent = todayPresent,
            lowStockCount = lowStock,
            pendingRequestsCount = pendingReqs,
            totalInvoicedTzs = totalInvoiced,
            totalCollectedTzs = totalPaid,
            outstandingDebtorsTzs = outstanding.coerceAtLeast(0.0),
            currentPayrollEstimateTzs = payrollTotal
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardKpis())

    fun setRole(role: UserRole) {
        _currentRole.value = role
        emitMessage("Switched role to: ${role.displayName}")
    }

    fun setSelectedBranch(branch: BranchEntity?) {
        _selectedBranch.value = branch
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun emitMessage(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }

    // --- Actions ---

    // 1. Employee Management
    fun registerEmployee(
        fullName: String,
        phone: String,
        nidaNumber: String,
        branch: BranchEntity,
        roleTitle: String,
        classification: String,
        dailyRateTzs: Double,
        monthlySalaryTzs: Double,
        documentsStatus: String
    ) {
        viewModelScope.launch {
            val count = (employees.value.size) + 1
            val code = "MCS-%03d".format(count)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val employee = EmployeeEntity(
                employeeCode = code,
                fullName = fullName,
                phone = phone,
                nidaNumber = nidaNumber,
                branchId = branch.id,
                branchName = branch.name,
                roleTitle = roleTitle,
                classification = classification,
                dailyRateTzs = dailyRateTzs,
                monthlySalaryTzs = monthlySalaryTzs,
                isActive = true,
                hireDate = today,
                documentsStatus = documentsStatus
            )
            repository.insertEmployee(employee)
            emitMessage("Registered employee $fullName ($code)")
        }
    }

    fun toggleEmployeeActive(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.deactivateEmployee(employee)
            val state = if (employee.isActive) "Deactivated" else "Activated"
            emitMessage("$state ${employee.fullName}")
        }
    }

    // 2. Attendance Management with Duplicate Prevention
    fun recordDailyAttendance(
        date: String,
        employee: EmployeeEntity,
        status: String,
        checkInTime: String,
        checkOutTime: String,
        notes: String
    ) {
        viewModelScope.launch {
            val result = repository.recordAttendance(
                date = date,
                employee = employee,
                status = status,
                checkInTime = checkInTime,
                checkOutTime = checkOutTime,
                notes = notes
            )
            when (result) {
                is CsomsRepository.AttendanceResult.Success -> {
                    emitMessage("Recorded $status for ${employee.fullName} on $date")
                }
                is CsomsRepository.AttendanceResult.DuplicateError -> {
                    emitMessage("⚠️ ${result.message}")
                }
            }
        }
    }

    // 3. Inventory & Restock
    fun addInventoryItem(
        name: String,
        category: String,
        unit: String,
        headOfficeStock: Int,
        branchStock: Int,
        minStockThreshold: Int,
        unitCostTzs: Double
    ) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val item = InventoryItemEntity(
                name = name,
                category = category,
                unit = unit,
                headOfficeStock = headOfficeStock,
                branchStock = branchStock,
                minStockThreshold = minStockThreshold,
                unitCostTzs = unitCostTzs,
                lastRestockedDate = today
            )
            repository.insertInventoryItem(item)
            emitMessage("Added inventory item: $name")
        }
    }

    fun restockItem(itemId: Long, addHQ: Int, addBranch: Int) {
        viewModelScope.launch {
            repository.restockItem(itemId, addHQ, addBranch)
            emitMessage("Stock updated successfully")
        }
    }

    // 4. Equipment Request
    fun submitEquipmentRequest(
        branch: BranchEntity,
        item: InventoryItemEntity,
        quantity: Int,
        urgency: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val count = (requests.value.size) + 1
            val reqCode = "REQ-2026-%04d".format(count)
            val request = EquipmentRequestEntity(
                requestCode = reqCode,
                branchId = branch.id,
                branchName = branch.name,
                requestedBy = branch.supervisorName,
                itemId = item.id,
                itemName = item.name,
                category = item.category,
                quantityRequested = quantity,
                urgency = urgency,
                requestDate = today,
                status = "PENDING",
                remarks = remarks
            )
            repository.insertEquipmentRequest(request)
            emitMessage("Submitted request $reqCode for ${branch.name}")
        }
    }

    fun updateEquipmentRequestStatus(
        request: EquipmentRequestEntity,
        newStatus: String,
        remarks: String = ""
    ) {
        viewModelScope.launch {
            repository.updateRequestStatus(request, newStatus, remarks)
            emitMessage("Request ${request.requestCode} status updated to $newStatus")
        }
    }

    // 5. Client & Contracts
    fun registerClient(
        name: String,
        industry: String,
        contactPerson: String,
        phone: String,
        email: String,
        address: String,
        branchName: String
    ) {
        viewModelScope.launch {
            val client = ClientEntity(
                name = name,
                industry = industry,
                contactPerson = contactPerson,
                phone = phone,
                email = email,
                address = address,
                branchName = branchName
            )
            repository.insertClient(client)
            emitMessage("Registered client: $name")
        }
    }

    fun createContract(
        client: ClientEntity,
        branchName: String,
        serviceType: String,
        monthlyValueTzs: Double,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            val count = (contracts.value.size) + 1
            val code = "MNG-CNT-2026-%02d".format(count)
            val contract = ContractEntity(
                contractCode = code,
                clientId = client.id,
                clientName = client.name,
                branchName = branchName,
                serviceType = serviceType,
                monthlyValueTzs = monthlyValueTzs,
                startDate = startDate,
                endDate = endDate
            )
            repository.insertContract(contract)
            emitMessage("Created service contract $code for ${client.name}")
        }
    }

    // 6. Invoicing with 18% Tanzanian VAT
    fun generateInvoice(
        client: ClientEntity,
        contractId: Long,
        subtotalTzs: Double,
        dueDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.generateInvoice(
                clientId = client.id,
                clientName = client.name,
                contractId = contractId,
                subtotalTzs = subtotalTzs,
                dueDate = dueDate,
                notes = notes
            )
            emitMessage("Generated VAT Invoice with 18% VAT for ${client.name}")
        }
    }

    // 7. Payment Recording
    fun recordPayment(
        invoice: InvoiceEntity,
        amountTzs: Double,
        paymentMethod: String,
        referenceNumber: String
    ) {
        viewModelScope.launch {
            repository.recordPayment(invoice, amountTzs, paymentMethod, referenceNumber)
            emitMessage("Recorded payment of ${formatTzs(amountTzs)} for ${invoice.invoiceNumber}")
        }
    }

    // 8. Payroll Processing
    fun generatePayroll(period: String) {
        viewModelScope.launch {
            val count = repository.generateMonthlyPayrollRun(period)
            emitMessage("Processed payroll run for $period ($count staff records generated)")
        }
    }

    fun updatePayrollStatus(record: PayrollRecordEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updatePayrollStatus(record, newStatus)
            emitMessage("Payroll for ${record.employeeName} updated to $newStatus")
        }
    }

    // 9. Branch Management
    fun createBranch(
        name: String,
        region: String,
        address: String,
        supervisorName: String,
        contactPhone: String
    ) {
        viewModelScope.launch {
            val branch = BranchEntity(
                name = name,
                region = region,
                address = address,
                supervisorName = supervisorName,
                contactPhone = contactPhone,
                staffCount = 0
            )
            repository.insertBranch(branch)
            emitMessage("Created branch: $name in $region")
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDatabase()
            emitMessage("Reset system data to standard demo state")
        }
    }

    companion object {
        fun formatTzs(amount: Double): String {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            return "TSh " + formatter.format(amount.toLong())
        }
    }
}
