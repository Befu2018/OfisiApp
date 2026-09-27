package com.example.csoms.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BranchEntity::class,
        EmployeeEntity::class,
        AttendanceEntity::class,
        InventoryItemEntity::class,
        EquipmentRequestEntity::class,
        ClientEntity::class,
        ContractEntity::class,
        InvoiceEntity::class,
        PaymentEntity::class,
        PayrollRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CsomsDatabase : RoomDatabase() {

    abstract fun csomsDao(): CsomsDao

    companion object {
        @Volatile
        private var INSTANCE: CsomsDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CsomsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CsomsDatabase::class.java,
                    "mangasi_csoms_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.csomsDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: CsomsDao) {
            // 1. Initial Tanzanian Branches
            val branches = listOf(
                BranchEntity(
                    id = 1,
                    name = "Dar es Salaam HQ & Ilala",
                    region = "Dar es Salaam",
                    address = "Plot 42, Lumumba Street, Kariakoo/CBD",
                    supervisorName = "Baraka Juma Mwinyi",
                    contactPhone = "+255 754 112 334",
                    staffCount = 18
                ),
                BranchEntity(
                    id = 2,
                    name = "Dar es Salaam - Kinondoni",
                    region = "Dar es Salaam",
                    address = "Ali Hassan Mwinyi Rd, Victoria Plaza",
                    supervisorName = "Amina Saidi Salum",
                    contactPhone = "+255 765 223 445",
                    staffCount = 12
                ),
                BranchEntity(
                    id = 3,
                    name = "Arusha Central Branch",
                    region = "Arusha",
                    address = "Goliondoi Road, Clock Tower Complex",
                    supervisorName = "Emanuel Lucas Mollel",
                    contactPhone = "+255 784 334 556",
                    staffCount = 8
                ),
                BranchEntity(
                    id = 4,
                    name = "Dodoma Capital Branch",
                    region = "Dodoma",
                    address = "Kikuyu South, Government City Corridor",
                    supervisorName = "Grace Peter Mwambene",
                    contactPhone = "+255 712 445 667",
                    staffCount = 10
                ),
                BranchEntity(
                    id = 5,
                    name = "Mwanza Lake Zone",
                    region = "Mwanza",
                    address = "Nyerere Road, Capri Point Commercial",
                    supervisorName = "Daudi John Machumu",
                    contactPhone = "+255 755 556 778",
                    staffCount = 7
                ),
                BranchEntity(
                    id = 6,
                    name = "Zanzibar Urban Branch",
                    region = "Zanzibar",
                    address = "Mizingani Road, Stone Town Harbor Office",
                    supervisorName = "Fatma Khamis Nassor",
                    contactPhone = "+255 777 667 889",
                    staffCount = 6
                )
            )
            dao.insertBranches(branches)

            // 2. Initial Employees (Contract & Non-Contract)
            val employees = listOf(
                EmployeeEntity(
                    id = 1,
                    employeeCode = "MCS-001",
                    fullName = "Josephat Julius Kazi",
                    phone = "+255 713 100 201",
                    nidaNumber = "19880412-11101-00001-22",
                    branchId = 1,
                    branchName = "Dar es Salaam HQ & Ilala",
                    roleTitle = "Senior Deep Clean Specialist",
                    classification = "CONTRACT",
                    dailyRateTzs = 0.0,
                    monthlySalaryTzs = 650000.0,
                    isActive = true,
                    hireDate = "2024-03-15",
                    documentsStatus = "NIDA Verified • 2-Yr Contract • Medical Passed"
                ),
                EmployeeEntity(
                    id = 2,
                    employeeCode = "MCS-002",
                    fullName = "Asha Ramadhani Kibena",
                    phone = "+255 754 200 302",
                    nidaNumber = "19930825-11102-00002-31",
                    branchId = 1,
                    branchName = "Dar es Salaam HQ & Ilala",
                    roleTitle = "Office & Janitorial Cleaner",
                    classification = "CONTRACT",
                    dailyRateTzs = 0.0,
                    monthlySalaryTzs = 480000.0,
                    isActive = true,
                    hireDate = "2024-06-01",
                    documentsStatus = "NIDA Verified • Indefinite Contract • Guarantor Letter"
                ),
                EmployeeEntity(
                    id = 3,
                    employeeCode = "MCS-003",
                    fullName = "Michael Deo Mrema",
                    phone = "+255 762 300 403",
                    nidaNumber = "19961104-11103-00003-19",
                    branchId = 1,
                    branchName = "Dar es Salaam HQ & Ilala",
                    roleTitle = "Casual Floor Polisher",
                    classification = "NON_CONTRACT",
                    dailyRateTzs = 18000.0,
                    monthlySalaryTzs = 0.0,
                    isActive = true,
                    hireDate = "2025-01-10",
                    documentsStatus = "NIDA Verified • Casual Daily Agreement • ID Attached"
                ),
                EmployeeEntity(
                    id = 4,
                    employeeCode = "MCS-004",
                    fullName = "Zulfa Rashid Mwenda",
                    phone = "+255 788 400 504",
                    nidaNumber = "19970219-11104-00004-45",
                    branchId = 2,
                    branchName = "Dar es Salaam - Kinondoni",
                    roleTitle = "Sanitation & Hygiene Attendant",
                    classification = "NON_CONTRACT",
                    dailyRateTzs = 15000.0,
                    monthlySalaryTzs = 0.0,
                    isActive = true,
                    hireDate = "2025-02-01",
                    documentsStatus = "NIDA Card Pending • Local Intro Letter Attached"
                ),
                EmployeeEntity(
                    id = 5,
                    employeeCode = "MCS-005",
                    fullName = "Kelvin Elias Tarimo",
                    phone = "+255 715 500 605",
                    nidaNumber = "19901201-21101-00005-55",
                    branchId = 3,
                    branchName = "Arusha Central Branch",
                    roleTitle = "Industrial Scrubber Operator",
                    classification = "CONTRACT",
                    dailyRateTzs = 0.0,
                    monthlySalaryTzs = 580000.0,
                    isActive = true,
                    hireDate = "2024-08-15",
                    documentsStatus = "NIDA Verified • Safety Certified • Contract Signed"
                ),
                EmployeeEntity(
                    id = 6,
                    employeeCode = "MCS-006",
                    fullName = "Neema Charles Shayo",
                    phone = "+255 767 600 706",
                    nidaNumber = "19950515-21102-00006-67",
                    branchId = 3,
                    branchName = "Arusha Central Branch",
                    roleTitle = "Commercial Window Cleaner",
                    classification = "NON_CONTRACT",
                    dailyRateTzs = 16000.0,
                    monthlySalaryTzs = 0.0,
                    isActive = true,
                    hireDate = "2025-03-01",
                    documentsStatus = "NIDA Verified • Working at Heights Cert"
                ),
                EmployeeEntity(
                    id = 7,
                    employeeCode = "MCS-007",
                    fullName = "Said Hamis Bakari",
                    phone = "+255 773 700 807",
                    nidaNumber = "19920910-51101-00007-88",
                    branchId = 6,
                    branchName = "Zanzibar Urban Branch",
                    roleTitle = "Resort Hospitality Cleaner",
                    classification = "CONTRACT",
                    dailyRateTzs = 0.0,
                    monthlySalaryTzs = 520000.0,
                    isActive = true,
                    hireDate = "2024-11-20",
                    documentsStatus = "Zanzibar Resident ID • Medical Health Cert"
                ),
                EmployeeEntity(
                    id = 8,
                    employeeCode = "MCS-008",
                    fullName = "Hassan Rajab Mwinyi",
                    phone = "+255 714 800 908",
                    nidaNumber = "19980708-31101-00008-90",
                    branchId = 4,
                    branchName = "Dodoma Capital Branch",
                    roleTitle = "Daily Government Office Janitor",
                    classification = "NON_CONTRACT",
                    dailyRateTzs = 15000.0,
                    monthlySalaryTzs = 0.0,
                    isActive = true,
                    hireDate = "2025-04-12",
                    documentsStatus = "NIDA Verified • Security Background Clearance"
                )
            )
            dao.insertEmployees(employees)

            // 3. Today's Attendance Sample (Pre-prevented duplicates, clean records)
            val attendances = listOf(
                AttendanceEntity(
                    id = 1,
                    date = "2026-09-27",
                    employeeId = 1,
                    employeeName = "Josephat Julius Kazi",
                    branchId = 1,
                    branchName = "Dar es Salaam HQ & Ilala",
                    status = "PRESENT",
                    checkInTime = "06:45 AM",
                    checkOutTime = "04:30 PM",
                    wageEarnedTzs = 0.0, // Salaried Contract
                    notes = "Supervised CRDB Tower bank floor deep clean"
                ),
                AttendanceEntity(
                    id = 2,
                    date = "2026-09-27",
                    employeeId = 2,
                    employeeName = "Asha Ramadhani Kibena",
                    branchId = 1,
                    branchName = "Dar es Salaam HQ & Ilala",
                    status = "PRESENT",
                    checkInTime = "07:00 AM",
                    checkOutTime = "04:00 PM",
                    wageEarnedTzs = 0.0,
                    notes = "Lobby and reception sanitization completed"
                ),
                AttendanceEntity(
                    id = 3,
                    date = "2026-09-27",
                    employeeId = 3,
                    employeeName = "Michael Deo Mrema",
                    branchId = 1,
                    branchName = "Dar es Salaam HQ & Ilala",
                    status = "PRESENT",
                    checkInTime = "07:15 AM",
                    checkOutTime = "05:00 PM",
                    wageEarnedTzs = 18000.0, // Full day casual wage
                    notes = "Handled floor polishing machine at Serena Hotel"
                ),
                AttendanceEntity(
                    id = 4,
                    date = "2026-09-27",
                    employeeId = 4,
                    employeeName = "Zulfa Rashid Mwenda",
                    branchId = 2,
                    branchName = "Dar es Salaam - Kinondoni",
                    status = "HALF_DAY",
                    checkInTime = "07:00 AM",
                    checkOutTime = "12:00 PM",
                    wageEarnedTzs = 7500.0, // Half day casual wage
                    notes = "Approved half-day due to clinic visit"
                ),
                AttendanceEntity(
                    id = 5,
                    date = "2026-09-27",
                    employeeId = 5,
                    employeeName = "Kelvin Elias Tarimo",
                    branchId = 3,
                    branchName = "Arusha Central Branch",
                    status = "PRESENT",
                    checkInTime = "06:30 AM",
                    checkOutTime = "03:30 PM",
                    wageEarnedTzs = 0.0,
                    notes = "Conference hall carpet cleaning done"
                ),
                AttendanceEntity(
                    id = 6,
                    date = "2026-09-27",
                    employeeId = 6,
                    employeeName = "Neema Charles Shayo",
                    branchId = 3,
                    branchName = "Arusha Central Branch",
                    status = "SICK_LEAVE",
                    checkInTime = "--",
                    checkOutTime = "--",
                    wageEarnedTzs = 0.0,
                    notes = "Medical note submitted to supervisor"
                )
            )
            dao.insertAttendanceList(attendances)

            // 4. Inventory Items (Cleaning Tools & Consumables, low-stock triggers)
            val inventoryItems = listOf(
                InventoryItemEntity(
                    id = 1,
                    name = "Industrial Ride-On / Walk Floor Scrubber",
                    category = "EQUIPMENT",
                    unit = "Units",
                    headOfficeStock = 4,
                    branchStock = 6,
                    minStockThreshold = 3,
                    unitCostTzs = 4500000.0,
                    lastRestockedDate = "2026-01-15"
                ),
                InventoryItemEntity(
                    id = 2,
                    name = "Commercial Wet & Dry Vacuum 30L",
                    category = "EQUIPMENT",
                    unit = "Units",
                    headOfficeStock = 8,
                    branchStock = 14,
                    minStockThreshold = 5,
                    unitCostTzs = 750000.0,
                    lastRestockedDate = "2026-02-10"
                ),
                InventoryItemEntity(
                    id = 3,
                    name = "Double-Bucket Wringer Mop Trolley",
                    category = "EQUIPMENT",
                    unit = "Units",
                    headOfficeStock = 12,
                    branchStock = 25,
                    minStockThreshold = 10,
                    unitCostTzs = 180000.0,
                    lastRestockedDate = "2026-02-28"
                ),
                InventoryItemEntity(
                    id = 4,
                    name = "Telescopic Window Glass Squeegee (6m)",
                    category = "EQUIPMENT",
                    unit = "Units",
                    headOfficeStock = 5,
                    branchStock = 9,
                    minStockThreshold = 4,
                    unitCostTzs = 95000.0,
                    lastRestockedDate = "2026-01-20"
                ),
                InventoryItemEntity(
                    id = 5,
                    name = "Heavy-Duty Multi-Surface Floor Cleaner (20L)",
                    category = "CONSUMABLE",
                    unit = "Jerrycans",
                    headOfficeStock = 3, // LOW STOCK ALERT!
                    branchStock = 8,
                    minStockThreshold = 10,
                    unitCostTzs = 65000.0,
                    lastRestockedDate = "2026-02-05"
                ),
                InventoryItemEntity(
                    id = 6,
                    name = "Hospital Grade Surface Disinfectant (5L)",
                    category = "CONSUMABLE",
                    unit = "Bottles",
                    headOfficeStock = 4, // LOW STOCK ALERT!
                    branchStock = 11,
                    minStockThreshold = 8,
                    unitCostTzs = 38000.0,
                    lastRestockedDate = "2026-02-18"
                ),
                InventoryItemEntity(
                    id = 7,
                    name = "Industrial Glass & Window Cleaner (5L)",
                    category = "CONSUMABLE",
                    unit = "Bottles",
                    headOfficeStock = 15,
                    branchStock = 22,
                    minStockThreshold = 8,
                    unitCostTzs = 24000.0,
                    lastRestockedDate = "2026-03-01"
                ),
                InventoryItemEntity(
                    id = 8,
                    name = "Heavy-Duty Black Trash Bags (100L, Pack of 50)",
                    category = "CONSUMABLE",
                    unit = "Packs",
                    headOfficeStock = 2, // LOW STOCK ALERT!
                    branchStock = 6,
                    minStockThreshold = 15,
                    unitCostTzs = 22000.0,
                    lastRestockedDate = "2026-01-28"
                ),
                InventoryItemEntity(
                    id = 9,
                    name = "Color-Coded Microfiber Cleaning Cloths (Pack 12)",
                    category = "CONSUMABLE",
                    unit = "Packs",
                    headOfficeStock = 24,
                    branchStock = 40,
                    minStockThreshold = 12,
                    unitCostTzs = 18000.0,
                    lastRestockedDate = "2026-03-05"
                )
            )
            dao.insertInventoryItems(inventoryItems)

            // 5. Equipment Requests from Branches
            val requests = listOf(
                EquipmentRequestEntity(
                    id = 1,
                    requestCode = "REQ-2026-0042",
                    branchId = 3,
                    branchName = "Arusha Central Branch",
                    requestedBy = "Emanuel Lucas Mollel",
                    itemId = 5,
                    itemName = "Heavy-Duty Multi-Surface Floor Cleaner (20L)",
                    category = "CONSUMABLE",
                    quantityRequested = 4,
                    urgency = "URGENT",
                    requestDate = "2026-09-25",
                    status = "PENDING",
                    remarks = "Needed for upcoming safari lodge conference deep cleaning contract"
                ),
                EquipmentRequestEntity(
                    id = 2,
                    requestCode = "REQ-2026-0038",
                    branchId = 2,
                    branchName = "Dar es Salaam - Kinondoni",
                    requestedBy = "Amina Saidi Salum",
                    itemId = 2,
                    itemName = "Commercial Wet & Dry Vacuum 30L",
                    category = "EQUIPMENT",
                    quantityRequested = 2,
                    urgency = "NORMAL",
                    requestDate = "2026-09-22",
                    status = "APPROVED",
                    remarks = "Approved by Store Keeper. Scheduled for dispatch via delivery van",
                    dispatchedDate = ""
                ),
                EquipmentRequestEntity(
                    id = 3,
                    requestCode = "REQ-2026-0035",
                    branchId = 6,
                    branchName = "Zanzibar Urban Branch",
                    requestedBy = "Fatma Khamis Nassor",
                    itemId = 6,
                    itemName = "Hospital Grade Surface Disinfectant (5L)",
                    category = "CONSUMABLE",
                    quantityRequested = 6,
                    urgency = "CRITICAL",
                    requestDate = "2026-09-18",
                    status = "DISPATCHED",
                    remarks = "Dispatched via Azam Marine cargo ferry to Malindi Port",
                    dispatchedDate = "2026-09-20"
                )
            )
            dao.insertEquipmentRequests(requests)

            // 6. Clients in Tanzania
            val clients = listOf(
                ClientEntity(
                    id = 1,
                    name = "CRDB Bank HQ Tower",
                    industry = "Banking & Financial Services",
                    contactPerson = "Eng. Moses Mwakipesile",
                    phone = "+255 754 889 001",
                    email = "facilities@crdbbank.co.tz",
                    address = "Azikiwe Street, Dar es Salaam CBD",
                    branchName = "Dar es Salaam HQ & Ilala",
                    activeContractsCount = 1
                ),
                ClientEntity(
                    id = 2,
                    name = "Dar es Salaam Serena Hotel",
                    industry = "Hospitality & Tourism",
                    contactPerson = "Ms. Lilian Ndibalema",
                    phone = "+255 767 990 112",
                    email = "housekeeping.manager@serenahotels.com",
                    address = "Ohio Street, Dar es Salaam",
                    branchName = "Dar es Salaam HQ & Ilala",
                    activeContractsCount = 1
                ),
                ClientEntity(
                    id = 3,
                    name = "NMB House Commercial Wing",
                    industry = "Banking & Real Estate",
                    contactPerson = "Mr. Godfrey Temba",
                    phone = "+255 784 102 938",
                    email = "procurement@nmbtz.com",
                    address = "Ali Hassan Mwinyi Road, Kinondoni",
                    branchName = "Dar es Salaam - Kinondoni",
                    activeContractsCount = 1
                ),
                ClientEntity(
                    id = 4,
                    name = "Kilimanjaro International Conference Centre",
                    industry = "Government & Events",
                    contactPerson = "Mr. Aloyce Shirima",
                    phone = "+255 755 332 211",
                    email = "operations@aicc.co.tz",
                    address = "Afrika Mashariki Rd, Arusha",
                    branchName = "Arusha Central Branch",
                    activeContractsCount = 1
                )
            )
            dao.insertClients(clients)

            // 7. Active Contracts
            val contracts = listOf(
                ContractEntity(
                    id = 1,
                    contractCode = "MNG-CNT-2026-01",
                    clientId = 1,
                    clientName = "CRDB Bank HQ Tower",
                    branchName = "Dar es Salaam HQ & Ilala",
                    serviceType = "Daily Full Janitorial & High-Rise Glass Facade Cleaning",
                    monthlyValueTzs = 6800000.0,
                    startDate = "2026-01-01",
                    endDate = "2026-12-31",
                    billingCycle = "Monthly",
                    status = "ACTIVE"
                ),
                ContractEntity(
                    id = 2,
                    contractCode = "MNG-CNT-2026-02",
                    clientId = 2,
                    clientName = "Dar es Salaam Serena Hotel",
                    branchName = "Dar es Salaam HQ & Ilala",
                    serviceType = "Kitchen Exhaust, Carpet Shampoo & Banquet Hall Sanitization",
                    monthlyValueTzs = 4500000.0,
                    startDate = "2026-01-01",
                    endDate = "2026-12-31",
                    billingCycle = "Monthly",
                    status = "ACTIVE"
                ),
                ContractEntity(
                    id = 3,
                    contractCode = "MNG-CNT-2026-03",
                    clientId = 3,
                    clientName = "NMB House Commercial Wing",
                    branchName = "Dar es Salaam - Kinondoni",
                    serviceType = "Daily Office Cleaning, Washroom Care & Waste Management",
                    monthlyValueTzs = 3800000.0,
                    startDate = "2026-02-01",
                    endDate = "2027-01-31",
                    billingCycle = "Monthly",
                    status = "ACTIVE"
                ),
                ContractEntity(
                    id = 4,
                    contractCode = "MNG-CNT-2026-04",
                    clientId = 4,
                    clientName = "Kilimanjaro International Conference Centre",
                    branchName = "Arusha Central Branch",
                    serviceType = "Pre & Post Event Deep Cleaning, Floor Buffing",
                    monthlyValueTzs = 2900000.0,
                    startDate = "2026-03-01",
                    endDate = "2026-08-31",
                    billingCycle = "Monthly",
                    status = "ACTIVE"
                )
            )
            dao.insertContracts(contracts)

            // 8. Invoices with 18% Tanzanian VAT
            val invoices = listOf(
                InvoiceEntity(
                    id = 1,
                    invoiceNumber = "INV-2026-0901",
                    clientId = 1,
                    clientName = "CRDB Bank HQ Tower",
                    contractId = 1,
                    issueDate = "2026-09-01",
                    dueDate = "2026-09-25",
                    subtotalTzs = 6800000.0,
                    vatRate = 0.18,
                    vatAmountTzs = 1224000.0, // 18% of 6,800,000
                    totalAmountTzs = 8024000.0,
                    paidAmountTzs = 8024000.0,
                    status = "PAID",
                    notes = "Monthly Cleaning Service for September 2026. Paid via Bank Wire"
                ),
                InvoiceEntity(
                    id = 2,
                    invoiceNumber = "INV-2026-0902",
                    clientId = 2,
                    clientName = "Dar es Salaam Serena Hotel",
                    contractId = 2,
                    issueDate = "2026-09-01",
                    dueDate = "2026-09-20",
                    subtotalTzs = 4500000.0,
                    vatRate = 0.18,
                    vatAmountTzs = 810000.0, // 18% of 4,500,000
                    totalAmountTzs = 5310000.0,
                    paidAmountTzs = 3000000.0,
                    status = "PARTIALLY_PAID",
                    notes = "Balance outstanding TSh 2,310,000 due before end of month"
                ),
                InvoiceEntity(
                    id = 3,
                    invoiceNumber = "INV-2026-0903",
                    clientId = 3,
                    clientName = "NMB House Commercial Wing",
                    contractId = 3,
                    issueDate = "2026-09-05",
                    dueDate = "2026-09-30",
                    subtotalTzs = 3800000.0,
                    vatRate = 0.18,
                    vatAmountTzs = 684000.0,
                    totalAmountTzs = 4484000.0,
                    paidAmountTzs = 0.0,
                    status = "UNPAID",
                    notes = "Pending accountant clearance from NMB finance department"
                ),
                InvoiceEntity(
                    id = 4,
                    invoiceNumber = "INV-2026-0810",
                    clientId = 4,
                    clientName = "Kilimanjaro International Conference Centre",
                    contractId = 4,
                    issueDate = "2026-08-10",
                    dueDate = "2026-09-10",
                    subtotalTzs = 2900000.0,
                    vatRate = 0.18,
                    vatAmountTzs = 522000.0,
                    totalAmountTzs = 3422000.0,
                    paidAmountTzs = 0.0,
                    status = "OVERDUE",
                    notes = "Overdue reminder letter dispatched to AICC Arusha Directorate"
                )
            )
            dao.insertInvoices(invoices)

            // 9. Payment Receipts
            val payments = listOf(
                PaymentEntity(
                    id = 1,
                    receiptNumber = "RCT-2026-0412",
                    invoiceId = 1,
                    invoiceNumber = "INV-2026-0901",
                    clientName = "CRDB Bank HQ Tower",
                    paymentDate = "2026-09-14",
                    amountTzs = 8024000.0,
                    paymentMethod = "CRDB Bank Transfer",
                    referenceNumber = "FT26257008129"
                ),
                PaymentEntity(
                    id = 2,
                    receiptNumber = "RCT-2026-0419",
                    invoiceId = 2,
                    invoiceNumber = "INV-2026-0902",
                    clientName = "Dar es Salaam Serena Hotel",
                    paymentDate = "2026-09-18",
                    amountTzs = 3000000.0,
                    paymentMethod = "Bank Cheque",
                    referenceNumber = "CHQ-004921"
                )
            )
            dao.insertPayments(payments)

            // 10. Sample Payroll Records
            val payrollRecords = listOf(
                PayrollRecordEntity(
                    id = 1,
                    payrollPeriod = "September 2026",
                    employeeId = 1,
                    employeeName = "Josephat Julius Kazi",
                    employeeCode = "MCS-001",
                    classification = "CONTRACT",
                    branchName = "Dar es Salaam HQ & Ilala",
                    daysAttended = 26,
                    basePayTzs = 650000.0,
                    allowanceTzs = 50000.0, // Transport allowance
                    deductionsTzs = 65000.0, // NSSF 10%
                    netPayTzs = 635000.0,
                    paymentStatus = "APPROVED",
                    processedDate = "2026-09-25"
                ),
                PayrollRecordEntity(
                    id = 2,
                    payrollPeriod = "September 2026",
                    employeeId = 2,
                    employeeName = "Asha Ramadhani Kibena",
                    employeeCode = "MCS-002",
                    classification = "CONTRACT",
                    branchName = "Dar es Salaam HQ & Ilala",
                    daysAttended = 25,
                    basePayTzs = 480000.0,
                    allowanceTzs = 30000.0,
                    deductionsTzs = 48000.0, // NSSF 10%
                    netPayTzs = 462000.0,
                    paymentStatus = "APPROVED",
                    processedDate = "2026-09-25"
                ),
                PayrollRecordEntity(
                    id = 3,
                    payrollPeriod = "September 2026",
                    employeeId = 3,
                    employeeName = "Michael Deo Mrema",
                    employeeCode = "MCS-003",
                    classification = "NON_CONTRACT",
                    branchName = "Dar es Salaam HQ & Ilala",
                    daysAttended = 22,
                    basePayTzs = 396000.0, // 22 days * 18,000 TZS
                    allowanceTzs = 0.0,
                    deductionsTzs = 0.0,
                    netPayTzs = 396000.0,
                    paymentStatus = "PAID",
                    processedDate = "2026-09-26"
                ),
                PayrollRecordEntity(
                    id = 4,
                    payrollPeriod = "September 2026",
                    employeeId = 4,
                    employeeName = "Zulfa Rashid Mwenda",
                    employeeCode = "MCS-004",
                    classification = "NON_CONTRACT",
                    branchName = "Dar es Salaam - Kinondoni",
                    daysAttended = 19,
                    basePayTzs = 285000.0, // 19 days * 15,000 TZS
                    allowanceTzs = 0.0,
                    deductionsTzs = 0.0,
                    netPayTzs = 285000.0,
                    paymentStatus = "PENDING",
                    processedDate = "2026-09-26"
                )
            )
            dao.insertPayrollRecords(payrollRecords)
        }
    }
}
