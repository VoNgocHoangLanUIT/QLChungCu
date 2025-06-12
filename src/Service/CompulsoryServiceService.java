package Service;

import DAO.CompulsoryServiceDAO;
import Model.CompulsoryService;
import java.util.List;

public class CompulsoryServiceService {
    
    private CompulsoryServiceDAO compulsoryServiceDAO;

    public CompulsoryServiceService() {
        this.compulsoryServiceDAO = new CompulsoryServiceDAO();
    }

    public List<CompulsoryService> getAllServices() {
        return compulsoryServiceDAO.getAllServices();
    }
    
    public boolean addService(CompulsoryService service) {
        if (service.getServiceId() == null || service.getServiceId().trim().isEmpty()) {
            System.err.println("Service ID cannot be empty.");
            return false;
        }
        return compulsoryServiceDAO.addService(service);
    }
    
    public boolean updateService(CompulsoryService service) {
        if (service.getServiceId() == null || service.getServiceId().trim().isEmpty()) {
            System.err.println("Service ID cannot be empty for update.");
            return false;
        }
        return compulsoryServiceDAO.updateService(service);
    }
    
    public boolean deleteService(String serviceId) {
        if (serviceId == null || serviceId.trim().isEmpty()) {
            System.err.println("Service ID cannot be empty for deletion.");
            return false;
        }
        return compulsoryServiceDAO.deleteService(serviceId);
    }
}
