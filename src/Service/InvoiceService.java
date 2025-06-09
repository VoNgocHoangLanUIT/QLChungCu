/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import DAO.InvoiceDAO;
import Model.SubscribedFacility;
import java.util.List;

/**
 *
 * @author DELL
 */

public class InvoiceService {
    private InvoiceDAO invoiceDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public int createInvoice(double initialFee) {
        // Có thể thêm logic ở đây, ví dụ kiểm tra initialFee > 0
        return invoiceDAO.addInvoice(initialFee);
    }

    public void updateInvoiceTotal(int invoiceId, double newTotal) {
        invoiceDAO.updateInvoiceTotal(invoiceId, newTotal);
    }

    // Logic nghiệp vụ: Tính tổng tiền từ danh sách các dịch vụ đã đăng ký
    public double calculateTotalFee(List<SubscribedFacility> subscribedFacilities) {
        double total = 0.0;
        for (SubscribedFacility sf : subscribedFacilities) {
            total += sf.getLineTotal();
        }
        return total;
    }
}
