package Model;

import java.util.Date;

public class Invoice {
    private int invoiceId;
    private Date creationDate;
    private double totalFee;
    private String residentId;
    private double cashReceived;
    private String status;
    private double changeReturned;
    private boolean deleted; // Thuộc tính mới cho soft delete

    // Constructors
    public Invoice() {
    }
    
    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public String getResidentId() {
        return residentId;
    }

    public void setResidentId(String residentId) {
        this.residentId = residentId;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(double cashReceived) {
        this.cashReceived = cashReceived;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getChangeReturned() {
        return changeReturned;
    }

    public void setChangeReturned(double changeReturned) {
        this.changeReturned = changeReturned;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
