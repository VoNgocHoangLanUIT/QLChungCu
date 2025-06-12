package Model.DTO;

public class LoginResponse {
    private String role;
    private String residentId;

    public LoginResponse(String role, String residentId) {
        this.role = role;
        this.residentId = residentId;
    }

    // Getters
    public String getRole() {
        return role;
    }

    public String getResidentId() {
        return residentId;
    }
}
