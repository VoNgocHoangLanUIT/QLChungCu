package Model.DTO;

public class AuthorizationInfo {
    private String username;
    private String email;
    private String role;
    private String residentId;

    // Constructors
    public AuthorizationInfo() {
    }

    public AuthorizationInfo(String username, String email, String role, String residentId) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.residentId = residentId;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getResidentId() {
        return residentId;
    }

    public void setResidentId(String residentId) {
        this.residentId = residentId;
    }
}
