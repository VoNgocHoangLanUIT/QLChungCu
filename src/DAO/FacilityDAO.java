/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import ConnectDB.ConnectionUtils;
import Model.Facility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class FacilityDAO {

    public List<Facility> getAllFacilities() {
        List<Facility> facilities = new ArrayList<>();
        // Sử dụng tên bảng và cột từ file BookFacilities.java của bạn
        String sql = "SELECT ma_dv_ngoai, ten_dv_ngoai, ma_nha_cung_cap, don_vi, so_luong, gia FROM DichVuNgoai"; 
        
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Đọc số lượng dưới dạng int để kiểm tra NULL
                int stock = rs.getInt("so_luong");
                String stockQuantityStr;
                if (rs.wasNull()) {
                    // Nếu trong CSDL là NULL, chúng ta có thể biểu diễn nó là "unlimited" hoặc một giá trị mặc định
                    stockQuantityStr = "unlimited"; 
                } else {
                    stockQuantityStr = String.valueOf(stock);
                }

                Facility facility = new Facility(
                    rs.getString("ma_dv_ngoai"),
                    rs.getString("ten_dv_ngoai"),
                    rs.getString("ma_nha_cung_cap"), // Giả sử đây là Manufacturer
                    rs.getString("don_vi"),
                    stockQuantityStr, // Dùng chuỗi đã được xử lý
                    rs.getDouble("gia")
                );
                facilities.add(facility);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return facilities;
    }

    public void updateStockQuantity(String serviceId, int newQuantity) {
        String sql = "UPDATE DichVuNgoai SET so_luong = ? WHERE ma_dv_ngoai = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Dùng setInt cho cột có kiểu dữ liệu là số
            pstmt.setInt(1, newQuantity); 
            pstmt.setString(2, serviceId);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
