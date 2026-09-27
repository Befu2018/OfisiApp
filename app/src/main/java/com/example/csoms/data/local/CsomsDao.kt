package com.example.csoms.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface CsomsDao {

    // Branches
    @Query("SELECT * FROM branches ORDER BY name ASC")
    fun getAllBranches(): Flow<List<BranchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: BranchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<BranchEntity>)

    @Update
    suspend fun updateBranch(branch: BranchEntity)

    @Delete
    suspend fun deleteBranch(branch: BranchEntity)

    // Employees
    @Query("SELECT * FROM employees ORDER BY fullName ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE branchId = :branchId ORDER BY fullName ASC")
    fun getEmployeesByBranch(branchId: Long): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Long): EmployeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeEntity>)

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Delete
    suspend fun deleteEmployee(employee: EmployeeEntity)

    // Attendance
    @Query("SELECT * FROM attendance ORDER BY date DESC, employeeName ASC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date ORDER BY employeeName ASC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date AND employeeId = :employeeId LIMIT 1")
    suspend fun getAttendanceForEmployeeOnDate(date: String, employeeId: Long): AttendanceEntity?

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND date LIKE :monthPrefix || '%'")
    fun getAttendanceForEmployeeInMonth(employeeId: Long, monthPrefix: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceEntity>)

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)

    // Inventory
    @Query("SELECT * FROM inventory_items ORDER BY category ASC, name ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItems(items: List<InventoryItemEntity>)

    @Update
    suspend fun updateInventoryItem(item: InventoryItemEntity)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItemEntity)

    // Equipment Requests
    @Query("SELECT * FROM equipment_requests ORDER BY requestDate DESC, id DESC")
    fun getAllEquipmentRequests(): Flow<List<EquipmentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentRequest(request: EquipmentRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentRequests(requests: List<EquipmentRequestEntity>)

    @Update
    suspend fun updateEquipmentRequest(request: EquipmentRequestEntity)

    // Clients & Contracts
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Query("SELECT * FROM contracts ORDER BY startDate DESC")
    fun getAllContracts(): Flow<List<ContractEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContract(contract: ContractEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContracts(contracts: List<ContractEntity>)

    @Update
    suspend fun updateContract(contract: ContractEntity)

    // Invoices & Payments
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    // Payroll Records
    @Query("SELECT * FROM payroll_records ORDER BY processedDate DESC, employeeName ASC")
    fun getAllPayrollRecords(): Flow<List<PayrollRecordEntity>>

    @Query("SELECT * FROM payroll_records WHERE payrollPeriod = :period ORDER BY employeeName ASC")
    fun getPayrollByPeriod(period: String): Flow<List<PayrollRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayrollRecord(record: PayrollRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayrollRecords(records: List<PayrollRecordEntity>)

    @Update
    suspend fun updatePayrollRecord(record: PayrollRecordEntity)
}
