# [Q&A] Tag Index

Every tag below is a `// [Q&A #n]` comment inserted directly above the relevant
method/field in the source files. Grep for `[Q&A` in any editor to jump between
them, or use this table as a lookup.

| # | Interview Question | File | Method / Field |
|---|---|---|---|
| 1 | How do you achieve non-duplicating IDs if one was previously deleted? | `ApplianceManager.java`, `CustomerManager.java`, `StaffManager.java` | `generateNextApplianceID()`, `generateNextCustomerID()`, `generateNextStaffID()` |
| 2 | Why is `Appliance` abstract instead of concrete with a `type` field? | `Appliance.java` | `calculateFinalPrice()`, `getDefaultProvider()`, `getDefaultDuration()` |
| 3 | How do you guarantee every sale is attributable to a staff member? | `Transaction.java` | constructor (`soldBy` null-check) |
| 4 | Why validate in setters when the menu already validates? | `Customer.java` | `setName()` (representative — same pattern in `Staff.java`/`Appliance.java`) |
| 5 | How do you prevent orphaned transactions if a staff member is deleted? | `StaffManager.java` | `removeStaff()` |
| 6 | Why split `Account` and `Staff` into two classes? | `Account.java` | class-level header comment |
| 7 | Why a String ID link instead of holding the `Staff` object directly? | `Account.java` | `staffID` field |
| 8 | How is a customer's discount rate calculated? | `MembershipStatus.java` | `getDiscountRate()` |
| 9 | Why custom checked exceptions instead of `RuntimeException`? | `DuplicateAccountException.java`, `DuplicateApplianceException.java`, `DuplicateCustomerException.java`, `DuplicateStaffException.java`, `InvalidWarrantyExtensionException.java` | class declarations |
| 10 | How is total repair cost decided? | `Warranty.java` | `getTotalRepairCost()` |
| 11 | Why constructor-inject `ApplianceManager` but pass `CustomerManager`/`StaffManager` as params? | `WarrantyManager.java` | class field + constructor |
| 12 | How did you avoid duplicating input validation? | `InputValidator.java` | class-level header comment |
| 13 | Is that validation consistent everywhere? *(honest gap)* | `Driver.java`, `StaffManager.java` | `readAlphabetOnly()`/`readValidEmail()` (Driver), `readStaffRole()`/`readValidEmail()` (StaffManager) |
| 14 | Why is `Staff.role` a String, not an enum like the others? *(honest gap)* | `Staff.java` | `ROLE_MANAGER`/`ROLE_STAFF` constants + `setRole()` |
| 15 | How is the sales report broken down by staff/appliance built? | `StaffManager.java` | `printSalesReport()` |
| 16 | How do you find the top-performing staff member? | `StaffManager.java` | `getTopPerformer()` |
| 17 | Why a plain array instead of a `Map` for membership breakdown? | `CustomerManager.java` | `getMembershipBreakdown()` |
| 18 | What stops two staff members from sharing an ID? | `StaffManager.java` | `registerStaff(Staff staff)` |

**Note on #13 and #14:** these are the two spots flagged as genuine, honest
inconsistencies in the codebase rather than deliberate design choices — worth
naming yourself in an interview rather than waiting to be asked.
