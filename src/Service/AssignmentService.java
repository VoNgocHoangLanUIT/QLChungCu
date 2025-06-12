/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

/**
 *
 * @author DELL
 */
import DAO.AssignmentDAO;
import DAO.ComplaintDAO;

public class AssignmentService {

    private AssignmentDAO assignmentDAO;
    private ComplaintDAO complaintDAO; // To update the complaint's status

    public AssignmentService() {
        this.assignmentDAO = new AssignmentDAO();
        this.complaintDAO = new ComplaintDAO();
    }

    /**
     * Creates an assignment record and updates the complaint status.
     * @param complaintId The ID of the complaint.
     * @param staffId The ID of the assigned staff member.
     * @return true if both actions succeed.
     */
    public boolean assignComplaintToStaff(String complaintId, String staffId) {
        // Step 1: Add a record to the Assignment table
        boolean assignmentSuccess = assignmentDAO.addAssignment(complaintId, staffId);

        // Step 2: If the assignment was successful, update the complaint's status
        if (assignmentSuccess) {
            // Update status to "In Progress"
            boolean updateStatusSuccess = complaintDAO.updateComplaintStatus(complaintId, "In Progress");
            return updateStatusSuccess;
        }

        return false; // Return false if the first step failed
    }
}
