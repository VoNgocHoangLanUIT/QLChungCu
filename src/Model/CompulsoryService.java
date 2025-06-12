package Model;

import java.math.BigDecimal;

public class CompulsoryService {
    private String serviceId;
    private String serviceName;
    private String providerId;
    private String unit;
    private BigDecimal price;

    // Constructors
    public CompulsoryService() {
    }

    public CompulsoryService(String serviceId, String serviceName, String providerId, String unit, BigDecimal price) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.providerId = providerId;
        this.unit = unit;
        this.price = price;
    }

    // Getters and Setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
