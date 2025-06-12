package Service;

import DAO.AccountDAO;
import Model.Account;
import Model.DTO.LoginResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    private AccountDAO accountDAO;

    public AuthService() {
        this.accountDAO = new AccountDAO();
    }

    /**
     * Handles the login logic.
     * @param username The username from the login form.
     * @param password The raw password from the login form.
     * @return A LoginResponse object if successful, otherwise null.
     */
    public LoginResponse login(String username, String password) {
        Account account = accountDAO.findByUsername(username);

        if (account != null && account.getPasswordHash().equals(hashPassword(password))) {
            return new LoginResponse(account.getRole(), account.getResidentId());
        }
        
        return null;
    }

    /**
     * Handles the registration logic for a new user.
     * This version only creates an account without assigning a role.
     * @param username The username for the new account.
     * @param password The raw password.
     * @param email The email for the new account.
     * @return An integer status: 1 for success, -1 for already exists/error.
     */
    public int register(String username, String password, String email) {
        String hashedPassword = hashPassword(password);
        
        // Just create the account. The role will be assigned later by an admin.
        int newAccountId = accountDAO.createAccount(username, hashedPassword, email);
        
        // Return 1 for success, -1 for duplicate/error (as returned by DAO)
        return (newAccountId > 0) ? 1 : -1;
    }

    /**
     * Hashes a password using the SHA-256 algorithm.
     * @param password The raw password.
     * @return The hashed password string.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
