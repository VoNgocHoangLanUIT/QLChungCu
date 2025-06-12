package DAO;

import ConnectDB.ConnectionUtils;
import Model.Resident;
import java.sql.*;

public class ResidentDAO {

    /**
     * Lấy thông tin một cư dân bằng mã cư dân.
     * @param residentId Mã của cư dân cần tìm.
     * @return Đối tượng Resident nếu tìm thấy, ngược lại trả về null.
     */
    public Resident getResidentById(String residentId) {
        String sql = "SELECT ma_cu_dan, ma_can_ho, ho_ten, so_dien_thoai FROM CuDan WHERE ma_cu_dan = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, residentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Resident resident = new Resident();
                    resident.setResidentId(rs.getString("ma_cu_dan"));
                    resident.setApartmentId(rs.getString("ma_can_ho"));
                    resident.setFullName(rs.getString("ho_ten"));
                    resident.setPhoneNumber(rs.getString("so_dien_thoai"));
                    return resident;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    /**
     * Kiểm tra xem một cư dân đã tồn tại qua mã cư dân hay chưa.
     * @param residentId Mã cư dân cần kiểm tra.
     * @return true nếu tồn tại, ngược lại false.
     */
    public boolean residentExists(String residentId) {
        String sql = "SELECT 1 FROM CuDan WHERE ma_cu_dan = ?";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, residentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu rs.next() là true, nghĩa là đã tìm thấy bản ghi
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tạo một cư dân mới với thông tin cơ bản.
     * @param residentId Mã của cư dân mới.
     * @return true nếu tạo thành công, ngược lại false.
     */
    public boolean createResident(String residentId) {
        // Các trường khác có thể là null hoặc có giá trị mặc định
        String sql = "INSERT INTO CuDan (ma_cu_dan, ho_ten) VALUES (?, ?)";
        try (Connection conn = ConnectionUtils.getMyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, residentId);
            ps.setString(2, "New Resident"); // Tên mặc định, có thể thay đổi sau
            return ps.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            // Lỗi có thể xảy ra nếu resident đã tồn tại (vi phạm khóa chính).
            // Ghi lại lỗi nhưng không coi đây là lỗi nghiêm trọng ở tầng service.
            System.err.println("Lỗi khi tạo cư dân, có thể đã tồn tại: " + e.getMessage());
            return false;
        }
    }
}
