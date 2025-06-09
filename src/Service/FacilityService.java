/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.FacilityDAO;
import DAO.SubscribedFacilityDAO;
import Model.Facility;
import java.util.List;
import java.util.Date;

/**
 *
 * @author DELL
 */
public class FacilityService {
    private FacilityDAO facilityDAO;
    private SubscribedFacilityDAO subscribedFacilityDAO;
    
    public FacilityService() {
        this.facilityDAO = new FacilityDAO();
        this.subscribedFacilityDAO = new SubscribedFacilityDAO();
    }

    public List<Facility> getAllFacilities() {
        return facilityDAO.getAllFacilities();
    }
    
    public boolean isTimeSlotAvailable(Facility facility, String timeSlot) {
        String capacityStr = facility.getStockQuantity(); // Lấy giới hạn từ cột so_luong
        if (capacityStr == null || capacityStr.trim().equalsIgnoreCase("unlimited")) {
            return true; // Luôn có sẵn nếu không giới hạn
        }
        try {
            int capacity = Integer.parseInt(capacityStr);
            // Đếm số lượt đã dùng cho khung giờ này hôm nay
            int usedToday = subscribedFacilityDAO.countUsageForTimeSlot(facility.getServiceId(), new Date(), timeSlot);
            // Nếu số lượt đã dùng < sức chứa, thì vẫn còn chỗ
            return usedToday < capacity;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
