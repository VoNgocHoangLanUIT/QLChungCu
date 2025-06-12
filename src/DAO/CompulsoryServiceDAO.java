package DAO;

import ConnectDB.ConnectionUtils;
import Model.CompulsoryService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompulsoryServiceDAO {

    /**
     * Lấy tất cả dịch vụ bắt buộc từ cơ sở dữ liệu.
     * @return Danh sách các dịch vụ.
     */
    public List<CompulsoryService> getAllServices() {
        List<CompulsoryService> serviceList = new ArrayList<>();
        String sql = "SELECT ma_dv_bat_buoc, ten_dv_bat_buoc, ma_nha_cung_cap, don_vi, gia FROM DichVuBatBuoc";
        try (Connection conn = ConnectionUtils.getMyConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CompulsoryService service = new CompulsoryService();
                service.setServiceId(rs.getString("ma_dv_bat_buoc"));
                service.setServiceName(rs.getString("ten_dv_bat_buoc"));
                service.setProviderId(rs.getString("ma_nha_cung_cap"));
                service.setUnit(rs.getString("don_vi"));
                service.setPrice(rs.getBigDecimal("gia"));
                serviceList.add(service);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return serviceList;
    }

    /**
     * Thêm một dịch vụ bắt buộc mới.
     * @param service Đối tượng dịch vụ cần thêm.
     * @return true nếu thêm thành công.
     */
    public boolean addService(CompulsoryService service) {
        String sql = "INSERT INTO DichVuBatBuoc (ma_dv_bat_buoc, ten_dv_bat_buoc, ma_nha_cung_cap, don_vi, gia) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, service.getServiceId());
            ps.setString(2, service.getServiceName());
            ps.setString(3, service.getProviderId());
            ps.setString(4, service.getUnit());
            ps.setBigDecimal(5, service.getPrice());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin một dịch vụ bắt buộc.
     * @param service Đối tượng dịch vụ cần cập nhật.
     * @return true nếu cập nhật thành công.
     */
    public boolean updateService(CompulsoryService service) {
        String sql = "UPDATE DichVuBatBuoc SET ten_dv_bat_buoc = ?, ma_nha_cung_cap = ?, don_vi = ?, gia = ? WHERE ma_dv_bat_buoc = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, service.getServiceName());
            ps.setString(2, service.getProviderId());
            ps.setString(3, service.getUnit());
            ps.setBigDecimal(4, service.getPrice());
            ps.setString(5, service.getServiceId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một dịch vụ bắt buộc.
     * @param serviceId Mã của dịch vụ cần xóa.
     * @return true nếu xóa thành công.
     */
    public boolean deleteService(String serviceId) {
        String sql = "DELETE FROM DichVuBatBuoc WHERE ma_dv_bat_buoc = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, serviceId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
