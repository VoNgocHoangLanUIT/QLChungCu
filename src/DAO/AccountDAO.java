package DAO;

import ConnectDB.ConnectionUtils;
import Model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Account findByUsername(String username) {
        String sql = "SELECT a.ACCOUNT_ID, a.USERNAME, a.PASSWORD_HASH, a.EMAIL, a.ma_cu_dan, rg.NAME_ROLE_GROUP " +
                     "FROM ACCOUNT a " +
                     "LEFT JOIN ACCOUNT_ASSIGN_ROLE_GROUP aarg ON a.ACCOUNT_ID = aarg.ACCOUNT_ID " +
                     "LEFT JOIN ROLE_GROUP rg ON aarg.ROLE_GROUP_ID = rg.ROLE_GROUP_ID " +
                     "WHERE a.USERNAME = ?";
                     
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setAccountId(rs.getInt("ACCOUNT_ID"));
                    account.setUsername(rs.getString("USERNAME"));
                    account.setPasswordHash(rs.getString("PASSWORD_HASH"));
                    account.setEmail(rs.getString("EMAIL"));
                    account.setResidentId(rs.getString("ma_cu_dan"));
                    account.setRole(rs.getString("NAME_ROLE_GROUP"));
                    return account;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createAccount(String username, String hashedPassword, String email) {
        String sql = "INSERT INTO ACCOUNT (USERNAME, PASSWORD_HASH, EMAIL) VALUES (?, ?, ?) RETURNING ACCOUNT_ID INTO ?";
        String plsql = "BEGIN " + sql + "; END;";
        try (Connection conn = ConnectionUtils.getMyConnection();
             CallableStatement cs = conn.prepareCall(plsql)) {
            
            cs.setString(1, username);
            cs.setString(2, hashedPassword);
            cs.setString(3, email);
            cs.registerOutParameter(4, Types.INTEGER);
            
            cs.execute();
            return cs.getInt(4);

        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { 
                System.err.println("Username or Email already exists.");
            } else {
                e.printStackTrace();
            }
            return -1; // Thêm return ở đây để xử lý lỗi
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return -1; // Thêm return ở đây để xử lý lỗi
        }
    }
    
    /**
     * Lấy tất cả các tài khoản cùng với vai trò và mã cư dân.
     * @return Danh sách các đối tượng Account.
     */
    public List<Account> getAllAccountsForAuthorization() {
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT a.ACCOUNT_ID, a.USERNAME, a.EMAIL, a.ma_cu_dan, rg.NAME_ROLE_GROUP " +
                     "FROM ACCOUNT a " +
                     "LEFT JOIN ACCOUNT_ASSIGN_ROLE_GROUP aarg ON a.ACCOUNT_ID = aarg.ACCOUNT_ID " +
                     "LEFT JOIN ROLE_GROUP rg ON aarg.ROLE_GROUP_ID = rg.ROLE_GROUP_ID " +
                     "ORDER BY a.ACCOUNT_ID";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("ACCOUNT_ID"));
                account.setUsername(rs.getString("USERNAME"));
                account.setEmail(rs.getString("EMAIL"));
                account.setResidentId(rs.getString("ma_cu_dan"));
                account.setRole(rs.getString("NAME_ROLE_GROUP"));
                accountList.add(account);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return accountList;
    }

    /**
     * Cập nhật mã cư dân cho một tài khoản.
     * @param accountId ID của tài khoản.
     * @param residentId Mã cư dân mới (có thể là null).
     * @return true nếu thành công.
     */
    public boolean updateResidentIdForAccount(int accountId, String residentId) {
        String sql = "UPDATE ACCOUNT SET ma_cu_dan = ? WHERE ACCOUNT_ID = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (residentId == null || residentId.trim().isEmpty() || residentId.equalsIgnoreCase("N/A")) {
                ps.setNull(1, Types.NVARCHAR);
            } else {
                ps.setString(1, residentId);
            }
            ps.setInt(2, accountId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật hoặc thêm mới vai trò cho tài khoản.
     * @param accountId ID của tài khoản.
     * @param roleId ID của vai trò.
     * @return true nếu thành công.
     */
    public boolean upsertAccountRole(int accountId, int roleId) {
        String updateSql = "UPDATE ACCOUNT_ASSIGN_ROLE_GROUP SET ROLE_GROUP_ID = ? WHERE ACCOUNT_ID = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
            
            psUpdate.setInt(1, roleId);
            psUpdate.setInt(2, accountId);
            int rowsAffected = psUpdate.executeUpdate();
            
            if (rowsAffected > 0) {
                return true; // Cập nhật thành công
            } else {
                // Nếu không có dòng nào được cập nhật (chưa có bản ghi), thì thêm mới
                String insertSql = "INSERT INTO ACCOUNT_ASSIGN_ROLE_GROUP (ACCOUNT_ID, ROLE_GROUP_ID) VALUES (?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, accountId);
                    psInsert.setInt(2, roleId);
                    return psInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Tìm ID của vai trò bằng tên.
     * @param roleName Tên vai trò (vd: "cudan").
     * @return ID của vai trò, hoặc -1 nếu không tìm thấy.
     */
    public int findRoleIdByName(String roleName) {
        String sql = "SELECT ROLE_GROUP_ID FROM ROLE_GROUP WHERE NAME_ROLE_GROUP = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ROLE_GROUP_ID");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
