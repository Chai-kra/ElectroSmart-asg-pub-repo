package crc.assignmentbase;

// File location: src/main/java/AccountRole.java
// Supports the login gate in Driver.java

/**
 * The two kinds of login account. ADMIN can also manage (view, edit,
 * clear) all stored data; STAFF can only use the day-to-day store
 * functions (register customer, process sale, etc).
 */
public enum AccountRole {
    ADMIN,
    STAFF
}
