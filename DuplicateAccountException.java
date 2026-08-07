package crc.assignmentbase;

// File location: src/main/java/DuplicateAccountException.java
// Supports the login gate in Driver.java

/**
 * Thrown by AccountManager.registerAccount() when the username is
 * already taken.
 */
public class DuplicateAccountException extends Exception {
    public DuplicateAccountException(String message) {
        super(message);
    }
}
