/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Process.*;
import View.Booking.BookFacilitiesForm;
import View.Parking.*;
import View.Service.AddCompulsoryServiceForm;
import View.Service.UpdateCompulsoryServiceForm;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import Model.*;
import Model.DTO.AuthorizationInfo;
import Service.*;
import View.Complaint.AddComplaintForm;
import View.Complaint.DetailComplaintForm;
import View.Complaint.UpdateComplaintForm;
import View.Facility.AddFacilityForm;
import View.Facility.DetailFacilityForm;
import View.Facility.UpdateFacilityForm;
import View.Invoice.DetailInvoiceForm;
import View.Invoice.UpdateInvoiceForm;
import View.Complaint.AssignComplaintForm;
import View.Authorization.UpdateAuthorization;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

// THÊM CÁC IMPORT CẦN THIẾT CHO VIỆC XUẤT PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author DELL
 */
public class HomeAdminForm extends javax.swing.JFrame {

    /**
     * Creates new form HomeForm
     */
    // --- Services ---
    private ParkingService parkingService;
    private FacilityService facilityService;
    private InvoiceService invoiceService;
    private SubscribedFacilityService subscribedFacilityService;
    private ComplaintService complaintService;
    private StaffService staffService; 
    private AssignmentService assignmentService; 
    private ResidentService residentService;
    private AuthorizationService authorizationService;
    private CompulsoryServiceService compulsoryServiceService;
    
    private String currentResidentId;

    // --- UI Helpers ---
    private SetLayoutPanel s;
    private ButtonEffectGroup group;
    
    public HomeAdminForm(String residentId) {
        
        this.currentResidentId = residentId;
        
        initComponents();
        this.parkingService = new ParkingService();
        this.facilityService = new FacilityService();
        this.invoiceService = new InvoiceService();
        this.subscribedFacilityService = new SubscribedFacilityService();
        this.complaintService = new ComplaintService();
        this.staffService = new StaffService(); 
        this.assignmentService = new AssignmentService(); 
        this.residentService = new ResidentService();
        this.authorizationService = new AuthorizationService();
        this.compulsoryServiceService = new CompulsoryServiceService();
        
        s = new SetLayoutPanel(scrollPane, contentPanel, mainPanel);
        
//----------------------------------------MENU--------------------------------------------
        String[] iconPaths = {
            "/icon/icon1.png",  // Icon cho Button 1
            "/icon/icon2.png",  // Icon cho Button 2
            "/icon/icon3.png", 
            "/icon/icon4.png", 
            "/icon/icon5.png", 
            "/icon/icon6.png", 
            "/icon/icon7.png", 
            "/icon/icon8.png", 
            "/icon/icon9.png", 
        };

        // Gán các button vào ButtonEffectGroup
        JButton[] buttons = {homeButton, residentManagementButton, parkingManagementButton, 
                            complaintsButton, serviceFacilityButton, bookingButton,
                            profileButton, authorizationButton, logOutButton};

        // Tạo ButtonEffectGroup và truyền vào các button và icon
        group = new ButtonEffectGroup(buttons, iconPaths, serviceSub, serviceFacilityButton, menuPanel, logOutButton);
        SubButtonEffectGroup g = new SubButtonEffectGroup(serviceSub);
        ScrollCustomizer.customizeScrollPane(scrollPane);
        
 //----------------------------------------PARKING-----------------------------------------
        new SetupTable(searchParkingSlotField, listParkingSlotTable);     
        this.updateTableParking();
        
//---------------------------------------Booking----------------------------------
        
        new SetupTable(jTextField1, listFacilitiesTable);
        
        new SetupTable(listFacilitiesSubscribiedTable);
        
        descriptionFacilities.setLineWrap(true);
        descriptionFacilities.setWrapStyleWord(true);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        
        //xu ly chi tiet dich vu ngoai khi select
        listFacilitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = listFacilitiesTable.getSelectedRow();
                if (selectedRow != -1) {
                    String serviceId = listFacilitiesTable.getValueAt(selectedRow, 0).toString();

                    // Tìm đối tượng Facility đầy đủ từ service
                    Facility selectedFacility = facilityService.getAllFacilities().stream()
                        .filter(f -> f.getServiceId().equals(serviceId))
                        .findFirst()
                        .orElse(null);

                    if (selectedFacility != null) {
                        // --- Xử lý hình ảnh (giữ nguyên) ---
                        String path = "/icon/" + selectedFacility.getServiceId() + ".jpg";
                        java.net.URL imgUrl = getClass().getResource(path);
                        if (imgUrl != null) {
                            ImageIcon icon = new ImageIcon(imgUrl);
                            int panelWidth = jPanel4.getWidth();
                            int panelHeight = jPanel4.getHeight();
                            if (panelWidth > 0 && panelHeight > 0) {
                                Image scaled = icon.getImage().getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                                jLabel2.setIcon(new ImageIcon(scaled));
                            }
                        } else {
                            jLabel2.setIcon(null);
                        }

                        // --- Gán dữ liệu vào các text field ---
                        serviceID.setText(selectedFacility.getServiceId());
                        serviceName.setText(selectedFacility.getServiceName());
                        price.setText(String.valueOf(selectedFacility.getPrice()));

                        // THÊM DÒNG NÀY ĐỂ CẬP NHẬT MÔ TẢ
                        descriptionFacilities.setText(selectedFacility.getDescription());
                    }
                }
            }
        });
              
        this.updateTableFacilities();
        DefaultTableModel model2 = (DefaultTableModel) listFacilitiesSubscribiedTable.getModel();
        model2.setRowCount(0); // Xóa tất cả các dòng
        
        //xu ly tien thua cua khach
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateChangeReturned(); }
            public void removeUpdate(DocumentEvent e) { updateChangeReturned(); }
            public void changedUpdate(DocumentEvent e) { updateChangeReturned(); }
        };

        totalBill.getDocument().addDocumentListener(listener);
        cashReceived.getDocument().addDocumentListener(listener);
//---------------------------------------------------------------------SERVICE---------------------------------------------
        new SetupTable(searchServiceField, listServiceTable);
        updateServiceTable();
 //---------------------------------------------------------------------COMPLAINT---------------------------------------------     
        new SetupTable(searchComplaintField, listComplaintTable);
        updateComplaintsTable();
 //---------------------------------------------------------------------AUTHORIZATION---------------------------------------------  
        new SetupTable(searchAuthorizationField, listAuthorizationTable);
        updateAuthorizationTable();
 //---------------------------------------------------------------------FACILITY--------------------------------------------- 
        new SetupTable(searchFacilityField, listFacilityTable);
        updateFacilityTable();
 //---------------------------------------------------------------------INVOICE--------------------------------------------- 
        new SetupTable(searchInvoiceField, listInvoiceTable);
        updateInvoiceTable(); // Tải dữ liệu cho bảng hóa đơn
    }
//--------------------------------------------ket thuc constructor-------------------------------------------------------  
    public void showPanel(JPanel panel,String name) {
        CardLayout cl = (CardLayout) panel.getLayout();
        cl.show(panel, name);
    }

    //--------------------------------------------------------------------------------Parking------------------------------------------
    // Đảm bảo rằng bạn có một phương thức để cập nhật bảng:
    public void updateTableParking() {
        List<ParkingSlot> slots = parkingService.getAllParkingSlots(); // Trả về List<ParkingSlot>

        DefaultTableModel model = (DefaultTableModel) listParkingSlotTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // Thêm dữ liệu mới vào bảng
        for (ParkingSlot slot : slots) {
            model.addRow(new Object[]{
                slot.getSlotName(),
                slot.getSlotType(),
                slot.getVehicle(),
                slot.getStatus(),
                slot.getLicensePlate()
            });
        }
    }
    
//--------------------------------------------------------------------------------booking--------------------------------------------------
    //xu ly tien thua cua khach
    private void updateChangeReturned() {
        try {
            // Xóa ký tự phân cách nhóm trước khi chuyển đổi
            String totalText = totalBill.getText().trim().replace(" ", "");
            String cashText = cashReceived.getText().trim().replace(" ", "");

            double tongTien = Double.parseDouble(totalText);
            double tienNhan = Double.parseDouble(cashText);
            double tienThua = tienNhan - tongTien;

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            // THAY ĐỔI ĐỊNH DẠNG: Bỏ phần thập phân .00
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

            changeReturned.setText(formatter.format(tienThua));
        } catch (NumberFormatException e) {
            changeReturned.setText(""); 
        }
    }
    
    // Hàm resize JLabel hiện tại
    public void resizeCurrentLabel() {
        // Tìm JLabel đang hiển thị trong panelCard
        for (Component comp : jPanel4.getComponents()) {
            if (comp.isVisible() && comp instanceof JLabel label) {
                Object imageObj = label.getClientProperty("originalImage");
                if (imageObj instanceof Image originalImage) {
                    int width = jPanel4.getWidth();
                    int height = jPanel4.getHeight();
                    Image scaled = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaled));
                }
            }
        }
    }
    public void updateTableFacilities() {
        List<Facility> facilities = facilityService.getAllFacilities();
        DefaultTableModel model = (DefaultTableModel) listFacilitiesTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (Facility facility : facilities) {
            Object stockValue = (facility.getStockQuantity() == null || facility.getStockQuantity().trim().isEmpty())
                                ? "unlimited"
                                : facility.getStockQuantity();

            model.addRow(new Object[]{
                facility.getServiceId(),
                facility.getServiceName(),
                facility.getManufacturer(),
                facility.getUnit(),
                stockValue,
                facility.getPrice()
            });
        }
    }

    private void handleInvoiceUpdate() {
        if (invoiceID.getText().isEmpty()) {
            totalBill.setText("");
            cashReceived.setText("");
            changeReturned.setText("");
            return;
        }

        int currentInvoiceId = Integer.parseInt(invoiceID.getText());
        List<SubscribedFacility> currentCart = subscribedFacilityService.getByInvoiceId(currentInvoiceId);
        double totalFee = invoiceService.calculateTotalFee(currentCart);
        invoiceService.updateInvoiceTotal(currentInvoiceId, totalFee);
        
        // THAY ĐỔI ĐỊNH DẠNG: Hiển thị dưới dạng số nguyên
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        totalBill.setText(formatter.format(totalFee));
    }

    private void updateSubscribedFacilitiesTable() {
        DefaultTableModel subscribedModel = (DefaultTableModel) listFacilitiesSubscribiedTable.getModel();
        subscribedModel.setRowCount(0);

        if (invoiceID.getText().isEmpty()) {
            return;
        }

        int currentInvoiceId = Integer.parseInt(invoiceID.getText());
        List<SubscribedFacility> cartFromDB = subscribedFacilityService.getByInvoiceId(currentInvoiceId);

        // Sử dụng Map để gộp các dịch vụ có cùng mã
        // Key: Service ID (String), Value: Dữ liệu dòng (Object[])
        java.util.Map<String, Object[]> aggregatedRows = new java.util.LinkedHashMap<>();

        for (SubscribedFacility sf : cartFromDB) {
            String serviceId = sf.getServiceId(); // Lấy Service ID làm khóa

            if (aggregatedRows.containsKey(serviceId)) {
                // Nếu đã có, cập nhật số lượng và thành tiền
                Object[] existingRow = aggregatedRows.get(serviceId);
                try {
                    int oldQuantity = Integer.parseInt(existingRow[1].toString());
                    int quantityToAdd = sf.getQuantity();
                    int newQuantity = oldQuantity + quantityToAdd;
                    double unitPrice = Double.parseDouble(existingRow[3].toString());
                    double newLineTotal = newQuantity * unitPrice;
                    existingRow[1] = newQuantity;
                    existingRow[4] = newLineTotal;
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi định dạng số: " + e.getMessage());
                }
            } else {
                // Nếu chưa có, thêm dòng mới vào map
                Object[] newRow = new Object[]{
                    sf.getServiceId(), // Hiển thị Service ID thay vì Service Name
                    sf.getQuantity(),
                    sf.getUnit(),
                    sf.getUnitPrice(),
                    sf.getLineTotal()
                };
                aggregatedRows.put(serviceId, newRow);
            }
        }

        // Thêm các dòng đã được gộp vào bảng
        for (Object[] rowData : aggregatedRows.values()) {
            subscribedModel.addRow(rowData);
        }
    }
    
    private void resetInvoiceFields() {
        invoiceID.setText("");
        totalBill.setText("");
        cashReceived.setText("");
        changeReturned.setText("");
        nameResident.setText("");
        phoneNumber.setText("");
        
        updateSubscribedFacilitiesTable(); 
    }
