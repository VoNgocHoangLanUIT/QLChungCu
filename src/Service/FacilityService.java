package Service;

import DAO.FacilityDAO;
import DAO.SubscribedFacilityDAO;
import Model.Facility;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
    
    public boolean addFacility(Facility facility) {
        if (facility.getServiceId() == null || facility.getServiceId().trim().isEmpty()) {
            return false;
        }
        return facilityDAO.addFacility(facility);
    }
    
    public boolean updateFacility(Facility facility) {
        if (facility.getServiceId() == null || facility.getServiceId().trim().isEmpty()) {
            return false;
        }
        return facilityDAO.updateFacility(facility);
    }
    
    public boolean deleteFacility(String facilityId) {
        if (facilityId == null || facilityId.trim().isEmpty()) {
            return false;
        }
        return facilityDAO.deleteFacility(facilityId);
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
    
    public List<String> getBookedTimeSlotsForInvoice(int invoiceId, String serviceId) {
        // Nếu không có hóa đơn hợp lệ thì trả về danh sách rỗng
        if (invoiceId <= 0) {
            return new ArrayList<>();
        }
        // Gọi đến DAO để lấy dữ liệu cho ngày hôm nay
        return subscribedFacilityDAO.getBookedTimeSlotsForInvoice(invoiceId, serviceId, new Date());
    }
}
