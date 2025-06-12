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
import Model.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    /**
     * Gets all staff members from the database.
     * @return A list of Staff objects.
     */
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        // Note: Assumes your table is named 'NhanVien' or 'Staff'. 
        // Please change the table and column names to match your database schema.
        String sql = "SELECT ma_nhan_vien, ho_ten, ngay_sinh, so_dien_thoai, gioi_tinh, dia_chi, phong_ban, luong FROM NhanVien";

        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Staff staff = new Staff();
                staff.setStaffId(rs.getString("ma_nhan_vien"));
                staff.setFullName(rs.getString("ho_ten"));
                staff.setDateOfBirth(rs.getDate("ngay_sinh"));
                staff.setPhoneNumber(rs.getString("so_dien_thoai"));
                staff.setGender(rs.getString("gioi_tinh"));
                staff.setAddress(rs.getString("dia_chi"));
                staff.setDepartment(rs.getString("phong_ban"));
                staff.setSalary(rs.getDouble("luong"));
                staffList.add(staff);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return staffList;
    }
}
