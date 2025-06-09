/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.SubscribedFacilityDAO;
import Model.SubscribedFacility;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class SubscribedFacilityService {
    private SubscribedFacilityDAO dao;

    public SubscribedFacilityService() {
        this.dao = new SubscribedFacilityDAO();
    }

    public List<SubscribedFacility> getByInvoiceId(int invoiceId) {
        if (invoiceId <= 0) {
            return new ArrayList<>();
        }
        return dao.getByInvoiceId(invoiceId);
    }
    
    // Gộp logic thêm hoặc cập nhật
    public void addOrUpdateFacility(SubscribedFacility sf) {
        // Thử thêm mới
        boolean added = dao.add(sf);
        
        // Nếu thêm mới thất bại (vì vi phạm khóa chính - đã tồn tại) thì thực hiện cập nhật
        if (!added) {
            dao.updateQuantity(sf);
        }
    }

    public boolean deleteFacilityFromInvoice(int invoiceId, String serviceId) {
        return dao.delete(invoiceId, serviceId);
    }
}