//--------------------------------------------------------------------------------SERVICE------------------------------------------------------
    public void updateServiceTable() {
        List<CompulsoryService> services = compulsoryServiceService.getAllServices();
        DefaultTableModel model = (DefaultTableModel) listServiceTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        for (CompulsoryService service : services) {
            model.addRow(new Object[]{
                service.getServiceId(),
                service.getServiceName(),
                service.getProviderId(),
                service.getUnit(),
                service.getPrice()
            });
        }
    }
//--------------------------------------------------------------------------------COMPLAINT------------------------------------------------------
    public void updateComplaintsTable() {
        // Lấy model của JTable mà bạn đã tạo
        DefaultTableModel model = (DefaultTableModel) listComplaintTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        // Đặt lại tên các cột cho khớp với yêu cầu
        // Bạn có thể làm việc này một lần trong Design View của NetBeans
        model.setColumnIdentifiers(new Object[]{
            "Complaint ID", "Complaint Title", "Complaint By", "Date", "Status"
        });

        List<Complaint> complaints = complaintService.getAllComplaints();

        // Định dạng ngày tháng
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

        for (Complaint complaint : complaints) {
            String complaintDateStr = (complaint.getComplaintDate() != null) ? sdf.format(complaint.getComplaintDate()) : "N/A";
            model.addRow(new Object[]{
                complaint.getComplaintId(),
                complaint.getTitle(),
                complaint.getApartmentId(), // "Complaint By" là mã căn hộ
                complaintDateStr,
                complaint.getStatus()
            });
        }
    }
//--------------------------------------------------------------------------------AUTHORIZATION------------------------------------------------------
    public void updateAuthorizationTable() {
        DefaultTableModel model = (DefaultTableModel) listAuthorizationTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        List<Account> accountList = authorizationService.getAllAccountsForAuthorization();

        for (Account acc : accountList) {
            model.addRow(new Object[]{
                acc.getUsername(),
                acc.getEmail(),
                acc.getRole() == null ? "N/A" : acc.getRole(),
                acc.getResidentId() == null ? "N/A" : acc.getResidentId()
            });
        }
    }
//--------------------------------------------------------------------------------FACILITY------------------------------------------------------
    public void updateFacilityTable() {
        List<Facility> facilities = facilityService.getAllFacilities();
        DefaultTableModel model = (DefaultTableModel) listFacilityTable.getModel();
        model.setRowCount(0); // Clear old data

        for (Facility facility : facilities) {
            model.addRow(new Object[]{
                facility.getServiceId(),
                facility.getServiceName(),
                facility.getManufacturer(),
                facility.getUnit(),
                facility.getStockQuantity(),
                facility.getPrice()
            });
        }
    }
