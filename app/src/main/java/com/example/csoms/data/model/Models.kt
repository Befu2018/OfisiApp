package com.example.csoms.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole(val displayName: String, val badgeTitle: String, val description: String) {
    SYSTEM_ADMIN("System Admin", "Admin", "System configuration, user access, and global settings"),
    MANAGEMENT("Management", "Executive", "Executive dashboards, strategic reports, and branch comparisons"),
    HR_OFFICER("HR Officer", "Human Resources", "Employee lifecycle, contracts, documents, and classifications"),
    BRANCH_SUPERVISOR("Branch Supervisor", "Field Ops", "Daily attendance recording, shifts, and equipment requests"),
    STORE_KEEPER("Store Keeper", "Inventory", "Cleaning tools, consumables stock, approvals, and dispatches"),
    ACCOUNTANT("Accountant", "Finance", "Daily & monthly payroll, VAT invoicing (18%), and client payments")
}

enum class EmployeeClassification(val label: String) {
    CONTRACT("Contract (Monthly)"),
    NON_CONTRACT("Non-Contract (Daily Casual)")
}

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    HALF_DAY("Half Day"),
    ABSENT("Absent"),
    SICK_LEAVE("Sick Leave")
}

enum class ItemCategory(val label: String) {
    EQUIPMENT("Tools & Machines"),
    CONSUMABLE("Cleaning Consumables")
}

enum class RequestStatus(val label: String) {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    DISPATCHED("Dispatched to Branch"),
    RECEIVED("Received at Branch"),
    REJECTED("Rejected")
}

enum class RequestUrgency(val label: String) {
    NORMAL("Normal"),
    URGENT("Urgent"),
    CRITICAL("Critical Emergency")
}

enum class ContractStatus(val label: String) {
    ACTIVE("Active"),
    RENEWED("Renewed"),
    EXPIRED("Expired"),
    TERMINATED("Terminated")
}

enum class InvoiceStatus(val label: String) {
    UNPAID("Unpaid"),
    PARTIALLY_PAID("Partially Paid"),
    PAID("Paid in Full"),
    OVERDUE("Overdue")
}

enum class PaymentMethod(val label: String) {
    BANK_TRANSFER("CRDB / NMB Bank Transfer"),
    M_PESA_LIPA("Vodacom M-Pesa Lipa"),
    TIGO_PESA("Tigo Pesa Lipa"),
    CHEQUE("Bank Cheque"),
    CASH("Direct Cash")
}

@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val region: String,
    val address: String,
    val supervisorName: String,
    val contactPhone: String,
    val staffCount: Int = 0
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeCode: String,
    val fullName: String,
    val phone: String,
    val nidaNumber: String,
    val branchId: Long,
    val branchName: String,
    val roleTitle: String,
    val classification: String, // CONTRACT or NON_CONTRACT
    val dailyRateTzs: Double = 0.0,
    val monthlySalaryTzs: Double = 0.0,
    val isActive: Boolean = true,
    val hireDate: String,
    val documentsStatus: String = "NIDA Verified • Work Contract • Medical Clearance"
)

@Entity(
    tableName = "attendance",
    indices = [Index(value = ["date", "employeeId"], unique = true)]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val employeeId: Long,
    val employeeName: String,
    val branchId: Long,
    val branchName: String,
    val status: String, // PRESENT, HALF_DAY, ABSENT, SICK_LEAVE
    val checkInTime: String,
    val checkOutTime: String,
    val wageEarnedTzs: Double = 0.0,
    val notes: String = ""
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // EQUIPMENT or CONSUMABLE
    val unit: String, // Unit, Liters, Pack, Bottles
    val headOfficeStock: Int,
    val branchStock: Int,
    val minStockThreshold: Int,
    val unitCostTzs: Double,
    val lastRestockedDate: String
)

@Entity(tableName = "equipment_requests")
data class EquipmentRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestCode: String,
    val branchId: Long,
    val branchName: String,
    val requestedBy: String,
    val itemId: Long,
    val itemName: String,
    val category: String,
    val quantityRequested: Int,
    val urgency: String, // NORMAL, URGENT, CRITICAL
    val requestDate: String,
    val status: String, // PENDING, APPROVED, DISPATCHED, RECEIVED, REJECTED
    val remarks: String = "",
    val dispatchedDate: String = ""
)

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val industry: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val address: String,
    val branchName: String,
    val activeContractsCount: Int = 1
)

@Entity(tableName = "contracts")
data class ContractEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractCode: String,
    val clientId: Long,
    val clientName: String,
    val branchName: String,
    val serviceType: String,
    val monthlyValueTzs: Double,
    val startDate: String,
    val endDate: String,
    val billingCycle: String = "Monthly",
    val status: String = "ACTIVE"
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val clientId: Long,
    val clientName: String,
    val contractId: Long,
    val issueDate: String,
    val dueDate: String,
    val subtotalTzs: Double,
    val vatRate: Double = 0.18, // 18% Tanzanian VAT
    val vatAmountTzs: Double,
    val totalAmountTzs: Double,
    val paidAmountTzs: Double = 0.0,
    val status: String = "UNPAID", // UNPAID, PARTIALLY_PAID, PAID, OVERDUE
    val notes: String = ""
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNumber: String,
    val invoiceId: Long,
    val invoiceNumber: String,
    val clientName: String,
    val paymentDate: String,
    val amountTzs: Double,
    val paymentMethod: String,
    val referenceNumber: String
)

@Entity(tableName = "payroll_records")
data class PayrollRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val payrollPeriod: String, // e.g., "January 2026"
    val employeeId: Long,
    val employeeName: String,
    val employeeCode: String,
    val classification: String, // CONTRACT or NON_CONTRACT
    val branchName: String,
    val daysAttended: Int,
    val basePayTzs: Double,
    val allowanceTzs: Double = 0.0,
    val deductionsTzs: Double = 0.0, // NSSF 10%, PAYE, etc.
    val netPayTzs: Double,
    val paymentStatus: String = "PENDING", // PENDING, APPROVED, PAID
    val processedDate: String
)
