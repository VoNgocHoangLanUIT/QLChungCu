/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;
import DAO.ComplaintDAO;
import Model.Complaint;
import java.util.List;

/**
 *
 * @author DELL
 */

/**
 * Cung cấp các dịch vụ logic nghiệp vụ liên quan đến Phản Ánh.
 */
public class ComplaintService {

    private ComplaintDAO complaintDAO;

    public ComplaintService() {
        this.complaintDAO = new ComplaintDAO();
    }

    /**
     * Lấy tất cả các phản ánh.
     * @return Danh sách các đối tượng Complaint.
     */
    public List<Complaint> getAllComplaints() {
        return complaintDAO.getAllComplaints();
    }

    /**
     * Thêm một phản ánh mới sau khi kiểm tra dữ liệu đầu vào.
     * @param complaint Đối tượng Complaint cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addComplaint(Complaint complaint) {
        // Có thể thêm các logic kiểm tra dữ liệu ở đây
        if (complaint.getComplaintId() == null || complaint.getComplaintId().trim().isEmpty() ||
            complaint.getTitle() == null || complaint.getTitle().trim().isEmpty()) {
            return false; // Dữ liệu không hợp lệ
        }
        return complaintDAO.addComplaint(complaint);
    }

    /**
     * Cập nhật trạng thái của một phản ánh.
     */
    public boolean updateComplaint(Complaint complaint) {
        // Có thể thêm các logic kiểm tra dữ liệu ở đây nếu cần
        if (complaint == null || complaint.getComplaintId() == null || complaint.getComplaintId().trim().isEmpty()) {
            return false; // Dữ liệu không hợp lệ
        }
        return complaintDAO.updateComplaint(complaint);
    }

    /**
     * Xóa một phản ánh.
     * @param complaintId Mã của phản ánh cần xóa.
     * @return true nếu xóa thành công.
     */
    public boolean deleteComplaint(String complaintId) {
        if (complaintId == null || complaintId.trim().isEmpty()) {
            return false;
        }
        return complaintDAO.deleteComplaint(complaintId);
    }
}
