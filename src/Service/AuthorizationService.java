package Service;

import DAO.AccountDAO;
import Model.Account;
import java.util.List;

public class AuthorizationService {

    private AccountDAO accountDAO;
    private ResidentService residentService; // Thêm ResidentService

    public AuthorizationService() {
        this.accountDAO = new AccountDAO();
        this.residentService = new ResidentService(); // Khởi tạo ResidentService
    }

    /**
     * Gets a list of all accounts for the authorization view.
     * @return A list of Account objects.
     */
    public List<Account> getAllAccountsForAuthorization() {
        // Trả về trực tiếp danh sách Account từ DAO, không cần chuyển đổi
        return accountDAO.getAllAccountsForAuthorization();
    }

    /**
     * Updates the role and resident ID for a given user.
     * @param username The username of the account to update.
     * @param newResidentId The new resident ID (can be null or "N/A").
     * @param newRoleName The name of the new role.
     * @return true if both updates are successful.
     */
    public boolean updateAuthorization(String username, String newResidentId, String newRoleName) {
        // Tìm tài khoản để lấy ID
        Account account = accountDAO.findByUsername(username);
        if (account == null) {
            System.err.println("Không thể cập nhật: Không tìm thấy tài khoản cho username " + username);
            return false;
        }
        int accountId = account.getAccountId();

        // Tìm ID của vai trò từ tên
        int roleId = accountDAO.findRoleIdByName(newRoleName);
        if (roleId == -1) {
            System.err.println("Không thể cập nhật: Không tìm thấy vai trò '" + newRoleName + "'.");
            return false;
        }
        
        // SỬA LỖI: Tạo cư dân mới (nếu cần) TRƯỚC KHI cập nhật tài khoản để đảm bảo ràng buộc khóa ngoại
        if (newResidentId != null && !newResidentId.trim().isEmpty() && !newResidentId.equalsIgnoreCase("N/A")) {
            residentService.createResidentIfNotExist(newResidentId);
        }

        // Thực hiện cập nhật
        boolean residentIdUpdateSuccess = accountDAO.updateResidentIdForAccount(accountId, newResidentId);
        boolean roleUpdateSuccess = accountDAO.upsertAccountRole(accountId, roleId);

        return residentIdUpdateSuccess && roleUpdateSuccess;
    }
}
