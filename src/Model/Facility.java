package Model;

public class Facility {
    private String serviceId;
    private String serviceName;
    private String manufacturer;
    private String unit;
    private String stockQuantity;
    private double price;
    private String description;
    private boolean deleted; // Thuộc tính mới cho soft delete

    // Constructors
    public Facility() {}

    // CẬP NHẬT CONSTRUCTOR ĐẦY ĐỦ THAM SỐ
    public Facility(String serviceId, String serviceName, String manufacturer, String unit, String stockQuantity, double price, String description) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.manufacturer = manufacturer;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.description = description;
        this.deleted = false; // Mặc định là chưa xóa
    }

    // --- Getters and Setters ---
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