//--------------------------------------------------------------------------------INVOICE------------------------------------------------------
    public void updateInvoiceTable() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        DefaultTableModel model = (DefaultTableModel) listInvoiceTable.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Invoice invoice : invoices) {
            model.addRow(new Object[]{
                invoice.getInvoiceId(),
                invoice.getResidentId(),
                dateFormat.format(invoice.getCreationDate()),
                invoice.getTotalFee(),
                invoice.getStatus()
            });
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        contentPanel = new javax.swing.JPanel();
        homePanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        residentPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        mainParking = new javax.swing.JPanel();
        parkingToolbar = new javax.swing.JPanel();
        searchParkingSlotField = new javax.swing.JTextField();
        addParkingSlotButton = new javax.swing.JButton();
        updateParkingSlotButton = new javax.swing.JButton();
        deleteParkingSlotButton = new javax.swing.JButton();
        exportParkingSlotsButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        toggleMenuLabel = new javax.swing.JLabel();
        listParkingSlotPanel = new javax.swing.JPanel();
        listParkingSlotScroll = new javax.swing.JScrollPane();
        listParkingSlotTable = new javax.swing.JTable();
        listParkingSlotLabel = new javax.swing.JLabel();
        bookingFacilities = new javax.swing.JPanel();
        slot = new javax.swing.JPanel();
        parkingToolbar1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        bookFacilities = new javax.swing.JButton();
        listParkingSlotPanel1 = new javax.swing.JPanel();
        listParkingSlotScroll1 = new javax.swing.JScrollPane();
        listFacilitiesTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        serviceID = new javax.swing.JTextField();
        serviceName = new javax.swing.JTextField();
        price = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionFacilities = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        invoiceID = new javax.swing.JTextField();
        phoneNumber = new javax.swing.JTextField();
        nameResident = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel28 = new javax.swing.JLabel();
        totalBill = new javax.swing.JTextField();
        cashReceived = new javax.swing.JTextField();
        changeReturned = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        printInvoiceButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listFacilitiesSubscribiedTable = new javax.swing.JTable();
        deleteSubscribedFacilities = new javax.swing.JButton();
        servicePanel = new javax.swing.JPanel();
        serviceToolbar = new javax.swing.JPanel();
        searchServiceField = new javax.swing.JTextField();
        addServiceButton = new javax.swing.JButton();
        updateServiceButton = new javax.swing.JButton();
        deleteServiceButton = new javax.swing.JButton();
        exportServiceButton = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        toggleMenuLabel1 = new javax.swing.JLabel();
        listServicePanel = new javax.swing.JPanel();
        listServiceScroll = new javax.swing.JScrollPane();
        listServiceTable = new javax.swing.JTable();
        listServiceLabel = new javax.swing.JLabel();
        complaintPanel = new javax.swing.JPanel();
        complaintToolbar = new javax.swing.JPanel();
        searchComplaintField = new javax.swing.JTextField();
        addComplaintButton = new javax.swing.JButton();
        updateComplaintButton = new javax.swing.JButton();
        deleteComplaintButton = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        toggleMenuLabel2 = new javax.swing.JLabel();
        detailComplaintButton = new javax.swing.JButton();
        assignComplaintButton = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        listComplaintPanel = new javax.swing.JPanel();
        listComplaintScroll = new javax.swing.JScrollPane();
        listComplaintTable = new javax.swing.JTable();
        listComplaintLabel = new javax.swing.JLabel();
        authorizationPanel = new javax.swing.JPanel();
        authorizationToolbar = new javax.swing.JPanel();
        searchAuthorizationField = new javax.swing.JTextField();
        updateAuthorizationButton = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        toggleMenuLabel3 = new javax.swing.JLabel();
        listAuthorizationPanel = new javax.swing.JPanel();
        listComplaintScroll1 = new javax.swing.JScrollPane();
        listAuthorizationTable = new javax.swing.JTable();
        listComplaintLabel1 = new javax.swing.JLabel();
        facilityPanel = new javax.swing.JPanel();
        facilityToolbar = new javax.swing.JPanel();
        searchFacilityField = new javax.swing.JTextField();
        addFacilityButton = new javax.swing.JButton();
        updateFacilityButton = new javax.swing.JButton();
        deleteFacilityButton = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        toggleMenuLabel4 = new javax.swing.JLabel();
        detailFacilityButton = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        listFacilityPanel = new javax.swing.JPanel();
        listComplaintScroll2 = new javax.swing.JScrollPane();
        listFacilityTable = new javax.swing.JTable();
        listComplaintLabel2 = new javax.swing.JLabel();
        invoicePanel = new javax.swing.JPanel();
        invoiceToolbar = new javax.swing.JPanel();
        searchInvoiceField = new javax.swing.JTextField();
        updateInvoiceButton = new javax.swing.JButton();
        deleteInvoiceButton = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        toggleMenuLabel5 = new javax.swing.JLabel();
        detailInvoiceButton = new javax.swing.JButton();
        jLabel48 = new javax.swing.JLabel();
        listInvoicePanel = new javax.swing.JPanel();
        listComplaintScroll3 = new javax.swing.JScrollPane();
        listInvoiceTable = new javax.swing.JTable();
        listComplaintLabel3 = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        menuPanel = new javax.swing.JPanel();
        bookingButton = new javax.swing.JButton();
        profileButton = new javax.swing.JButton();
        authorizationButton = new javax.swing.JButton();
        serviceFacilityButton = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        residentManagementButton = new javax.swing.JButton();
        complaintsButton = new javax.swing.JButton();
        homeButton = new javax.swing.JButton();
        parkingManagementButton = new javax.swing.JButton();
        serviceSub = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        logOutButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setLayout(new java.awt.BorderLayout());

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setLayout(new java.awt.CardLayout());

        homePanel.setBackground(new java.awt.Color(255, 255, 255));
        homePanel.setForeground(new java.awt.Color(255, 255, 255));
        homePanel.setPreferredSize(new java.awt.Dimension(1020, 800));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/vt.jpg"))); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(1200, 800));

        javax.swing.GroupLayout homePanelLayout = new javax.swing.GroupLayout(homePanel);
        homePanel.setLayout(homePanelLayout);
        homePanelLayout.setHorizontalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanelLayout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1008, Short.MAX_VALUE))
            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        homePanelLayout.setVerticalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanelLayout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        contentPanel.add(homePanel, "home");

        residentPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setText("day la cu dan admin");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout residentPanelLayout = new javax.swing.GroupLayout(residentPanel);
        residentPanel.setLayout(residentPanelLayout);
        residentPanelLayout.setHorizontalGroup(
            residentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(677, Short.MAX_VALUE))
        );
        residentPanelLayout.setVerticalGroup(
            residentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(residentPanelLayout.createSequentialGroup()
                .addGroup(residentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(residentPanelLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(residentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(708, Short.MAX_VALUE))
        );

        contentPanel.add(residentPanel, "resident");

        mainParking.setBackground(new java.awt.Color(245, 245, 245));

        parkingToolbar.setBackground(new java.awt.Color(255, 255, 255));

        searchParkingSlotField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchParkingSlotField.setText("Search...");

        addParkingSlotButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        addParkingSlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/61658_add_green_plus_icon.png"))); // NOI18N
        addParkingSlotButton.setContentAreaFilled(false);
        addParkingSlotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addParkingSlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addParkingSlotButtonActionPerformed(evt);
            }
        });

        updateParkingSlotButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        updateParkingSlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-update-48.png"))); // NOI18N
        updateParkingSlotButton.setContentAreaFilled(false);
        updateParkingSlotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateParkingSlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateParkingSlotButtonActionPerformed(evt);
            }
        });

        deleteParkingSlotButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        deleteParkingSlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delete-48.png"))); // NOI18N
        deleteParkingSlotButton.setContentAreaFilled(false);
        deleteParkingSlotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteParkingSlotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteParkingSlotButtonActionPerformed(evt);
            }
        });

        exportParkingSlotsButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        exportParkingSlotsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-export-pdf-48.png"))); // NOI18N
        exportParkingSlotsButton.setContentAreaFilled(false);
        exportParkingSlotsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("ADD");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("UPDATE");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("DELETE");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("EXPORT");

        toggleMenuLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toggleMenuLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        toggleMenuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout parkingToolbarLayout = new javax.swing.GroupLayout(parkingToolbar);
        parkingToolbar.setLayout(parkingToolbarLayout);
        parkingToolbarLayout.setHorizontalGroup(
            parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parkingToolbarLayout.createSequentialGroup()
                .addComponent(toggleMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addParkingSlotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(updateParkingSlotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(deleteParkingSlotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportParkingSlotsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 179, Short.MAX_VALUE)
                .addComponent(searchParkingSlotField, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        parkingToolbarLayout.setVerticalGroup(
            parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parkingToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportParkingSlotsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateParkingSlotButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addParkingSlotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteParkingSlotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(parkingToolbarLayout.createSequentialGroup()
                .addGroup(parkingToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(parkingToolbarLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(searchParkingSlotField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(parkingToolbarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(toggleMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        listParkingSlotPanel.setBackground(new java.awt.Color(0, 153, 153));
        listParkingSlotPanel.setLayout(new java.awt.BorderLayout());

        listParkingSlotTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"A101", "Staff", "Car", "Unavailable", "092849284"},
                {"A102", "Resident", "Bike", "Unavailable", "76482794723"},
                {"B202", "Staff", "Bike", "Unavailable", "93875927394"},
                {"C101", "Staff", "Car", "Available", "N/A"}
            },
            new String [] {
                "Slot Name", "Slot Type", "Vehicle", "Status", "License Plate"
            }
        ));
        listParkingSlotScroll.setViewportView(listParkingSlotTable);

        listParkingSlotPanel.add(listParkingSlotScroll, java.awt.BorderLayout.CENTER);

        listParkingSlotLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        listParkingSlotLabel.setForeground(new java.awt.Color(255, 255, 255));
        listParkingSlotLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listParkingSlotLabel.setText("\tLIST OF PARKING SLOTS");
        listParkingSlotLabel.setPreferredSize(new java.awt.Dimension(245, 40));
        listParkingSlotPanel.add(listParkingSlotLabel, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout mainParkingLayout = new javax.swing.GroupLayout(mainParking);
        mainParking.setLayout(mainParkingLayout);
        mainParkingLayout.setHorizontalGroup(
            mainParkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parkingToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(listParkingSlotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainParkingLayout.setVerticalGroup(
            mainParkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainParkingLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(parkingToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(listParkingSlotPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );

        contentPanel.add(mainParking, "parking");

        bookingFacilities.setBackground(new java.awt.Color(255, 255, 255));
        bookingFacilities.setLayout(new java.awt.BorderLayout());

        slot.setBackground(new java.awt.Color(245, 245, 245));
        slot.setPreferredSize(new java.awt.Dimension(800, 809));

        parkingToolbar1.setBackground(new java.awt.Color(255, 255, 255));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextField1.setText("Search");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        bookFacilities.setBackground(new java.awt.Color(102, 102, 255));
        bookFacilities.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        bookFacilities.setForeground(new java.awt.Color(255, 255, 102));
        bookFacilities.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-signing-a-document-30.png"))); // NOI18N
        bookFacilities.setText("BOOK");
        bookFacilities.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookFacilitiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout parkingToolbar1Layout = new javax.swing.GroupLayout(parkingToolbar1);
        parkingToolbar1.setLayout(parkingToolbar1Layout);
        parkingToolbar1Layout.setHorizontalGroup(
            parkingToolbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parkingToolbar1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bookFacilities, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        parkingToolbar1Layout.setVerticalGroup(
            parkingToolbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, parkingToolbar1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parkingToolbar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bookFacilities))
                .addGap(15, 15, 15))
        );

        listParkingSlotPanel1.setBackground(new java.awt.Color(255, 255, 255));

        listFacilitiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"s1", "Gym", "Life Fitness", "Hour", "unlimited", "25000"},
                {"s2", "Swimming Pool", "Pentair", "Person", "unlimited", "30000"},
                {"s3", "Laundry", "LG Electronics	", "Kilogram", "500", "25000"},
                {"s4", "Community Room", "Generic Provider	", "Hour", "50", "35000"}
            },
            new String [] {
                "ServiceID", "Service Name", "Manufacturer", "Unit", "Stock Quantity", "Price"
            }
        ));
        listParkingSlotScroll1.setViewportView(listFacilitiesTable);

        javax.swing.GroupLayout listParkingSlotPanel1Layout = new javax.swing.GroupLayout(listParkingSlotPanel1);
        listParkingSlotPanel1.setLayout(listParkingSlotPanel1Layout);
        listParkingSlotPanel1Layout.setHorizontalGroup(
            listParkingSlotPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listParkingSlotScroll1)
        );
        listParkingSlotPanel1Layout.setVerticalGroup(
            listParkingSlotPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listParkingSlotScroll1)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        serviceID.setBackground(new java.awt.Color(240, 240, 240));
        serviceID.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        serviceID.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        serviceID.setFocusable(false);

        serviceName.setBackground(new java.awt.Color(240, 240, 240));
        serviceName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        serviceName.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        serviceName.setFocusable(false);
        serviceName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceNameActionPerformed(evt);
            }
        });

        price.setBackground(new java.awt.Color(240, 240, 240));
        price.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        price.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        price.setFocusable(false);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("ServiceID:");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setText("Service Name:");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setText("Description:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setText("Price:");

        descriptionFacilities.setBackground(new java.awt.Color(240, 240, 240));
        descriptionFacilities.setColumns(20);
        descriptionFacilities.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        descriptionFacilities.setRows(5);
        jScrollPane1.setViewportView(descriptionFacilities);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serviceName, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serviceID, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serviceID, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serviceName, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        jLabel22.setBackground(new java.awt.Color(0, 153, 153));
        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("SERVICE INFORMATION");
        jLabel22.setOpaque(true);

        javax.swing.GroupLayout slotLayout = new javax.swing.GroupLayout(slot);
        slot.setLayout(slotLayout);
        slotLayout.setHorizontalGroup(
            slotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, slotLayout.createSequentialGroup()
                .addGroup(slotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(listParkingSlotPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parkingToolbar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(slotLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        slotLayout.setVerticalGroup(
            slotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(slotLayout.createSequentialGroup()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(slotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parkingToolbar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listParkingSlotPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bookingFacilities.add(slot, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(245, 245, 245));
        jPanel2.setPreferredSize(new java.awt.Dimension(450, 834));

        jLabel23.setBackground(new java.awt.Color(0, 153, 153));
        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("SUBSCRIBED SERVICES");
        jLabel23.setOpaque(true);

        jLabel24.setBackground(new java.awt.Color(0, 153, 153));
        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("INVOICE");
        jLabel24.setOpaque(true);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel25.setText("Phone Number:");

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel26.setText("InvoiceID:");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel27.setText("Name Resident:");

        invoiceID.setBackground(new java.awt.Color(240, 240, 240));
        invoiceID.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        invoiceID.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        invoiceID.setFocusable(false);

        phoneNumber.setBackground(new java.awt.Color(240, 240, 240));
        phoneNumber.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        phoneNumber.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        phoneNumber.setFocusable(false);

        nameResident.setBackground(new java.awt.Color(240, 240, 240));
        nameResident.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        nameResident.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        nameResident.setFocusable(false);

        jSeparator1.setBackground(java.awt.Color.lightGray);

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel28.setText("Change returned:");

        totalBill.setBackground(new java.awt.Color(240, 240, 240));
        totalBill.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        totalBill.setForeground(java.awt.Color.red);
        totalBill.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        totalBill.setFocusable(false);

        cashReceived.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cashReceived.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));

        changeReturned.setBackground(new java.awt.Color(240, 240, 240));
        changeReturned.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        changeReturned.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        changeReturned.setFocusable(false);

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel29.setText("Cash received:");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel30.setForeground(java.awt.Color.red);
        jLabel30.setText("Total bill:");

        printInvoiceButton.setBackground(new java.awt.Color(0, 153, 0));
        printInvoiceButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        printInvoiceButton.setForeground(new java.awt.Color(255, 255, 255));
        printInvoiceButton.setText("PRINT THE INVOICE");
        printInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printInvoiceButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(invoiceID, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(18, 18, 18)
                                .addComponent(phoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(18, 18, 18)
                                .addComponent(nameResident, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel29)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cashReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(39, 39, 39)
                                    .addComponent(totalBill, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(changeReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(printInvoiceButton))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(invoiceID)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameResident, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalBill, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cashReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(printInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        listFacilitiesSubscribiedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Gym", "3", "Hour", "25000", null},
                {"Community Room", "2", "Hour", "35000", null}
            },
            new String [] {
                "ServiceID", "Quantity", "Unit", "Unit Price", "Line Total"
            }
        ));
        jScrollPane2.setViewportView(listFacilitiesSubscribiedTable);

        deleteSubscribedFacilities.setBackground(new java.awt.Color(255, 0, 0));
        deleteSubscribedFacilities.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delete-45.png"))); // NOI18N
        deleteSubscribedFacilities.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSubscribedFacilitiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteSubscribedFacilities, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteSubscribedFacilities, javax.swing.GroupLayout.PREFERRED_SIZE, 46, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bookingFacilities.add(jPanel2, java.awt.BorderLayout.EAST);

        contentPanel.add(bookingFacilities, "booking");

        servicePanel.setBackground(new java.awt.Color(245, 245, 245));

        serviceToolbar.setBackground(new java.awt.Color(255, 255, 255));

        searchServiceField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchServiceField.setText("Search...");

        addServiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        addServiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/61658_add_green_plus_icon.png"))); // NOI18N
        addServiceButton.setContentAreaFilled(false);
        addServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addServiceButtonActionPerformed(evt);
            }
        });

        updateServiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        updateServiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-update-48.png"))); // NOI18N
        updateServiceButton.setContentAreaFilled(false);
        updateServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateServiceButtonActionPerformed(evt);
            }
        });

        deleteServiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        deleteServiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delete-48.png"))); // NOI18N
        deleteServiceButton.setContentAreaFilled(false);
        deleteServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteServiceButtonActionPerformed(evt);
            }
        });

        exportServiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        exportServiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-export-pdf-48.png"))); // NOI18N
        exportServiceButton.setContentAreaFilled(false);
        exportServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("ADD");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("UPDATE");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("DELETE");

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("EXPORT");

        toggleMenuLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toggleMenuLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        toggleMenuLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout serviceToolbarLayout = new javax.swing.GroupLayout(serviceToolbar);
        serviceToolbar.setLayout(serviceToolbarLayout);
        serviceToolbarLayout.setHorizontalGroup(
            serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serviceToolbarLayout.createSequentialGroup()
                .addComponent(toggleMenuLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(updateServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(deleteServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 179, Short.MAX_VALUE)
                .addComponent(searchServiceField, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        serviceToolbarLayout.setVerticalGroup(
            serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serviceToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateServiceButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteServiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(serviceToolbarLayout.createSequentialGroup()
                .addGroup(serviceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(serviceToolbarLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(searchServiceField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(serviceToolbarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(toggleMenuLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        listServicePanel.setBackground(new java.awt.Color(0, 153, 153));
        listServicePanel.setLayout(new java.awt.BorderLayout());

        listServiceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"s1", "Water", "Water Board", "kilogram", "20000"},
                {"s2", "Parking", "Levina", "slot", "200000"},
                {"s3", "Security", "Alex", "person", "200000"},
                {"s4", "Electrical", "Albert", "kilogram", "500000"}
            },
            new String [] {
                "ServiceID", "Service Name", "Provider Name", "Unit", "Price"
            }
        ));
        listServiceScroll.setViewportView(listServiceTable);

        listServicePanel.add(listServiceScroll, java.awt.BorderLayout.CENTER);

        listServiceLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        listServiceLabel.setForeground(new java.awt.Color(255, 255, 255));
        listServiceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listServiceLabel.setText("\tLIST OF SERVICES");
        listServiceLabel.setPreferredSize(new java.awt.Dimension(245, 40));
        listServicePanel.add(listServiceLabel, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout servicePanelLayout = new javax.swing.GroupLayout(servicePanel);
        servicePanel.setLayout(servicePanelLayout);
        servicePanelLayout.setHorizontalGroup(
            servicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(serviceToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(listServicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        servicePanelLayout.setVerticalGroup(
            servicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(serviceToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(listServicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );

        contentPanel.add(servicePanel, "service");

        complaintPanel.setBackground(new java.awt.Color(245, 245, 245));

        complaintToolbar.setBackground(new java.awt.Color(255, 255, 255));

        searchComplaintField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchComplaintField.setText("Search...");

        addComplaintButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        addComplaintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/61658_add_green_plus_icon.png"))); // NOI18N
        addComplaintButton.setContentAreaFilled(false);
        addComplaintButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addComplaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComplaintButtonActionPerformed(evt);
            }
        });

        updateComplaintButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        updateComplaintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-update-48.png"))); // NOI18N
        updateComplaintButton.setContentAreaFilled(false);
        updateComplaintButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateComplaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateComplaintButtonActionPerformed(evt);
            }
        });

        deleteComplaintButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        deleteComplaintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delete-48.png"))); // NOI18N
        deleteComplaintButton.setContentAreaFilled(false);
        deleteComplaintButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteComplaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteComplaintButtonActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("ADD");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("UPDATE");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("DELETE");

        toggleMenuLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toggleMenuLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        toggleMenuLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuLabel2MouseClicked(evt);
            }
        });

        detailComplaintButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        detailComplaintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-detail-48.png"))); // NOI18N
        detailComplaintButton.setContentAreaFilled(false);
        detailComplaintButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        detailComplaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailComplaintButtonActionPerformed(evt);
            }
        });

        assignComplaintButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        assignComplaintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delegate-48 (1).png"))); // NOI18N
        assignComplaintButton.setContentAreaFilled(false);
        assignComplaintButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        assignComplaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignComplaintButtonActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("DETAIL");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("ASSIGN");

        javax.swing.GroupLayout complaintToolbarLayout = new javax.swing.GroupLayout(complaintToolbar);
        complaintToolbar.setLayout(complaintToolbarLayout);
        complaintToolbarLayout.setHorizontalGroup(
            complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(complaintToolbarLayout.createSequentialGroup()
                .addComponent(toggleMenuLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addComplaintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updateComplaintButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deleteComplaintButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(detailComplaintButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(assignComplaintButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                .addComponent(searchComplaintField, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        complaintToolbarLayout.setVerticalGroup(
            complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(complaintToolbarLayout.createSequentialGroup()
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(complaintToolbarLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(searchComplaintField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(complaintToolbarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(toggleMenuLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(complaintToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(complaintToolbarLayout.createSequentialGroup()
                        .addComponent(assignComplaintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(complaintToolbarLayout.createSequentialGroup()
                        .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(updateComplaintButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(addComplaintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(deleteComplaintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(detailComplaintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(complaintToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );

        listComplaintPanel.setBackground(new java.awt.Color(0, 153, 153));
        listComplaintPanel.setLayout(new java.awt.BorderLayout());

        listComplaintTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"A101", "Staff", "Car", "Unavailable", "092849284"},
                {"A102", "Resident", "Bike", "Unavailable", "76482794723"},
                {"B202", "Staff", "Bike", "Unavailable", "93875927394"},
                {"C101", "Staff", "Car", "Available", "N/A"}
            },
            new String [] {
                "ComplaintID", "Complaint Title", "Complaint By", "Date", "Complaint Status"
            }
        ));
        listComplaintScroll.setViewportView(listComplaintTable);

        listComplaintPanel.add(listComplaintScroll, java.awt.BorderLayout.CENTER);

        listComplaintLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        listComplaintLabel.setForeground(new java.awt.Color(255, 255, 255));
        listComplaintLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listComplaintLabel.setText("LIST OF COMPLAINTS");
        listComplaintLabel.setPreferredSize(new java.awt.Dimension(245, 40));
        listComplaintPanel.add(listComplaintLabel, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout complaintPanelLayout = new javax.swing.GroupLayout(complaintPanel);
        complaintPanel.setLayout(complaintPanelLayout);
        complaintPanelLayout.setHorizontalGroup(
            complaintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(complaintToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(listComplaintPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        complaintPanelLayout.setVerticalGroup(
            complaintPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(complaintPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(complaintToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(listComplaintPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );

        contentPanel.add(complaintPanel, "complaint");

        authorizationPanel.setBackground(new java.awt.Color(245, 245, 245));

        authorizationToolbar.setBackground(new java.awt.Color(255, 255, 255));

        searchAuthorizationField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchAuthorizationField.setText("Search...");

        updateAuthorizationButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        updateAuthorizationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-update-48.png"))); // NOI18N
        updateAuthorizationButton.setContentAreaFilled(false);
        updateAuthorizationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateAuthorizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateAuthorizationButtonActionPerformed(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("UPDATE");

        toggleMenuLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toggleMenuLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        toggleMenuLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout authorizationToolbarLayout = new javax.swing.GroupLayout(authorizationToolbar);
        authorizationToolbar.setLayout(authorizationToolbarLayout);
        authorizationToolbarLayout.setHorizontalGroup(
            authorizationToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authorizationToolbarLayout.createSequentialGroup()
                .addComponent(toggleMenuLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(authorizationToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updateAuthorizationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addComponent(searchAuthorizationField, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        authorizationToolbarLayout.setVerticalGroup(
            authorizationToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authorizationToolbarLayout.createSequentialGroup()
                .addGroup(authorizationToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(authorizationToolbarLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(searchAuthorizationField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(authorizationToolbarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(toggleMenuLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(authorizationToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateAuthorizationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        listAuthorizationPanel.setBackground(new java.awt.Color(0, 153, 153));
        listAuthorizationPanel.setLayout(new java.awt.BorderLayout());

        listAuthorizationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"A101", "Staff", "Car", "Unavailable"},
                {"A102", "Resident", "Bike", "Unavailable"},
                {"B202", "Staff", "Bike", "Unavailable"},
                {"C101", "Staff", "Car", "Available"}
            },
            new String [] {
                "Username", "Email", "Role", "ResidentID"
            }
        ));
        listComplaintScroll1.setViewportView(listAuthorizationTable);

        listAuthorizationPanel.add(listComplaintScroll1, java.awt.BorderLayout.CENTER);

        listComplaintLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        listComplaintLabel1.setForeground(new java.awt.Color(255, 255, 255));
        listComplaintLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listComplaintLabel1.setText("LIST OF AUTHORIZATION ");
        listComplaintLabel1.setPreferredSize(new java.awt.Dimension(245, 40));
        listAuthorizationPanel.add(listComplaintLabel1, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout authorizationPanelLayout = new javax.swing.GroupLayout(authorizationPanel);
        authorizationPanel.setLayout(authorizationPanelLayout);
        authorizationPanelLayout.setHorizontalGroup(
            authorizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(authorizationToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(listAuthorizationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        authorizationPanelLayout.setVerticalGroup(
            authorizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authorizationPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(authorizationToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(listAuthorizationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );

        contentPanel.add(authorizationPanel, "authorization");

        facilityPanel.setBackground(new java.awt.Color(245, 245, 245));

        facilityToolbar.setBackground(new java.awt.Color(255, 255, 255));

        searchFacilityField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchFacilityField.setText("Search...");

        addFacilityButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        addFacilityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/61658_add_green_plus_icon.png"))); // NOI18N
        addFacilityButton.setContentAreaFilled(false);
        addFacilityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addFacilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFacilityButtonActionPerformed(evt);
            }
        });

        updateFacilityButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        updateFacilityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-update-48.png"))); // NOI18N
        updateFacilityButton.setContentAreaFilled(false);
        updateFacilityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateFacilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateFacilityButtonActionPerformed(evt);
            }
        });

        deleteFacilityButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        deleteFacilityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delete-48.png"))); // NOI18N
        deleteFacilityButton.setContentAreaFilled(false);
        deleteFacilityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteFacilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFacilityButtonActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("ADD");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("UPDATE");

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("DELETE");

        toggleMenuLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toggleMenuLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        toggleMenuLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuLabel4MouseClicked(evt);
            }
        });

        detailFacilityButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        detailFacilityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-detail-48.png"))); // NOI18N
        detailFacilityButton.setContentAreaFilled(false);
        detailFacilityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        detailFacilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailFacilityButtonActionPerformed(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("DETAIL");

        javax.swing.GroupLayout facilityToolbarLayout = new javax.swing.GroupLayout(facilityToolbar);
        facilityToolbar.setLayout(facilityToolbarLayout);
        facilityToolbarLayout.setHorizontalGroup(
            facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(facilityToolbarLayout.createSequentialGroup()
                .addComponent(toggleMenuLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addFacilityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updateFacilityButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deleteFacilityButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(detailFacilityButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addComponent(searchFacilityField, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        facilityToolbarLayout.setVerticalGroup(
            facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(facilityToolbarLayout.createSequentialGroup()
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(facilityToolbarLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(searchFacilityField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(facilityToolbarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(toggleMenuLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(facilityToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(updateFacilityButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addFacilityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteFacilityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(detailFacilityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(facilityToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        listFacilityPanel.setBackground(new java.awt.Color(0, 153, 153));
        listFacilityPanel.setLayout(new java.awt.BorderLayout());

        listFacilityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"A101", "Staff", "Car", "Unavailable", null, "092849284"},
                {"A102", "Resident", "Bike", "Unavailable", null, "76482794723"},
                {"B202", "Staff", "Bike", "Unavailable", null, "93875927394"},
                {"C101", "Staff", "Car", "Available", null, "N/A"}
            },
            new String [] {
                "FacilityID", "Facility Name", "Manufacturer", "Unit", "Stock Quantity", "Price"
            }
        ));
        listComplaintScroll2.setViewportView(listFacilityTable);

        listFacilityPanel.add(listComplaintScroll2, java.awt.BorderLayout.CENTER);

        listComplaintLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        listComplaintLabel2.setForeground(new java.awt.Color(255, 255, 255));
        listComplaintLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listComplaintLabel2.setText("LIST OF FACILITIES");
        listComplaintLabel2.setPreferredSize(new java.awt.Dimension(245, 40));
        listFacilityPanel.add(listComplaintLabel2, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout facilityPanelLayout = new javax.swing.GroupLayout(facilityPanel);
        facilityPanel.setLayout(facilityPanelLayout);
        facilityPanelLayout.setHorizontalGroup(
            facilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(facilityToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(listFacilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        facilityPanelLayout.setVerticalGroup(
            facilityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(facilityPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(facilityToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(listFacilityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );

        contentPanel.add(facilityPanel, "facility");

        invoicePanel.setBackground(new java.awt.Color(245, 245, 245));

        invoiceToolbar.setBackground(new java.awt.Color(255, 255, 255));

        searchInvoiceField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchInvoiceField.setText("Search...");

        updateInvoiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        updateInvoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-update-48.png"))); // NOI18N
        updateInvoiceButton.setContentAreaFilled(false);
        updateInvoiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateInvoiceButtonActionPerformed(evt);
            }
        });

        deleteInvoiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        deleteInvoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-delete-48.png"))); // NOI18N
        deleteInvoiceButton.setContentAreaFilled(false);
        deleteInvoiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteInvoiceButtonActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("UPDATE");

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("DELETE");

        toggleMenuLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        toggleMenuLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-menu-80.png"))); // NOI18N
        toggleMenuLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuLabel5MouseClicked(evt);
            }
        });

        detailInvoiceButton.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        detailInvoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-detail-48.png"))); // NOI18N
        detailInvoiceButton.setContentAreaFilled(false);
        detailInvoiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        detailInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailInvoiceButtonActionPerformed(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("DETAIL");

        javax.swing.GroupLayout invoiceToolbarLayout = new javax.swing.GroupLayout(invoiceToolbar);
        invoiceToolbar.setLayout(invoiceToolbarLayout);
        invoiceToolbarLayout.setHorizontalGroup(
            invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invoiceToolbarLayout.createSequentialGroup()
                .addComponent(toggleMenuLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(invoiceToolbarLayout.createSequentialGroup()
                        .addComponent(updateInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(deleteInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(invoiceToolbarLayout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel47)))
                .addGap(18, 18, 18)
                .addGroup(invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(detailInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
                .addComponent(searchInvoiceField, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        invoiceToolbarLayout.setVerticalGroup(
            invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invoiceToolbarLayout.createSequentialGroup()
                .addGroup(invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(invoiceToolbarLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(searchInvoiceField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(invoiceToolbarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(toggleMenuLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(invoiceToolbarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(updateInvoiceButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(detailInvoiceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(invoiceToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        listInvoicePanel.setBackground(new java.awt.Color(0, 153, 153));
        listInvoicePanel.setLayout(new java.awt.BorderLayout());

        listInvoiceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"A101", null, "Staff", "Car", "Unavailable"},
                {"A102", null, "Resident", "Bike", "Unavailable"},
                {"B202", null, "Staff", "Bike", "Unavailable"},
                {"C101", null, "Staff", "Car", "Available"}
            },
            new String [] {
                "InvoiceID", "ResidentID", "Created Date", "Total Fee", "Status"
            }
        ));
        listComplaintScroll3.setViewportView(listInvoiceTable);

        listInvoicePanel.add(listComplaintScroll3, java.awt.BorderLayout.CENTER);

        listComplaintLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        listComplaintLabel3.setForeground(new java.awt.Color(255, 255, 255));
        listComplaintLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        listComplaintLabel3.setText("LIST OF INVOICE");
        listComplaintLabel3.setPreferredSize(new java.awt.Dimension(245, 40));
        listInvoicePanel.add(listComplaintLabel3, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout invoicePanelLayout = new javax.swing.GroupLayout(invoicePanel);
        invoicePanel.setLayout(invoicePanelLayout);
        invoicePanelLayout.setHorizontalGroup(
            invoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(invoiceToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(listInvoicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        invoicePanelLayout.setVerticalGroup(
            invoicePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invoicePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(invoiceToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(listInvoicePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE))
        );

        contentPanel.add(invoicePanel, "invoice");

        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        scrollPane.setPreferredSize(new java.awt.Dimension(230, 551));

        menuPanel.setBackground(new java.awt.Color(255, 255, 255));

        bookingButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        bookingButton.setText("Booking");
        bookingButton.setContentAreaFilled(false);
        bookingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookingButtonActionPerformed(evt);
            }
        });

        profileButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        profileButton.setText("Invoice");
        profileButton.setContentAreaFilled(false);
        profileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileButtonActionPerformed(evt);
            }
        });

        authorizationButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        authorizationButton.setText("Authorization");
        authorizationButton.setContentAreaFilled(false);
        authorizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authorizationButtonActionPerformed(evt);
            }
        });

        serviceFacilityButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        serviceFacilityButton.setText("Services & Facility");
        serviceFacilityButton.setContentAreaFilled(false);
        serviceFacilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceFacilityButtonActionPerformed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-manager-80.png"))); // NOI18N
        jLabel18.setText("Admin");

        residentManagementButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        residentManagementButton.setText("Resident Management");
        residentManagementButton.setContentAreaFilled(false);
        residentManagementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                residentManagementButtonActionPerformed(evt);
            }
        });

        complaintsButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        complaintsButton.setText("Complaints");
        complaintsButton.setContentAreaFilled(false);
        complaintsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                complaintsButtonActionPerformed(evt);
            }
        });

        homeButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        homeButton.setText("Home");
        homeButton.setContentAreaFilled(false);
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        parkingManagementButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        parkingManagementButton.setText("Parking Management");
        parkingManagementButton.setContentAreaFilled(false);
        parkingManagementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parkingManagementButtonActionPerformed(evt);
            }
        });

        serviceSub.setBackground(new java.awt.Color(210, 210, 210));
        serviceSub.setLayout(new javax.swing.BoxLayout(serviceSub, javax.swing.BoxLayout.Y_AXIS));

        jButton9.setFont(new java.awt.Font("Sans Serif Collection", 1, 15)); // NOI18N
        jButton9.setText("services");
        jButton9.setContentAreaFilled(false);
        jButton9.setMaximumSize(new java.awt.Dimension(242, 55));
        jButton9.setPreferredSize(new java.awt.Dimension(242, 55));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        serviceSub.add(jButton9);

        jButton10.setFont(new java.awt.Font("Sans Serif Collection", 1, 15)); // NOI18N
        jButton10.setText("facility");
        jButton10.setContentAreaFilled(false);
        jButton10.setMaximumSize(new java.awt.Dimension(242, 55));
        jButton10.setPreferredSize(new java.awt.Dimension(242, 55));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        serviceSub.add(jButton10);

        logOutButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        logOutButton.setText("Logout");
        logOutButton.setContentAreaFilled(false);
        logOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });

        jSeparator2.setBackground(java.awt.Color.lightGray);

        jSeparator3.setBackground(java.awt.Color.lightGray);

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(complaintsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(parkingManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(profileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(authorizationButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bookingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(serviceSub, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(serviceFacilityButton, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(residentManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(homeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(logOutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator3))
                .addContainerGap())
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(residentManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parkingManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(complaintsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serviceFacilityButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serviceSub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bookingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authorizationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(logOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        scrollPane.setViewportView(menuPanel);

        mainPanel.add(scrollPane, java.awt.BorderLayout.WEST);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        // TODO add your handling code here:
        s.toggleMenu();
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        // TODO add your handling code here:
        s.toggleMenu();
    }//GEN-LAST:event_jLabel13MouseClicked

    private void addParkingSlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addParkingSlotButtonActionPerformed
        // TODO add your handling code here:
        addParkingSlotButton.setBackground(Color.LIGHT_GRAY); // Set màu nền cho JButton
        addParkingSlotButton.setContentAreaFilled(true);
        AddParkingForm a = new AddParkingForm(this, true, parkingService); // 'this' là Frame cha
        a.setVisible(true); // Code sẽ dừng ở đây cho đến khi dialog đóng
        addParkingSlotButton.setContentAreaFilled(false);
        this.updateTableParking();
    }//GEN-LAST:event_addParkingSlotButtonActionPerformed

    private void toggleMenuLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabelMouseClicked
        // TODO add your handling code here:
        s.toggleMenu();
    }//GEN-LAST:event_toggleMenuLabelMouseClicked

    private void deleteParkingSlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteParkingSlotButtonActionPerformed
        // TODO add your handling code here:
        deleteParkingSlotButton.setContentAreaFilled(true); // Tạo hiệu ứng khi click vào nút
        deleteParkingSlotButton.setBackground(Color.LIGHT_GRAY); // Tạo màu nền khi click vào
    
        int[] selectedRows = listParkingSlotTable.getSelectedRows();
        if (selectedRows.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete the selected slot(s)?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleteSuccess = false;

                // Phải xóa từ dòng dưới lên (tránh lỗi index)
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int row = selectedRows[i];
                    Object slotNameObj = listParkingSlotTable.getValueAt(row, 0);
                    if (slotNameObj != null) {
                        String slotName = slotNameObj.toString();
                        int result = parkingService.deleteParkingSlot(slotName);
                        if (result > 0) {
                            deleteSuccess = true;
                        }
                    }
                }

                // Sau khi xóa, cập nhật lại table
                updateTableParking();
                

                if (deleteSuccess) {
                    JOptionPane.showMessageDialog(null, "Delete successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete the selected slot(s)!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select at least one row to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Quay lại trạng thái ban đầu của button
        deleteParkingSlotButton.setContentAreaFilled(false);
    }//GEN-LAST:event_deleteParkingSlotButtonActionPerformed

    private void updateParkingSlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateParkingSlotButtonActionPerformed
        // TODO add your handling code here:
        
        updateParkingSlotButton.setBackground(Color.LIGHT_GRAY);
        updateParkingSlotButton.setContentAreaFilled(true);

        int selectedRow = listParkingSlotTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select one row to update!", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (listParkingSlotTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "You can only select one row to update!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Tạo đối tượng ParkingSlot từ dữ liệu trên JTable
            String slotName = listParkingSlotTable.getValueAt(selectedRow, 0).toString();
            String slotType = listParkingSlotTable.getValueAt(selectedRow, 1).toString();
            String vehicle = listParkingSlotTable.getValueAt(selectedRow, 2).toString();
            String status = listParkingSlotTable.getValueAt(selectedRow, 3).toString();
            String licensePlate = listParkingSlotTable.getValueAt(selectedRow, 4).toString();

            ParkingSlot selectedSlot = new ParkingSlot(slotName, slotType, vehicle, status, licensePlate);

            // Truyền cả đối tượng này cho form Update
            UpdateParkingForm updateForm = new UpdateParkingForm(this, true, selectedSlot, parkingService);
            updateForm.setVisible(true); // Hiển thị form update

            // Sau khi form update đóng, cập nhật lại bảng
            this.updateTableParking();
        }

        updateParkingSlotButton.setContentAreaFilled(false); // Trả lại trạng thái ban đầu cho nút
    }//GEN-LAST:event_updateParkingSlotButtonActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void bookFacilitiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookFacilitiesActionPerformed
        // TODO add your handling code here:
        int selectedRow = listFacilitiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dịch vụ từ bảng danh sách để đặt.", "Chưa chọn dịch vụ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String serviceId = listFacilitiesTable.getValueAt(selectedRow, 0).toString();
        Facility selectedFacility = facilityService.getAllFacilities().stream()
                .filter(f -> f.getServiceId().equals(serviceId))
                .findFirst().orElse(null);

        if (selectedFacility == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy dịch vụ đã chọn.", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int currentInvoiceId = -1;
        if (!invoiceID.getText().isEmpty()) {
            currentInvoiceId = Integer.parseInt(invoiceID.getText());
        }
    
        BookFacilitiesForm timeDialog = new BookFacilitiesForm(this, true, selectedFacility, facilityService, currentInvoiceId);
        timeDialog.setVisible(true);

        if (timeDialog.isConfirmed()) {
            List<String> selectedSlots = timeDialog.getSelectedTimeSlots();
            if (selectedSlots.isEmpty()) {
                return;
            }

            // Tạo hóa đơn mới nếu chưa có
            if (invoiceID.getText().isEmpty()) {
                // TẢI VÀ HIỂN THỊ THÔNG TIN CƯ DÂN KHI TẠO HÓA ĐƠN MỚI
                if (this.currentResidentId != null) {
                    Resident resident = residentService.getResidentById(this.currentResidentId);
                    if (resident != null) {
                        nameResident.setText(resident.getFullName());
                        phoneNumber.setText(resident.getPhoneNumber());
                    } else {
                        nameResident.setText("Resident not found");
                        phoneNumber.setText("");
                        JOptionPane.showMessageDialog(this, "Could not find resident with ID: " + this.currentResidentId, "Data Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
                // Tạo hóa đơn và gán mã cư dân
                int newId = invoiceService.createInvoice(this.currentResidentId, 0); 
                if (newId != -1) {
                    invoiceID.setText(String.valueOf(newId));
                    currentInvoiceId = newId;
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không thể tạo hóa đơn mới.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                currentInvoiceId = Integer.parseInt(invoiceID.getText());
            }

            for (String slot : selectedSlots) {
                SubscribedFacility newItem = new SubscribedFacility();
                newItem.setInvoiceId(currentInvoiceId);
                newItem.setServiceId(selectedFacility.getServiceId());
                newItem.setUnitPrice(selectedFacility.getPrice());
                newItem.setKhungGio(slot);
                
                subscribedFacilityService.addOrUpdateFacility(newItem);
            }

            updateSubscribedFacilitiesTable();
            handleInvoiceUpdate();
            JOptionPane.showMessageDialog(this, "Đã đăng ký thành công " + selectedSlots.size() + " khung giờ.");
        }
    }//GEN-LAST:event_bookFacilitiesActionPerformed

    private void serviceNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serviceNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_serviceNameActionPerformed

    private void deleteSubscribedFacilitiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSubscribedFacilitiesActionPerformed
        // TODO add your handling code here:
        int selectedRow = listFacilitiesSubscribiedTable.getSelectedRow();
        if (selectedRow == -1 || invoiceID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ trong hóa đơn để xóa.", "Chưa chọn dịch vụ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa dịch vụ đã chọn này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int currentInvoiceId = Integer.parseInt(invoiceID.getText());
            List<SubscribedFacility> currentCart = subscribedFacilityService.getByInvoiceId(currentInvoiceId);
            SubscribedFacility itemToRemove = currentCart.get(selectedRow);

            boolean success = subscribedFacilityService.deleteFacilityFromInvoice(itemToRemove.getInvoiceId(), itemToRemove.getServiceId());

            if (success) {
                updateSubscribedFacilitiesTable();
                handleInvoiceUpdate();
                JOptionPane.showMessageDialog(this, "Đã xóa dịch vụ thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Xóa dịch vụ thất bại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteSubscribedFacilitiesActionPerformed

    private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutButtonActionPerformed
        // TODO add your handling code here:
        //Tao hieu ung khi click vao nut
        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to log out of the application?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Login l = new Login();
            l.setVisible(true);
            l.setLocationRelativeTo(null);
            this.dispose();
        }
    }//GEN-LAST:event_logOutButtonActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "facility");
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "service");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void parkingManagementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parkingManagementButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "parking");
    }//GEN-LAST:event_parkingManagementButtonActionPerformed

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "home");
    }//GEN-LAST:event_homeButtonActionPerformed

    private void residentManagementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_residentManagementButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "resident");
    }//GEN-LAST:event_residentManagementButtonActionPerformed

    private void serviceFacilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serviceFacilityButtonActionPerformed
        // TODO add your handling code here:
        serviceSub.setVisible(!serviceSub.isVisible());
        if(serviceSub.isVisible()){
            menuPanel.setPreferredSize(new Dimension(menuPanel.getWidth() , 660));
            menuPanel.setSize(menuPanel.getPreferredSize());
            menuPanel.revalidate();
            menuPanel.repaint();
        }
        else{
            menuPanel.setPreferredSize(new Dimension(menuPanel.getWidth(), 545));
            menuPanel.setSize(menuPanel.getPreferredSize());
            menuPanel.revalidate();
            menuPanel.repaint();
        }
    }//GEN-LAST:event_serviceFacilityButtonActionPerformed

    private void authorizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authorizationButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "authorization");
    }//GEN-LAST:event_authorizationButtonActionPerformed

    private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServiceButtonActionPerformed
        // TODO add your handling code here:
        addServiceButton.setBackground(Color.LIGHT_GRAY);
        addServiceButton.setContentAreaFilled(true);
        
        // Mở form thêm mới và truyền vào đối tượng service đã được khởi tạo
        AddCompulsoryServiceForm addForm = new AddCompulsoryServiceForm(this, true, this.compulsoryServiceService);
        addForm.setVisible(true); // Chờ cho đến khi dialog được đóng

        addServiceButton.setContentAreaFilled(false); // Trả lại trạng thái cho nút
        updateServiceTable(); // Cập nhật lại bảng sau khi thêm
        
    }//GEN-LAST:event_addServiceButtonActionPerformed

    private void updateServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateServiceButtonActionPerformed
        // TODO add your handling code here:
        updateServiceButton.setBackground(Color.LIGHT_GRAY);
        updateServiceButton.setContentAreaFilled(true);

        int selectedRow = listServiceTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        } else if (listServiceTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one service to update.", "Multiple Selections", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                // Lấy dữ liệu từ dòng đã chọn trong bảng
                String serviceId = listServiceTable.getValueAt(selectedRow, 0).toString();
                String serviceName = listServiceTable.getValueAt(selectedRow, 1).toString();
                String providerId = listServiceTable.getValueAt(selectedRow, 2).toString();
                String unit = listServiceTable.getValueAt(selectedRow, 3).toString();
                BigDecimal price = new BigDecimal(listServiceTable.getValueAt(selectedRow, 4).toString());

                // Tạo đối tượng CompulsoryService từ dữ liệu đã lấy
                CompulsoryService serviceToUpdate = new CompulsoryService(serviceId, serviceName, providerId, unit, price);

                // Mở form cập nhật và truyền đối tượng service cùng service instance
                UpdateCompulsoryServiceForm updateForm = new UpdateCompulsoryServiceForm(this, true, serviceToUpdate, this.compulsoryServiceService);
                updateForm.setVisible(true);

                // Cập nhật lại bảng sau khi dialog đóng
                updateServiceTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error retrieving service data: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        updateServiceButton.setContentAreaFilled(false); // Trả lại trạng thái cho nút
    }//GEN-LAST:event_updateServiceButtonActionPerformed

    private void deleteServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteServiceButtonActionPerformed
        // TODO add your handling code here:
        deleteServiceButton.setBackground(Color.LIGHT_GRAY);
        deleteServiceButton.setContentAreaFilled(true);

        int[] selectedRows = listServiceTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one service to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected service(s)?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean allSucceeded = true;
                // Lặp ngược để tránh lỗi chỉ số khi xóa nhiều dòng
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int modelRow = listServiceTable.convertRowIndexToModel(selectedRows[i]);
                    String serviceId = listServiceTable.getModel().getValueAt(modelRow, 0).toString();
                    
                    if (!compulsoryServiceService.deleteService(serviceId)) {
                        allSucceeded = false;
                    }
                }
                
                updateServiceTable(); // Cập nhật lại bảng
                
                if (allSucceeded) {
                    JOptionPane.showMessageDialog(this, "Service(s) deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete all selected services. Please check logs for details.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        deleteServiceButton.setContentAreaFilled(false); // Trả lại trạng thái cho nút
    }//GEN-LAST:event_deleteServiceButtonActionPerformed

    private void toggleMenuLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabel1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMenuLabel1MouseClicked

    private void printInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printInvoiceButtonActionPerformed
        // TODO add your handling code here:
        // 1. Kiểm tra xem có hóa đơn để xử lý không
        if (invoiceID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để xử lý.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // 2. Lấy và xác thực dữ liệu từ các trường trên form
        int invoiceId;
        double totalFee, cashReceivedValue = 0, changeReturnedValue = 0;
        String status;
    
        try {
            invoiceId = Integer.parseInt(invoiceID.getText());
            totalFee = Double.parseDouble(totalBill.getText().replace(" ", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng số trên hóa đơn.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // 3. Xác định trạng thái và giá trị thanh toán
        String cashReceivedText = cashReceived.getText().trim();
        if (cashReceivedText.isEmpty()) {
            status = "Pending"; // Nếu không nhập tiền mặt, trạng thái là "Chờ thanh toán"
        } else {
            try {
                cashReceivedValue = Double.parseDouble(cashReceivedText.replace(" ", ""));
                changeReturnedValue = Double.parseDouble(changeReturned.getText().replace(" ", ""));
                status = "Completed"; // Nếu có nhập tiền mặt, trạng thái là "Hoàn thành"
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số tiền nhận không hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
        // 4. Cập nhật thông tin thanh toán và trạng thái vào CSDL
        invoiceService.finalizePayment(invoiceId, totalFee, cashReceivedValue, changeReturnedValue, status);
    
        // 5. Mở hộp thoại để người dùng chọn nơi lưu file PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu hóa đơn PDF");
        String suggestedFileName = "Invoice_" + invoiceId + ".pdf";
        fileChooser.setSelectedFile(new java.io.File(suggestedFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            // 6. Tạo và ghi nội dung vào file PDF (Giữ nguyên nội dung PDF)
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

                Paragraph title = new Paragraph("SERVICE INVOICE", titleFont);
                title.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" "));

                document.add(new Paragraph("Invoice ID: " + invoiceID.getText(), normalFont));
                document.add(new Paragraph("Date Created: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()), normalFont));
                document.add(new Paragraph("Resident Name: " + nameResident.getText(), normalFont));
                document.add(new Paragraph("Phone Number: " + phoneNumber.getText(), normalFont));
                document.add(new Paragraph("-------------------------------------------------------------------"));
                document.add(new Paragraph(" "));

                Map<String, String> serviceNameMap = facilityService.getAllFacilities().stream()
                    .collect(Collectors.toMap(Facility::getServiceId, Facility::getServiceName));

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1.5f, 3f, 1.5f, 1.5f, 2f, 2f});

                String[] headers = {"Service ID", "Service Name", "Quantity", "Unit", "Unit Price", "Line Total"};
                for (String header : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(header, boldFont));
                    headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    table.addCell(headerCell);
                }

                DefaultTableModel model = (DefaultTableModel) listFacilitiesSubscribiedTable.getModel();
                for (int row = 0; row < model.getRowCount(); row++) {
                    String serviceId = model.getValueAt(row, 0).toString();
                    String serviceNameValue = serviceNameMap.getOrDefault(serviceId, "N/A");
                    
                    table.addCell(new Phrase(serviceId, normalFont));
                    table.addCell(new Phrase(serviceNameValue, normalFont));
                    
                    for (int col = 1; col < model.getColumnCount(); col++) {
                        table.addCell(new Phrase(model.getValueAt(row, col).toString(), normalFont));
                    }
                }
                document.add(table);
                document.add(new Paragraph("-------------------------------------------------------------------"));

                document.add(new Paragraph("Total Fee: " + totalBill.getText() + " VND", boldFont));
                document.add(new Paragraph("Cash Received: " + cashReceived.getText() + " VND", normalFont));
                document.add(new Paragraph("Change Returned: " + changeReturned.getText() + " VND", normalFont));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                try {
                    java.net.URL qrUrl = getClass().getResource("/icon/QR.jpg");
                    if (qrUrl != null) {
                        com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrUrl);
                        qrImage.scaleToFit(240, 240);
                        qrImage.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
                        document.add(qrImage);
                    }
                } catch (Exception ex) {
                    System.err.println("Could not find or add QR image.");
                    ex.printStackTrace();
                }

                document.close();
                
                JOptionPane.showMessageDialog(this, "PDF invoice exported successfully!\nSaved at: " + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error creating PDF file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        // 7. Làm mới giao diện sau khi hoàn tất
        resetInvoiceFields();
        updateInvoiceTable();
    }//GEN-LAST:event_printInvoiceButtonActionPerformed

    private void addComplaintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComplaintButtonActionPerformed
        // TODO add your handling code here:
        addComplaintButton.setBackground(Color.LIGHT_GRAY); // Set màu nền cho JButton
        addComplaintButton.setContentAreaFilled(true);
        AddComplaintForm a = new AddComplaintForm(this, true, complaintService); // 'this' là Frame cha
        a.setVisible(true); // Code sẽ dừng ở đây cho đến khi dialog đóng
        addComplaintButton.setContentAreaFilled(false);
        this.updateComplaintsTable();
    }//GEN-LAST:event_addComplaintButtonActionPerformed

    private void updateComplaintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateComplaintButtonActionPerformed
        // TODO add your handling code here:
        updateComplaintButton.setBackground(Color.LIGHT_GRAY);
        updateComplaintButton.setContentAreaFilled(true);

        int selectedRow = listComplaintTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select one complaint to update.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (listComplaintTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one complaint to update.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String complaintId = listComplaintTable.getValueAt(selectedRow, 0).toString();

            // Tìm đối tượng Complaint đầy đủ từ service
            Complaint complaintToUpdate = complaintService.getAllComplaints().stream()
                    .filter(c -> c.getComplaintId().equals(complaintId))
                    .findFirst()
                    .orElse(null);

            if (complaintToUpdate != null) {
                // Truyền đối tượng complaint và service vào form cập nhật
                UpdateComplaintForm updateForm = new UpdateComplaintForm(this, true, complaintToUpdate, complaintService);
                updateForm.setVisible(true);

                // Sau khi form đóng, làm mới lại bảng
                updateComplaintsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Could not find the selected complaint details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        updateComplaintButton.setContentAreaFilled(false); // Reset lại giao diện nút
        
    }//GEN-LAST:event_updateComplaintButtonActionPerformed

    private void deleteComplaintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteComplaintButtonActionPerformed
        // TODO add your handling code here:
        deleteComplaintButton.setContentAreaFilled(true);
        deleteComplaintButton.setBackground(Color.LIGHT_GRAY);

        int[] selectedRows = listComplaintTable.getSelectedRows();
        if (selectedRows.length > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected complaint(s)?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean allDeleted = true;
                // Lặp ngược để tránh lỗi chỉ số khi xóa nhiều dòng
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int row = selectedRows[i];
                    // Lấy ID của phản ánh từ cột đầu tiên (index 0)
                    String complaintId = listComplaintTable.getValueAt(row, 0).toString();
                    
                    if (!complaintService.deleteComplaint(complaintId)) {
                        allDeleted = false; // Đánh dấu nếu có bất kỳ lỗi xóa nào
                    }
                }

                // Cập nhật lại bảng để hiển thị thay đổi
                updateComplaintsTable();

                if (allDeleted) {
                    JOptionPane.showMessageDialog(this, "Complaint(s) deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete all selected complaints. Please check the logs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one complaint to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
        
        deleteComplaintButton.setContentAreaFilled(false); // Reset lại giao diện nút
        
    }//GEN-LAST:event_deleteComplaintButtonActionPerformed

    private void toggleMenuLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabel2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMenuLabel2MouseClicked

    private void complaintsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_complaintsButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "complaint");
    }//GEN-LAST:event_complaintsButtonActionPerformed

    private void detailComplaintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailComplaintButtonActionPerformed
        // TODO add your handling code here:
        detailComplaintButton.setBackground(Color.LIGHT_GRAY);
        detailComplaintButton.setContentAreaFilled(true);

        int selectedRow = listComplaintTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select one complaint to view detail.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (listComplaintTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one complaint to view detail.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String complaintId = listComplaintTable.getValueAt(selectedRow, 0).toString();

            // Tìm đối tượng Complaint đầy đủ từ service
            Complaint complaintToUpdate = complaintService.getAllComplaints().stream()
                    .filter(c -> c.getComplaintId().equals(complaintId))
                    .findFirst()
                    .orElse(null);

            if (complaintToUpdate != null) {
                // Truyền đối tượng complaint vào form cập nhật
                DetailComplaintForm detailForm = new DetailComplaintForm(this, true, complaintToUpdate);
                detailForm.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Could not find the selected complaint details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        detailComplaintButton.setContentAreaFilled(false); // Reset lại giao diện nút
    }//GEN-LAST:event_detailComplaintButtonActionPerformed

    private void assignComplaintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assignComplaintButtonActionPerformed
        // TODO add your handling code here:
        assignComplaintButton.setBackground(java.awt.Color.LIGHT_GRAY);
        assignComplaintButton.setContentAreaFilled(true);

        int selectedRow = listComplaintTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select one complaint to assign.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (listComplaintTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one complaint.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String complaintId = listComplaintTable.getValueAt(selectedRow, 0).toString();
            
            Complaint selectedComplaint = complaintService.getAllComplaints().stream()
                .filter(c -> c.getComplaintId().equals(complaintId))
                .findFirst()
                .orElse(null);

            if (selectedComplaint != null) {
                String currentStatus = selectedComplaint.getStatus();
                if ("In Progress".equalsIgnoreCase(currentStatus) || "Resolved".equalsIgnoreCase(currentStatus) || "Closed".equalsIgnoreCase(currentStatus)) {
                    JOptionPane.showMessageDialog(this, "This complaint has already been assigned or completed.", "Cannot Assign", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Mở form Assign và truyền các service đã được đổi tên
                    AssignComplaintForm assignForm = new AssignComplaintForm(this, true, selectedComplaint, staffService, assignmentService);
                    assignForm.setVisible(true);

                    // Cập nhật lại bảng chính sau khi form đóng
                    updateComplaintsTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Could not find complaint details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        assignComplaintButton.setContentAreaFilled(false);
    }//GEN-LAST:event_assignComplaintButtonActionPerformed

    private void toggleMenuLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabel3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMenuLabel3MouseClicked

    private void updateAuthorizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAuthorizationButtonActionPerformed
        // TODO add your handling code here:
        updateAuthorizationButton.setContentAreaFilled(true);
        updateAuthorizationButton.setBackground(java.awt.Color.LIGHT_GRAY);

        int selectedRow = listAuthorizationTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        } else {
            // Lấy username từ dòng đã chọn
            String username = listAuthorizationTable.getValueAt(selectedRow, 0).toString();
            
            // Tìm đối tượng Account đầy đủ từ service để đảm bảo dữ liệu chính xác
            Account selectedAccount = authorizationService.getAllAccountsForAuthorization().stream()
                .filter(acc -> acc.getUsername().equals(username))
                .findFirst()
                .orElse(null);

            if (selectedAccount != null) {
                // Mở form cập nhật và truyền đối tượng Account
                UpdateAuthorization updateForm = new UpdateAuthorization(this, true, selectedAccount, authorizationService);
                updateForm.setVisible(true);

                // Sau khi form đóng, làm mới lại bảng
                updateAuthorizationTable();
            } else {
                JOptionPane.showMessageDialog(this, "Could not find account details for: " + username, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        updateAuthorizationButton.setContentAreaFilled(false);
    }//GEN-LAST:event_updateAuthorizationButtonActionPerformed

    private void bookingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookingButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "booking");
    }//GEN-LAST:event_bookingButtonActionPerformed

    private void updateFacilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateFacilityButtonActionPerformed
        // TODO add your handling code here:
        updateFacilityButton.setBackground(Color.LIGHT_GRAY);
        updateFacilityButton.setContentAreaFilled(true);

        int selectedRow = listFacilityTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a facility to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        } else if (listFacilityTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one facility to update.", "Multiple Selections", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                String facilityId = listFacilityTable.getValueAt(selectedRow, 0).toString();
                Facility facilityToUpdate = facilityService.getAllFacilities().stream()
                        .filter(f -> f.getServiceId().equals(facilityId))
                        .findFirst()
                        .orElse(null);

                if (facilityToUpdate != null) {
                    UpdateFacilityForm updateForm = new UpdateFacilityForm(this, true, facilityToUpdate, this.facilityService);
                    updateForm.setVisible(true);
                    updateFacilityTable();
                } else {
                     JOptionPane.showMessageDialog(this, "Could not find details for the selected facility.", "Data Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error retrieving facility data: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        updateFacilityButton.setContentAreaFilled(false);
    }//GEN-LAST:event_updateFacilityButtonActionPerformed

    private void deleteFacilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFacilityButtonActionPerformed
        // TODO add your handling code here:
        deleteFacilityButton.setBackground(Color.LIGHT_GRAY);
        deleteFacilityButton.setContentAreaFilled(true);

        int[] selectedRows = listFacilityTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one facility to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the selected facility(s)? This may affect existing invoices.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean allSucceeded = true;
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int modelRow = listFacilityTable.convertRowIndexToModel(selectedRows[i]);
                    String facilityId = listFacilityTable.getModel().getValueAt(modelRow, 0).toString();
                    if (!facilityService.deleteFacility(facilityId)) {
                        allSucceeded = false;
                    }
                }
                
                updateFacilityTable();
                
                if (allSucceeded) {
                    JOptionPane.showMessageDialog(this, "Facility(s) deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete all selected facilities.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        deleteFacilityButton.setContentAreaFilled(false);
    }//GEN-LAST:event_deleteFacilityButtonActionPerformed

    private void toggleMenuLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabel4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMenuLabel4MouseClicked

    private void detailFacilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailFacilityButtonActionPerformed
        // TODO add your handling code here:
        detailFacilityButton.setBackground(Color.LIGHT_GRAY);
        detailFacilityButton.setContentAreaFilled(true);

        int selectedRow = listFacilityTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a facility to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
        } else if (listFacilityTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one facility.", "Multiple Selections", JOptionPane.WARNING_MESSAGE);
        } else {
            String facilityId = listFacilityTable.getValueAt(selectedRow, 0).toString();
            Facility facilityToShow = facilityService.getAllFacilities().stream()
                    .filter(f -> f.getServiceId().equals(facilityId))
                    .findFirst()
                    .orElse(null);

            if (facilityToShow != null) {
                DetailFacilityForm detailForm = new DetailFacilityForm(this, true, facilityToShow);
                detailForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Could not find details for the selected facility.", "Data Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        detailFacilityButton.setContentAreaFilled(false);
    }//GEN-LAST:event_detailFacilityButtonActionPerformed

    private void addFacilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFacilityButtonActionPerformed
        // TODO add your handling code here:
        addFacilityButton.setBackground(Color.LIGHT_GRAY);
        addFacilityButton.setContentAreaFilled(true);
        
        AddFacilityForm addForm = new AddFacilityForm(this, true, this.facilityService);
        addForm.setVisible(true);

        addFacilityButton.setContentAreaFilled(false);
        updateFacilityTable();
    }//GEN-LAST:event_addFacilityButtonActionPerformed

    private void updateInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateInvoiceButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = listInvoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để cập nhật.", "Chưa chọn hóa đơn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy trạng thái từ cột thứ 4 (chỉ số 4) của bảng
            String status = listInvoiceTable.getValueAt(selectedRow, 4).toString();

            // Kiểm tra trạng thái của hóa đơn
            if (!"Pending".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this, "Chỉ có thể cập nhật các hóa đơn có trạng thái 'Pending'.", "Không thể cập nhật", JOptionPane.INFORMATION_MESSAGE);
                return; // Dừng lại nếu không phải là "Pending"
            }

            // Nếu trạng thái là "Pending", tiếp tục xử lý
            int invoiceId = (int) listInvoiceTable.getValueAt(selectedRow, 0);
            Invoice invoiceToUpdate = invoiceService.getAllInvoices().stream()
                    .filter(inv -> inv.getInvoiceId() == invoiceId)
                    .findFirst().orElse(null);
            
            if (invoiceToUpdate != null) {
                UpdateInvoiceForm updateForm = new UpdateInvoiceForm(this, true, invoiceToUpdate, invoiceService);
                updateForm.setVisible(true);
                updateInvoiceTable(); // Làm mới bảng sau khi form cập nhật đóng lại
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết hóa đơn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_updateInvoiceButtonActionPerformed

    private void deleteInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteInvoiceButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = listInvoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this invoice? This action cannot be undone.", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int invoiceId = (int) listInvoiceTable.getValueAt(selectedRow, 0);
            if (invoiceService.deleteInvoice(invoiceId)) {
                JOptionPane.showMessageDialog(this, "Invoice deleted successfully.");
                updateInvoiceTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete invoice.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteInvoiceButtonActionPerformed

    private void toggleMenuLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabel5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMenuLabel5MouseClicked

    private void detailInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailInvoiceButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = listInvoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int invoiceId = (int) listInvoiceTable.getValueAt(selectedRow, 0);
            Invoice invoiceToShow = invoiceService.getAllInvoices().stream()
                    .filter(inv -> inv.getInvoiceId() == invoiceId)
                    .findFirst().orElse(null);
            
            if (invoiceToShow != null) {
                DetailInvoiceForm detailForm = new DetailInvoiceForm(this, true, invoiceToShow);
                detailForm.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading invoice details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_detailInvoiceButtonActionPerformed

    private void profileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileButtonActionPerformed
        // TODO add your handling code here:
        showPanel(contentPanel, "invoice");
    }//GEN-LAST:event_profileButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeAdminForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeAdminForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeAdminForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeAdminForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HomeAdminForm h = new HomeAdminForm("CD001");
                h.setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to toàn màn hình
                h.setLocationRelativeTo(null); 
                h.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addComplaintButton;
    private javax.swing.JButton addFacilityButton;
    private javax.swing.JButton addParkingSlotButton;
    private javax.swing.JButton addServiceButton;
    private javax.swing.JButton assignComplaintButton;
    private javax.swing.JButton authorizationButton;
    private javax.swing.JPanel authorizationPanel;
    private javax.swing.JPanel authorizationToolbar;
    private javax.swing.JButton bookFacilities;
    private javax.swing.JButton bookingButton;
    private javax.swing.JPanel bookingFacilities;
    private javax.swing.JTextField cashReceived;
    private javax.swing.JTextField changeReturned;
    private javax.swing.JPanel complaintPanel;
    private javax.swing.JPanel complaintToolbar;
    private javax.swing.JButton complaintsButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteComplaintButton;
    private javax.swing.JButton deleteFacilityButton;
    private javax.swing.JButton deleteInvoiceButton;
    private javax.swing.JButton deleteParkingSlotButton;
    private javax.swing.JButton deleteServiceButton;
    private javax.swing.JButton deleteSubscribedFacilities;
    private javax.swing.JTextArea descriptionFacilities;
    private javax.swing.JButton detailComplaintButton;
    private javax.swing.JButton detailFacilityButton;
    private javax.swing.JButton detailInvoiceButton;
    private javax.swing.JButton exportParkingSlotsButton;
    private javax.swing.JButton exportServiceButton;
    private javax.swing.JPanel facilityPanel;
    private javax.swing.JPanel facilityToolbar;
    private javax.swing.JButton homeButton;
    private javax.swing.JPanel homePanel;
    private javax.swing.JTextField invoiceID;
    private javax.swing.JPanel invoicePanel;
    private javax.swing.JPanel invoiceToolbar;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel listAuthorizationPanel;
    private javax.swing.JTable listAuthorizationTable;
    private javax.swing.JLabel listComplaintLabel;
    private javax.swing.JLabel listComplaintLabel1;
    private javax.swing.JLabel listComplaintLabel2;
    private javax.swing.JLabel listComplaintLabel3;
    private javax.swing.JPanel listComplaintPanel;
    private javax.swing.JScrollPane listComplaintScroll;
    private javax.swing.JScrollPane listComplaintScroll1;
    private javax.swing.JScrollPane listComplaintScroll2;
    private javax.swing.JScrollPane listComplaintScroll3;
    private javax.swing.JTable listComplaintTable;
    private javax.swing.JTable listFacilitiesSubscribiedTable;
    private javax.swing.JTable listFacilitiesTable;
    private javax.swing.JPanel listFacilityPanel;
    private javax.swing.JTable listFacilityTable;
    private javax.swing.JPanel listInvoicePanel;
    private javax.swing.JTable listInvoiceTable;
    private javax.swing.JLabel listParkingSlotLabel;
    private javax.swing.JPanel listParkingSlotPanel;
    private javax.swing.JPanel listParkingSlotPanel1;
    private javax.swing.JScrollPane listParkingSlotScroll;
    private javax.swing.JScrollPane listParkingSlotScroll1;
    private javax.swing.JTable listParkingSlotTable;
    private javax.swing.JLabel listServiceLabel;
    private javax.swing.JPanel listServicePanel;
    private javax.swing.JScrollPane listServiceScroll;
    private javax.swing.JTable listServiceTable;
    private javax.swing.JButton logOutButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainParking;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JTextField nameResident;
    private javax.swing.JButton parkingManagementButton;
    private javax.swing.JPanel parkingToolbar;
    private javax.swing.JPanel parkingToolbar1;
    private javax.swing.JTextField phoneNumber;
    private javax.swing.JTextField price;
    private javax.swing.JButton printInvoiceButton;
    private javax.swing.JButton profileButton;
    private javax.swing.JButton residentManagementButton;
    private javax.swing.JPanel residentPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField searchAuthorizationField;
    private javax.swing.JTextField searchComplaintField;
    private javax.swing.JTextField searchFacilityField;
    private javax.swing.JTextField searchInvoiceField;
    private javax.swing.JTextField searchParkingSlotField;
    private javax.swing.JTextField searchServiceField;
    private javax.swing.JButton serviceFacilityButton;
    private javax.swing.JTextField serviceID;
    private javax.swing.JTextField serviceName;
    private javax.swing.JPanel servicePanel;
    private javax.swing.JPanel serviceSub;
    private javax.swing.JPanel serviceToolbar;
    private javax.swing.JPanel slot;
    private javax.swing.JLabel toggleMenuLabel;
    private javax.swing.JLabel toggleMenuLabel1;
    private javax.swing.JLabel toggleMenuLabel2;
    private javax.swing.JLabel toggleMenuLabel3;
    private javax.swing.JLabel toggleMenuLabel4;
    private javax.swing.JLabel toggleMenuLabel5;
    private javax.swing.JTextField totalBill;
    private javax.swing.JButton updateAuthorizationButton;
    private javax.swing.JButton updateComplaintButton;
    private javax.swing.JButton updateFacilityButton;
    private javax.swing.JButton updateInvoiceButton;
    private javax.swing.JButton updateParkingSlotButton;
    private javax.swing.JButton updateServiceButton;
    // End of variables declaration//GEN-END:variables

}
