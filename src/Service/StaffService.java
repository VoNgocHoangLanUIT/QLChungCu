/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

/**
 *
 * @author DELL
 */
import DAO.StaffDAO;
import Model.Staff;
import java.util.List;

public class StaffService {

    private StaffDAO staffDAO;

    public StaffService() {
        this.staffDAO = new StaffDAO();
    }

    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }
}
