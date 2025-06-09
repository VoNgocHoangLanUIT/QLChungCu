/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Process.User;
import ConnectDB.ConnectionUtils;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author DELL
 */
public class UserJava { 
    
    public int themUser(String username, String password, String email) throws SQLException, ClassNotFoundException {
        int i = 0;
        // TODO add your handling code here:
        try (Connection con = ConnectionUtils.getMyConnection()) {
        
//            String query = "INSERT INTO "
//                    + "DOIBONG(MAD,TENDOI,QUOCGIA)"
//                    +" VALUES('"
//                    +maDoi+"','"+tenDoi+"','"+quocGia+"')";

            String query = "INSERT INTO ACCOUNT(USERNAME,PASSWORD_HASH, EMAIL) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            i = ps.executeUpdate();
//            Statement stat = con.createStatement();
//            i = stat.executeUpdate(query);
            
        }
        catch(SQLException e){
            if(e.getErrorCode() == 1) {
               // Lỗi do người dùng đăng ký đã trùng
               i = -2000; 
            }
            System.out.println("Error Code: " + e.getErrorCode()); // Error Code
            
        }
        
        return i;
}
    public UserResponse DangNhapNguoiDung(String username, String passwordHash) throws SQLException, ClassNotFoundException{
        int i = 0;
        UserResponse userResponse = new UserResponse();
        
        // TODO add your handling code here:
        try (Connection con = ConnectionUtils.getMyConnection()) {
        
//            String query = "INSERT INTO "
//                    + "DOIBONG(MAD,TENDOI,QUOCGIA)"
//                    +" VALUES('"
//                    +maDoi+"','"+tenDoi+"','"+quocGia+"')";

            String query = "Select account_id from ACCOUNT where USERNAME = ? AND PASSWORD_HASH = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            
            
            ResultSet resultSet = ps.executeQuery();
            
                if(resultSet.next()){
                    int accountID = resultSet.getInt("account_id");
                    userResponse.setStatus(true);
                    userResponse.setAccountID(accountID);
                    System.out.println("Status: " + userResponse.isStatus());
                    System.out.println("UserID: " + userResponse.getAccountID());
                    userResponse.setRole(this.getRoleName(accountID));
                    return userResponse;
                    
                } else{
                    userResponse.setStatus(false);
                    userResponse.setAccountID(0);
                    return userResponse;
                }
//            Statement stat = con.createStatement();
//            i = stat.executeUpdate(query);
            
        }
        catch(SQLException e){
            System.out.println("Error Code: " + e.getErrorCode()); // Error Code
            System.out.println("Error Code: " + e.getMessage());

        }
        
        userResponse.setStatus(false);
        userResponse.setAccountID(0);
        return userResponse;
    }
    
    public String getRoleName(int accountID) throws SQLException, ClassNotFoundException{
        String role = "hello";
        try (Connection con = ConnectionUtils.getMyConnection()) {
            System.out.println("→ accountID truyền vào: " + accountID);

            String query = "Select NAME_ROLE_GROUP "
                            + " from ACCOUNT_ASSIGN_ROLE_GROUP AR"
                            + " JOIN ROLE_GROUP R ON AR.ROLE_GROUP_ID = R.ROLE_GROUP_ID"
                            + " where AR.ACCOUNT_ID = ? ";

            PreparedStatement ps1 = con.prepareStatement(query);
            ps1.setInt(1, accountID);

            ResultSet resultSet1 = ps1.executeQuery();

            if (resultSet1.next()) {
                role = resultSet1.getString("NAME_ROLE_GROUP");
                System.out.println("→ Role tìm được: " + role);
            } else {
                System.out.println("→ Không tìm thấy role cho accountID: " + accountID);
            }
        }       
        catch(SQLException e){
            System.out.println("→ SQLException: " + e.getMessage());
        }
        return role;
    }

}
