/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.ParkingDAO;
import Model.ParkingSlot;
import java.util.List;

/**
 *
 * @author DELL
 */
public class ParkingService {
    
    private ParkingDAO parkingDAO;

    public ParkingService() {
        this.parkingDAO = new ParkingDAO();
    }

    public List<ParkingSlot> getAllParkingSlots() {
        return parkingDAO.getAllParkingSlots();
    }
    
    public int addParkingSlot(ParkingSlot slot) {
        // Kiểm tra xem slot name có rỗng không
        if (slot.getSlotName() == null || slot.getSlotName().trim().isEmpty()) {
            return -1; // Trả về mã lỗi
        }
        return parkingDAO.addParkingSlot(slot);
    }
    
    public int updateParkingSlot(ParkingSlot slot) {
        return parkingDAO.updateParkingSlot(slot);
    }
    
    public int deleteParkingSlot(String slotName) {
        return parkingDAO.deleteParkingSlot(slotName);
    }
}