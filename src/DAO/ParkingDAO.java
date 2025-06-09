package DAO;
import ConnectDB.ConnectionUtils; // Giữ lại kết nối CSDL của bạn
import Model.ParkingSlot;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */
public class ParkingDAO {

    /**
     * Lấy tất cả các chỗ đậu xe từ cơ sở dữ liệu.
     * @return một danh sách các đối tượng ParkingSlot.
     */
    public List<ParkingSlot> getAllParkingSlots() {
        List<ParkingSlot> slots = new ArrayList<>();
        String sql = "SELECT so_o_do, loai_o_do, loai_xe, trang_thai, bien_so_xe FROM ODoXe"; // Tên cột trong DB

        try (Connection con = ConnectionUtils.getMyConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Tạo đối tượng ParkingSlot từ mỗi dòng kết quả
                ParkingSlot slot = new ParkingSlot(
                    rs.getString("so_o_do"),
                    rs.getString("loai_o_do"),
                    rs.getString("loai_xe"),
                    rs.getString("trang_thai"),
                    rs.getString("bien_so_xe")
                );
                slots.add(slot);
            }
        } catch (SQLException | ClassNotFoundException e) {
            // In lỗi ra console để debug
            System.err.println("Lỗi khi lấy danh sách chỗ đậu xe: " + e.getMessage());
        }
        return slots;
    }

    /**
     * Thêm một chỗ đậu xe mới vào cơ sở dữ liệu.
     * @param slot Đối tượng ParkingSlot chứa thông tin cần thêm.
     * @return số dòng bị ảnh hưởng (thường là 1 nếu thành công).
     */
    public int addParkingSlot(ParkingSlot slot) {
        int result = 0;
        String call = "{call ADD_PARKINGSLOT(?,?,?,?,?)}";

        try (Connection con = ConnectionUtils.getMyConnection();
             CallableStatement cs = con.prepareCall(call)) {

            // Truyền tham số từ đối tượng ParkingSlot vào stored procedure
            cs.setString(1, slot.getSlotName());
            cs.setString(2, slot.getSlotType());
            cs.setString(3, slot.getVehicle());
            cs.setString(4, slot.getStatus());
            cs.setString(5, slot.getLicensePlate());

            result = cs.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi thêm chỗ đậu xe: " + e.getMessage());
        }
        return result;
    }

    /**
     * Cập nhật thông tin một chỗ đậu xe.
     * @param slot Đối tượng ParkingSlot chứa thông tin cần cập nhật.
     * @return số dòng bị ảnh hưởng (thường là 1 nếu thành công).
     */
    public int updateParkingSlot(ParkingSlot slot) {
        int result = 0;
        String call = "{call UPDATE_PARKINGSLOT(?,?,?,?,?)}";

        try (Connection con = ConnectionUtils.getMyConnection();
             CallableStatement cs = con.prepareCall(call)) {

            // Truyền tham số từ đối tượng ParkingSlot
            cs.setString(1, slot.getSlotName());
            cs.setString(2, slot.getSlotType());
            cs.setString(3, slot.getVehicle());
            cs.setString(4, slot.getStatus());
            cs.setString(5, slot.getLicensePlate());

            result = cs.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi cập nhật chỗ đậu xe: " + e.getMessage());
        }
        return result;
    }

    /**
     * Xóa một chỗ đậu xe khỏi cơ sở dữ liệu.
     * @param slotName Tên (ID) của chỗ đậu xe cần xóa.
     * @return số dòng bị ảnh hưởng (thường là 1 nếu thành công).
     */
    public int deleteParkingSlot(String slotName) {
        int result = 0;
        String call = "{call DELETE_PARKINGSLOT(?)}";

        try (Connection con = ConnectionUtils.getMyConnection();
             CallableStatement cs = con.prepareCall(call)) {

            cs.setString(1, slotName);
            result = cs.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi xóa chỗ đậu xe: " + e.getMessage());
        }
        return result;
    }
}
