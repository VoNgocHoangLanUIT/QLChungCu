package DAO;

import ConnectDB.ConnectionUtils;
import Model.Invoice;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        // Sửa đổi câu lệnh SQL để chỉ lấy các hóa đơn chưa bị xóa (da_xoa = 0)
        String sql = "SELECT * FROM HoaDon WHERE da_xoa = 0 ORDER BY ma_hoa_don DESC";
        try (Connection conn = ConnectionUtils.getMyConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("ma_hoa_don"));
                invoice.setCreationDate(rs.getDate("ngay_lap"));
                invoice.setTotalFee(rs.getDouble("tong_phi"));
                invoice.setResidentId(rs.getString("ma_cu_dan"));
                invoice.setCashReceived(rs.getDouble("tien_nhan"));
                invoice.setStatus(rs.getString("trang_thai"));
                invoice.setChangeReturned(rs.getDouble("tien_thua"));
                invoice.setDeleted(rs.getInt("da_xoa") == 1);
                invoices.add(invoice);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return invoices;
    }
    
    public int addInvoice(String residentId, double totalFee, String status) {
        int newInvoiceId = -1;
        // Cập nhật câu lệnh INSERT để bao gồm cột da_xoa (mặc định là 0)
        String sql = "INSERT INTO HoaDon (tong_phi, ma_cu_dan, trang_thai, da_xoa) VALUES (?, ?, ?, 0) RETURNING ma_hoa_don INTO ?";
        String plsql = "BEGIN " + sql + "; END;";

        try (Connection con = ConnectionUtils.getMyConnection();
             CallableStatement cs = con.prepareCall(plsql)) {

            cs.setDouble(1, totalFee);
            cs.setString(2, residentId);
            cs.setString(3, status);
            cs.registerOutParameter(4, Types.INTEGER);

            cs.execute();
            newInvoiceId = cs.getInt(4);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newInvoiceId;
    }

    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE HoaDon SET tong_phi = ?, ma_cu_dan = ?, tien_nhan = ?, trang_thai = ?, tien_thua = ? WHERE ma_hoa_don = ?";
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, invoice.getTotalFee());
            ps.setString(2, invoice.getResidentId());
            ps.setDouble(3, invoice.getCashReceived());
            ps.setString(4, invoice.getStatus());
            ps.setDouble(5, invoice.getChangeReturned());
            ps.setInt(6, invoice.getInvoiceId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInvoice(int invoiceId) {
        // Thay đổi từ DELETE sang UPDATE để thực hiện soft delete.
        // Không xóa các chi tiết liên quan để bảo toàn dữ liệu.
        String sql = "UPDATE HoaDon SET da_xoa = 1 WHERE ma_hoa_don = ?";
        
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, invoiceId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void updateInvoiceTotal(int invoiceId, double newTotalFee) {
        String sql = "UPDATE HoaDon SET tong_phi = ? WHERE ma_hoa_don = ?";
        
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, newTotalFee);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateInvoicePayment(int invoiceId, double totalFee, double cashReceived, double changeReturned, String status) {
        String sql = "UPDATE HoaDon SET tong_phi = ?, tien_nhan = ?, tien_thua = ?, trang_thai = ? WHERE ma_hoa_don = ?";
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, totalFee);
            ps.setDouble(2, cashReceived);
            ps.setDouble(3, changeReturned);
            ps.setString(4, status);
            ps.setInt(5, invoiceId);
            
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
