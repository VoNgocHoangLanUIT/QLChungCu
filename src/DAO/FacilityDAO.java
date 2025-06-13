package DAO;

import ConnectDB.ConnectionUtils;
import Model.Facility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacilityDAO {

    public List<Facility> getAllFacilities() {
        List<Facility> facilities = new ArrayList<>();
        // Sửa đổi câu lệnh SQL để chỉ lấy các dịch vụ chưa bị xóa (da_xoa = 0)
        String sql = "SELECT ma_dv_ngoai, ten_dv_ngoai, ma_nha_cung_cap, don_vi, so_luong, gia, mo_ta, da_xoa FROM DichVuNgoai WHERE da_xoa = 0"; 
        
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int stock = rs.getInt("so_luong");
                String stockQuantityStr = rs.wasNull() ? "unlimited" : String.valueOf(stock);
                
                Facility facility = new Facility(
                    rs.getString("ma_dv_ngoai"),
                    rs.getString("ten_dv_ngoai"),
                    rs.getString("ma_nha_cung_cap"),
                    rs.getString("don_vi"),
                    stockQuantityStr,
                    rs.getDouble("gia"),
                    rs.getString("mo_ta") 
                );
                facility.setDeleted(rs.getInt("da_xoa") == 1);
                facilities.add(facility);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return facilities;
    }
    
    public boolean addFacility(Facility facility) {
        // Cập nhật câu lệnh INSERT để bao gồm cột da_xoa (mặc định là 0)
        String sql = "INSERT INTO DichVuNgoai (ma_dv_ngoai, ten_dv_ngoai, ma_nha_cung_cap, don_vi, so_luong, gia, mo_ta, da_xoa) VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, facility.getServiceId());
            ps.setString(2, facility.getServiceName());
            ps.setString(3, facility.getManufacturer());
            ps.setString(4, facility.getUnit());
            
            if ("unlimited".equalsIgnoreCase(facility.getStockQuantity())) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, Integer.parseInt(facility.getStockQuantity()));
            }
            
            ps.setDouble(6, facility.getPrice());
            ps.setString(7, facility.getDescription());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFacility(Facility facility) {
        String sql = "UPDATE DichVuNgoai SET ten_dv_ngoai = ?, ma_nha_cung_cap = ?, don_vi = ?, so_luong = ?, gia = ?, mo_ta = ? WHERE ma_dv_ngoai = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, facility.getServiceName());
            ps.setString(2, facility.getManufacturer());
            ps.setString(3, facility.getUnit());

            if ("unlimited".equalsIgnoreCase(facility.getStockQuantity())) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, Integer.parseInt(facility.getStockQuantity()));
            }

            ps.setDouble(5, facility.getPrice());
            ps.setString(6, facility.getDescription());
            ps.setString(7, facility.getServiceId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFacility(String facilityId) {
        // Thay đổi từ DELETE sang UPDATE để thực hiện soft delete
        String sql = "UPDATE DichVuNgoai SET da_xoa = 1 WHERE ma_dv_ngoai = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, facilityId);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateStockQuantity(String serviceId, int newQuantity) {
        String sql = "UPDATE DichVuNgoai SET so_luong = ? WHERE ma_dv_ngoai = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newQuantity); 
            pstmt.setString(2, serviceId);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
