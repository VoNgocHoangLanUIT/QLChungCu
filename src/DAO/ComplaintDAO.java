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
     * Lấy tất cả các phản ánh từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng Complaint.
     */
    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        // Lấy các cột tương ứng với các thuộc tính của lớp Complaint
        String sql = "SELECT ma_phan_anh, tieu_de, ma_can_ho, ngay_phan_anh, trang_thai, mo_ta FROM PhanAnh ORDER BY ngay_phan_anh DESC";

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
        String sql = "INSERT INTO PhanAnh (ma_phan_anh, tieu_de, ma_can_ho, trang_thai, mo_ta) VALUES (?, ?, ?, ?, ?)";
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
     * Cập nhật trạng thái của một phản ánh.
     * @param complaintId Mã của phản ánh cần cập nhật.
     * @param newStatus Trạng thái mới.
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
     * Xóa một phản ánh khỏi cơ sở dữ liệu.
     * @param complaintId Mã của phản ánh cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteComplaint(String complaintId) {
        String sql = "DELETE FROM PhanAnh WHERE ma_phan_anh = ?";
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
