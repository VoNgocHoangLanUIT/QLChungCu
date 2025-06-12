package Service;

import DAO.ResidentDAO;
import Model.Resident;

public class ResidentService {
    private ResidentDAO residentDAO;

    public ResidentService() {
        this.residentDAO = new ResidentDAO();
    }

    public Resident getResidentById(String residentId) {
        if (residentId == null || residentId.trim().isEmpty()) {
            return null;
        }
        return residentDAO.getResidentById(residentId);
    }
    
    /**
     * Tạo một cư dân mới nếu chưa tồn tại.
     * @param residentId Mã của cư dân cần tạo.
     */
    public void createResidentIfNotExist(String residentId) {
        if (residentId == null || residentId.trim().isEmpty() || residentId.equalsIgnoreCase("N/A")) {
            return; // Không làm gì nếu mã cư dân không hợp lệ
        }
        
        // Kiểm tra xem cư dân đã tồn tại chưa
        if (!residentDAO.residentExists(residentId)) {
            // Nếu chưa, tạo mới
            residentDAO.createResident(residentId);
            System.out.println("Đã tạo cư dân mới với mã: " + residentId);
        }
    }
}
