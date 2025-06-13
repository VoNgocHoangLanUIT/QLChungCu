/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import ConnectDB.ConnectionUtils; 
import Model.Complaint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author DELL
 */

public class ComplaintDAO {

    /**
     * Lấy tất cả các phản ánh chưa bị xóa từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng Complaint.
     */
    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        // Sửa đổi câu lệnh SQL để chỉ lấy các phản ánh có da_xoa = 0
        String sql = "SELECT ma_phan_anh, tieu_de, ma_can_ho, ngay_phan_anh, trang_thai, mo_ta, da_xoa FROM PhanAnh WHERE da_xoa = 0 ORDER BY ngay_phan_anh DESC";

        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Complaint complaint = new Complaint();
                complaint.setComplaintId(rs.getString("ma_phan_anh"));
                complaint.setTitle(rs.getString("tieu_de"));
                complaint.setApartmentId(rs.getString("ma_can_ho"));
                complaint.setComplaintDate(rs.getDate("ngay_phan_anh"));
                complaint.setStatus(rs.getString("trang_thai"));
                complaint.setDescription(rs.getString("mo_ta"));
                // Lấy giá trị của cột da_xoa
                complaint.setDeleted(rs.getInt("da_xoa") == 1);
                complaints.add(complaint);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return complaints;
    }

    /**
     * Thêm một phản ánh mới.
     * @param complaint Đối tượng Complaint chứa thông tin cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addComplaint(Complaint complaint) {
        // Cập nhật câu lệnh INSERT để thêm giá trị cho cột da_xoa (mặc định là 0)
        String sql = "INSERT INTO PhanAnh (ma_phan_anh, tieu_de, ma_can_ho, trang_thai, mo_ta, da_xoa) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, complaint.getComplaintId());
            ps.setString(2, complaint.getTitle());
            ps.setString(3, complaint.getApartmentId());
            ps.setString(4, complaint.getStatus());
            ps.setString(5, complaint.getDescription());

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin của một phản ánh.
     * @param complaint Đối tượng Complaint chứa thông tin cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateComplaint(Complaint complaint) {
        String sql = "UPDATE PhanAnh SET tieu_de = ?, ma_can_ho = ?, trang_thai = ?, mo_ta = ? WHERE ma_phan_anh = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, complaint.getTitle());
            ps.setString(2, complaint.getApartmentId());
            ps.setString(3, complaint.getStatus());
            ps.setString(4, complaint.getDescription());
            ps.setString(5, complaint.getComplaintId()); // Mệnh đề WHERE

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateComplaintStatus(String complaintId, String newStatus) {
        String sql = "UPDATE PhanAnh SET trang_thai = ? WHERE ma_phan_anh = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, complaintId);

            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * "Xóa mềm" một phản ánh bằng cách cập nhật cột da_xoa = 1.
     * @param complaintId Mã của phản ánh cần xóa.
     * @return true nếu "xóa" thành công, false nếu thất bại.
     */
    public boolean deleteComplaint(String complaintId) {
        // Thay đổi câu lệnh từ DELETE sang UPDATE để thực hiện soft delete
        String sql = "UPDATE PhanAnh SET da_xoa = 1 WHERE ma_phan_anh = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, complaintId);
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
