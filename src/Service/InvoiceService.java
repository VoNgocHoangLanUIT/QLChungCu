package Service;

import DAO.InvoiceDAO;
import Model.Invoice;
import Model.SubscribedFacility;
import java.util.List;

public class InvoiceService {
    private InvoiceDAO invoiceDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    public int createInvoice(String residentId, double initialFee) {
        if (residentId == null || residentId.trim().isEmpty()) {
            return -1;
        }
        return invoiceDAO.addInvoice(residentId, initialFee, "Pending"); // Mặc định là Pending
    }
    
    public boolean updateInvoice(Invoice invoice) {
        return invoiceDAO.updateInvoice(invoice);
    }
    
    public boolean deleteInvoice(int invoiceId) {
        return invoiceDAO.deleteInvoice(invoiceId);
    }

    public void updateInvoiceTotal(int invoiceId, double newTotal) {
        invoiceDAO.updateInvoiceTotal(invoiceId, newTotal);
    }
    
    public void finalizePayment(int invoiceId, double totalFee, double cashReceived, double changeReturned, String status) {
        invoiceDAO.updateInvoicePayment(invoiceId, totalFee, cashReceived, changeReturned, status);
    }
    
    public double calculateTotalFee(List<SubscribedFacility> subscribedFacilities) {
        double total = 0.0;
        for (SubscribedFacility sf : subscribedFacilities) {
            total += sf.getLineTotal();
        }
        return total;
    }
}
