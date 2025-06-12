package Model;

public class Account {
    private int accountId;
    private String username;
    private String passwordHash;
    private String email;
    private String residentId; // ma_cu_dan
    private String role; // NAME_ROLE_GROUP

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
