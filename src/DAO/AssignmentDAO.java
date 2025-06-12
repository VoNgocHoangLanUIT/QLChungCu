/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

/**
 *
 * @author DELL
 */
import ConnectDB.ConnectionUtils;
import java.sql.*;

public class AssignmentDAO {

    /**
     * Adds a new assignment record to the database.
     * @param complaintId The ID of the assigned complaint.
     * @param staffId The ID of the staff member receiving the task.
     * @return true if the insertion is successful, false otherwise.
     */
    public boolean addAssignment(String complaintId, String staffId) {
        // Note: Assumes your table is named 'GiaoViec' or 'Assignment'.
        // Please change the table and column names to match your database schema.
        String sql = "INSERT INTO GiaoViec (ma_phan_anh, ma_nhan_vien) VALUES (?, ?)";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, complaintId);
            ps.setString(2, staffId);

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
