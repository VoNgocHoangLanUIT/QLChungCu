/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author DELL
 */
public class Facility {
    private String serviceId;
    private String serviceName;
    private String manufacturer;
    private String unit;
    private String stockQuantity; // Dùng String để chứa được cả số và "unlimited"
    private double price;

    // Constructors (nên có một constructor trống và một đủ tham số)
    public Facility() {}

    public Facility(String serviceId, String serviceName, String manufacturer, String unit, String stockQuantity, double price) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.manufacturer = manufacturer;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.price = price;
    }

    // Getters and Setters cho tất cả các thuộc tính
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(String stockQuantity) { this.stockQuantity = stockQuantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
