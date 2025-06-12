/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author DELL
 */
public class Complaint {

    private String complaintId;
    private String title;
    private String apartmentId; // Tương ứng với cột ma_can_ho
    private Date complaintDate; // Tương ứng với cột ngay_phan_anh
    private String status;
    private String description;

    // Constructors
    public Complaint() {
    }

    public Complaint(String complaintId, String title, String apartmentId, Date complaintDate, String status, String description) {
        this.complaintId = complaintId;
        this.title = title;
        this.apartmentId = apartmentId;
        this.complaintDate = complaintDate;
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(String apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Date getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(Date complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

