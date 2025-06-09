/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Process.User;

/**
 *
 * @author DELL
 */
public class UserResponse{
        private boolean status;
        private int accountID;
        private String role;
        
        public UserResponse() {
        }

        public UserResponse(boolean status, int accountID, String role) {
            this.status = status;
            this.accountID = accountID;
            this.role = role;
        }

        
        
        public boolean isStatus() {
            return status;
        }

        public int getAccountID() {
            return accountID;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public void setAccountID(int accountID) {
            this.accountID = accountID;
        }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
        
        
    }
