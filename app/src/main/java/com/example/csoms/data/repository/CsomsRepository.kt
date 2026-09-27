package com.example.csoms.data.repository

import com.example.csoms.data.local.CsomsDao
import com.example.csoms.data.local.CsomsDatabase
import com.example.csoms.data.model.AttendanceEntity
import com.example.csoms.data.model.BranchEntity
import com.example.csoms.data.model.ClientEntity
import com.example.csoms.data.model.ContractEntity
import com.example.csoms.data.model.EmployeeEntity
import com.example.csoms.data.model.EquipmentRequestEntity
import com.example.csoms.data.model.InventoryItemEntity
import com.example.csoms.data.model.InvoiceEntity
import com.example.csoms.data.model.PaymentEntity
import com.example.csoms.data.model.PayrollRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsomsRepository(private val dao: CsomsDao) {

    val allBranches: Flow<List<BranchEntity>> = dao.getAllBranches()
    val allEmployees: Flow<List<EmployeeEntity>> = dao.getAllEmployees()
    val allAttendance: Flow<List<AttendanceEntity>> = dao.getAllAttendance()
    val allInventory: Flow<List<InventoryItemEntity>> = dao.getAllInventoryItems()
    val allRequests: Flow<List<EquipmentRequestEntity>> = dao.getAllEquipmentRequests()
    val allClients: Flow<List<ClientEntity>> = dao.getAllClients()
    val allContracts: Flow<List<ContractEntity>> = dao.getAllContracts()
    val allInvoices: Flow<List<InvoiceEntity>> = dao.getAllInvoices()
    val allPayments: Flow<List<PaymentEntity>> = dao.getAllPayments()
    val allPayroll: Flow<List<PayrollRecordEntity>> = dao.getAllPayrollRecords()

    suspend fun getEmployeeById(id: Long) = dao.getEmployeeById(id)

    suspend fun insertBranch(branch: BranchEntity) = dao.insertBranch(branch)
    suspend fun updateBranch(branch: BranchEntity) = dao.updateBranch(branch)

    suspend fun insertEmployee(employee: EmployeeEntity) = dao.insertEmployee(employee)
    suspend fun updateEmployee(employee: EmployeeEntity) = dao.updateEmployee(employee)
    suspend fun deactivateEmployee(employee: EmployeeEntity) {
        dao.updateEmployee(employee.copy(isActive = !employee.isActive))
    }

    // Attendance with duplicate prevention constraint & automatic wage computation
    sealed class AttendanceResult {
        data class Success(val id: Long) : AttendanceResult()
        data class DuplicateError(val message: String) : AttendanceResult()
    }

    suspend fun recordAttendance(
        date: String,
        employee: EmployeeEntity,
        status: String,
        checkInTime: String,
        checkOutTime: String,
        notes: String
    ): AttendanceResult {
        // Prevent duplicate attendance for the same employee on the same date
        val existing = dao.getAttendanceForEmployeeOnDate(date, employee.id)
        if (existing != null) {
            return AttendanceResult.DuplicateError(
                "Attendance already recorded for ${employee.fullName} on $date (${existing.status})"
            )
        }

        // Calculate wage if non-contract
        val wage = if (employee.classification == "NON_CONTRACT") {
            when (status) {
                "PRESENT" -> employee.dailyRateTzs
                "HALF_DAY" -> employee.dailyRateTzs * 0.5
                else -> 0.0
            }
        } else {
            0.0 // Contract employees receive monthly salary via payroll
        }

        val attendance = AttendanceEntity(
            date = date,
            employeeId = employee.id,
            employeeName = employee.fullName,
            branchId = employee.branchId,
            branchName = employee.branchName,
            status = status,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
            wageEarnedTzs = wage,
            notes = notes
        )

        val id = dao.insertAttendance(attendance)
        return AttendanceResult.Success(id)
    }

    suspend fun insertInventoryItem(item: InventoryItemEntity) = dao.insertInventoryItem(item)
    suspend fun updateInventoryItem(item: InventoryItemEntity) = dao.updateInventoryItem(item)

    suspend fun restockItem(itemId: Long, addHQ: Int, addBranch: Int) {
        val items = dao.getAllInventoryItems().firstOrNull() ?: return
        val item = items.find { it.id == itemId } ?: return
        val updated = item.copy(
            headOfficeStock = item.headOfficeStock + addHQ,
            branchStock = item.branchStock + addBranch,
            lastRestockedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        dao.updateInventoryItem(updated)
    }

    suspend fun insertEquipmentRequest(request: EquipmentRequestEntity) =
        dao.insertEquipmentRequest(request)

    suspend fun updateRequestStatus(request: EquipmentRequestEntity, newStatus: String, notes: String = "") {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updated = request.copy(
            status = newStatus,
            remarks = if (notes.isNotBlank()) notes else request.remarks,
            dispatchedDate = if (newStatus == "DISPATCHED") today else request.dispatchedDate
        )
        dao.updateEquipmentRequest(updated)

        // If dispatched, update branch stock and deduct from HQ if store keeper approves
        if (newStatus == "DISPATCHED") {
            val items = dao.getAllInventoryItems().firstOrNull() ?: return
            val item = items.find { it.id == request.itemId }
            if (item != null && item.headOfficeStock >= request.quantityRequested) {
                dao.updateInventoryItem(
                    item.copy(
                        headOfficeStock = item.headOfficeStock - request.quantityRequested,
                        branchStock = item.branchStock + request.quantityRequested
                    )
                )
            }
        }
    }

    suspend fun insertClient(client: ClientEntity) = dao.insertClient(client)
    suspend fun insertContract(contract: ContractEntity) = dao.insertContract(contract)

    suspend fun generateInvoice(
        clientId: Long,
        clientName: String,
        contractId: Long,
        subtotalTzs: Double,
        dueDate: String,
        notes: String
    ): Long {
        val vatRate = 0.18 // 18% Tanzanian VAT
        val vatAmount = subtotalTzs * vatRate
        val total = subtotalTzs + vatAmount
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val count = (dao.getAllInvoices().firstOrNull()?.size ?: 0) + 1
        val invCode = "INV-2026-%04d".format(count)

        val invoice = InvoiceEntity(
            invoiceNumber = invCode,
            clientId = clientId,
            clientName = clientName,
            contractId = contractId,
            issueDate = today,
            dueDate = dueDate,
            subtotalTzs = subtotalTzs,
            vatRate = vatRate,
            vatAmountTzs = vatAmount,
            totalAmountTzs = total,
            paidAmountTzs = 0.0,
            status = "UNPAID",
            notes = notes
        )
        return dao.insertInvoice(invoice)
    }

    suspend fun recordPayment(
        invoice: InvoiceEntity,
        amountTzs: Double,
        paymentMethod: String,
        referenceNumber: String
    ): Long {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val count = (dao.getAllPayments().firstOrNull()?.size ?: 0) + 1
        val rctCode = "RCT-2026-%04d".format(count)

        val payment = PaymentEntity(
            receiptNumber = rctCode,
            invoiceId = invoice.id,
            invoiceNumber = invoice.invoiceNumber,
            clientName = invoice.clientName,
            paymentDate = today,
            amountTzs = amountTzs,
            paymentMethod = paymentMethod,
            referenceNumber = referenceNumber
        )
        val id = dao.insertPayment(payment)

        // Update invoice paid amount & status
        val newPaid = invoice.paidAmountTzs + amountTzs
        val newStatus = when {
            newPaid >= invoice.totalAmountTzs -> "PAID"
            newPaid > 0.0 -> "PARTIALLY_PAID"
            else -> invoice.status
        }
        dao.updateInvoice(invoice.copy(paidAmountTzs = newPaid, status = newStatus))
        return id
    }

    suspend fun generateMonthlyPayrollRun(period: String): Int {
        val employees = dao.getAllEmployees().firstOrNull() ?: emptyList()
        val allAtt = dao.getAllAttendance().firstOrNull() ?: emptyList()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val records = mutableListOf<PayrollRecordEntity>()
        for (emp in employees.filter { it.isActive }) {
            val empAttendance = allAtt.filter { it.employeeId == emp.id }
            val presentDays = empAttendance.count { it.status == "PRESENT" }
            val halfDays = empAttendance.count { it.status == "HALF_DAY" }
            val effectiveDays = presentDays + (if (empAttendance.isEmpty()) 24 else 0) // default realistic days if fresh

            val basePay: Double
            val allowances: Double
            val deductions: Double
            val netPay: Double

            if (emp.classification == "CONTRACT") {
                basePay = emp.monthlySalaryTzs
                allowances = 40000.0 // Standard transport / lunch allowance
                deductions = basePay * 0.10 // 10% statutory NSSF contribution in Tanzania
                netPay = basePay + allowances - deductions
            } else {
                basePay = (effectiveDays * emp.dailyRateTzs)
                allowances = 0.0
                deductions = 0.0
                netPay = basePay
            }

            records.add(
                PayrollRecordEntity(
                    payrollPeriod = period,
                    employeeId = emp.id,
                    employeeName = emp.fullName,
                    employeeCode = emp.employeeCode,
                    classification = emp.classification,
                    branchName = emp.branchName,
                    daysAttended = effectiveDays,
                    basePayTzs = basePay,
                    allowanceTzs = allowances,
                    deductionsTzs = deductions,
                    netPayTzs = netPay,
                    paymentStatus = "PENDING",
                    processedDate = today
                )
            )
        }
        dao.insertPayrollRecords(records)
        return records.size
    }

    suspend fun updatePayrollStatus(record: PayrollRecordEntity, newStatus: String) {
        dao.updatePayrollRecord(record.copy(paymentStatus = newStatus))
    }

    suspend fun resetDatabase() {
        CsomsDatabase.populateInitialData(dao)
    }
}
