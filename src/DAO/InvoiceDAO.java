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
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class InvoiceDAO {

    public int addInvoice(double totalFee) {
        int newInvoiceId = -1; // Giá trị mặc định nếu thất bại
        // Câu lệnh SQL của bạn, với tên bảng và cột đã được chuẩn hóa
        String sql = "INSERT INTO HoaDon (tong_phi) VALUES (?) RETURNING ma_hoa_don INTO ?";
        
        // Bọc trong khối BEGIN...END; cho CallableStatement
        String plsql = "BEGIN " + sql + "; END;";

        try (Connection con = ConnectionUtils.getMyConnection();
             CallableStatement cs = con.prepareCall(plsql)) {

            // Truyền tham số vào
            cs.setDouble(1, totalFee);
            // Đăng ký tham số out để nhận ID trả về
            cs.registerOutParameter(2, Types.INTEGER);

            cs.execute(); // Thực thi lệnh

            // Lấy ID hóa đơn từ tham số out
            newInvoiceId = cs.getInt(2);

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn (addInvoice): " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver CSDL: " + e.getMessage());
            e.printStackTrace();
        }

        return newInvoiceId;
    }

    public void updateInvoiceTotal(int invoiceId, double newTotalFee) {
        // Câu lệnh SQL của bạn, với tên bảng và cột đã được chuẩn hóa
        String sql = "UPDATE HoaDon SET tong_phi = ? WHERE ma_hoa_don = ?";
        
        try (Connection con = ConnectionUtils.getMyConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, newTotalFee);
            ps.setInt(2, invoiceId);
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tổng tiền hóa đơn (updateInvoiceTotal): " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver CSDL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
