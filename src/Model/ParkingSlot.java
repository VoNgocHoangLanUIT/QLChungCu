/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author DELL
 */
public class ParkingSlot {
    private String slotName;
    private String slotType;
    private String vehicle;
    private String status;
    private String licensePlate;

    // Constructors
    public ParkingSlot() {
    }

    public ParkingSlot(String slotName, String slotType, String vehicle, String status, String licensePlate) {
        this.slotName = slotName;
        this.slotType = slotType;
        this.vehicle = vehicle;
        this.status = status;
        this.licensePlate = licensePlate;
    }

    // Getters and Setters for all fields
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
}
