/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import ConnectDB.ConnectionUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import Model.SubscribedFacility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DELL
 */
public class SubscribedFacilityDAO {

    public List<SubscribedFacility> getByInvoiceId(int invoiceId) {
        List<SubscribedFacility> list = new ArrayList<>();
        String sql = "SELECT c.ma_hoa_don, c.ma_dv_ngoai, d.ten_dv_ngoai, d.don_vi, c.SoLuong, c.DonGiaTaiThoiDiemDat, c.ThanhTien " +
                     "FROM CHITIET_DICHVU_HD c " +
                     "JOIN DichVuNgoai d ON c.ma_dv_ngoai = d.ma_dv_ngoai " +
                     "WHERE c.ma_hoa_don = ?";
                     
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                SubscribedFacility sf = new SubscribedFacility();
                sf.setInvoiceId(rs.getInt("ma_hoa_don"));
                sf.setServiceId(rs.getString("ma_dv_ngoai"));
                sf.setServiceName(rs.getString("ten_dv_ngoai"));
                sf.setUnit(rs.getString("don_vi"));
                sf.setQuantity(rs.getInt("SoLuong"));
                sf.setUnitPrice(rs.getDouble("DonGiaTaiThoiDiemDat"));
                sf.setLineTotal(rs.getDouble("ThanhTien"));
                list.add(sf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(SubscribedFacility sf) {
        String sql = "INSERT INTO CHITIET_DICHVU_HD (ma_hoa_don, ma_dv_ngoai, SoLuong, DonGiaTaiThoiDiemDat, ThanhTien, NGAY_DANG_KY, khung_gio_dang_ky) VALUES (?, ?, ?, ?, ?, SYSDATE, ?)";
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, sf.getInvoiceId());
            ps.setString(2, sf.getServiceId());
            ps.setInt(3, 1); // Số lượng giờ là 1 cho mỗi dòng
            ps.setDouble(4, sf.getUnitPrice());
            ps.setDouble(5, sf.getUnitPrice()); // Thành tiền = 1 * Đơn giá
            ps.setString(6, sf.getKhungGio()); // Thêm khung giờ
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // SỬA LẠI: Cần cả MaHD và ma_dv_ngoai để xác định dòng cần update
    public boolean updateQuantity(SubscribedFacility sf) {
        String sql = "UPDATE CHITIET_DICHVU_HD SET SoLuong = ?, ThanhTien = ? WHERE ma_hoa_don = ? AND ma_dv_ngoai = ?";
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, sf.getQuantity());
            ps.setDouble(2, sf.getLineTotal());
            ps.setInt(3, sf.getInvoiceId());
            ps.setString(4, sf.getServiceId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // SỬA LẠI: Cần cả MaHD và MaDVNgoai để xác định dòng cần xóa
    public boolean delete(int invoiceId, String serviceId) {
        String sql = "DELETE FROM CHITIET_DICHVU_HD WHERE ma_hoa_don = ? AND ma_dv_ngoai = ?";
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, invoiceId);
            ps.setString(2, serviceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int countUsageForTimeSlot(String serviceId, Date date, String timeSlot) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM CHITIET_DICHVU_HD WHERE ma_dv_ngoai = ? AND TRUNC(ngay_dang_ky) = TRUNC(?) AND khung_gio_dang_ky = ?";
        
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serviceId);
            ps.setDate(2, new java.sql.Date(date.getTime()));
            ps.setString(3, timeSlot);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
