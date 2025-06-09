/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Process.*;
import Process.Parking.*;
import View.Booking.BookFacilitiesForm;
import View.Parking.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
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

import Service.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    // --- UI Helpers ---
    private SetLayoutPanel s;
    private ButtonEffectGroup group;
    
    public HomeAdminForm() {
        initComponents();
        this.parkingService = new ParkingService();
        this.facilityService = new FacilityService();
        this.invoiceService = new InvoiceService();
        this.subscribedFacilityService = new SubscribedFacilityService();
        
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
                            complaintsButton, serviceFacilityButton, accountsButton,
                            profileButton, otherButton, logOutButton};

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
        
        //xu ly chi tiet dich vu ngoai khi select
        listFacilitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = listFacilitiesTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Gán ảnh dịch vụ
                    Object value = listFacilitiesTable.getValueAt(selectedRow, 0);
                    if (value != null) {
                        String fileName = value.toString(); // ví dụ: "s1"
                        String path = "/icon/" + fileName +".jpg"; 

                        ImageIcon icon = new ImageIcon(getClass().getResource(path));
                        // Resize ảnh để vừa với JLabel
                        Image img = icon.getImage();
                        int labelWidth = jLabel2.getWidth();
                        int labelHeight = jLabel2.getHeight();

                        if (labelWidth > 0 && labelHeight > 0) {
                            Image scaled = img.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                            jLabel2.setIcon(new ImageIcon(scaled));
                        } else {
                            // Nếu label chưa hiển thị (width=0), thì gán tạm icon, resize sau
                            jLabel2.setIcon(icon);
                        }
                    }

                    // Gán dữ liệu vào các text field
                    Object serviceIDValue = listFacilitiesTable.getValueAt(selectedRow, 0);
                    Object serviceNameValue = listFacilitiesTable.getValueAt(selectedRow, 1);
                    Object priceValue = listFacilitiesTable.getValueAt(selectedRow, 5);

                    serviceID.setText(serviceIDValue != null ? serviceIDValue.toString() : "");
                    serviceName.setText(serviceNameValue != null ? serviceNameValue.toString() : "");
                    price.setText(priceValue != null ? priceValue.toString() : "");
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
        
    }
//--------------------------------------------ket thuc constructor-------------------------------------------------------  
    public void showPanel(JPanel panel,String name) {
        CardLayout cl = (CardLayout) panel.getLayout();
        cl.show(panel, name);
    }

    //----------------------------------------Parking------------------------------
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
    
//----------------------------------------booking------------------------------
    //xu ly tien thua cua khach
    private void updateChangeReturned() {
        try {
            double tongTien = Double.parseDouble(totalBill.getText().trim());
            double tienNhan = Double.parseDouble(cashReceived.getText().trim().replace(" ", ""));
            double tienThua = tienNhan - tongTien;

            // Tạo formatter với dấu cách giữa mỗi nhóm 3 chữ số
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);

            changeReturned.setText(formatter.format(tienThua));
        } catch (NumberFormatException e) {
            changeReturned.setText(""); // Nếu nhập sai định dạng số thì xóa kết quả
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
        // Gọi service để tính tổng tiền từ list object, thay vì đọc từ JTable
        if (invoiceID.getText().isEmpty()) {
            totalBill.setText("");
            cashReceived.setText("");
            changeReturned.setText("");
            return;
        }

        int currentInvoiceId = Integer.parseInt(invoiceID.getText());

        // Lấy danh sách chi tiết từ CSDL
        List<SubscribedFacility> currentCart = subscribedFacilityService.getByInvoiceId(currentInvoiceId);

        // Tính tổng tiền từ danh sách này
        double totalFee = invoiceService.calculateTotalFee(currentCart);

        // Cập nhật tổng tiền vào bảng HoaDon trong CSDL
        invoiceService.updateInvoiceTotal(currentInvoiceId, totalFee);

        // Hiển thị tổng tiền lên giao diện
        totalBill.setText(String.valueOf(totalFee));
    }

    private void updateSubscribedFacilitiesTable() {
        DefaultTableModel subscribedModel = (DefaultTableModel) listFacilitiesSubscribiedTable.getModel();
        subscribedModel.setRowCount(0); // Xóa hết dữ liệu cũ

        if (invoiceID.getText().isEmpty()) {
            return;
        }

        int currentInvoiceId = Integer.parseInt(invoiceID.getText());
        List<SubscribedFacility> cartFromDB = subscribedFacilityService.getByInvoiceId(currentInvoiceId);

        // Sử dụng Map để gộp các dịch vụ có cùng tên
        // Key: Service Name (String), Value: Dữ liệu dòng (Object[])
        java.util.Map<String, Object[]> aggregatedRows = new java.util.LinkedHashMap<>();

        for (SubscribedFacility sf : cartFromDB) {
            String serviceName = sf.getServiceName();

            // Kiểm tra xem dịch vụ đã có trong map hay chưa
            if (aggregatedRows.containsKey(serviceName)) {
                // Nếu đã có, cập nhật số lượng và thành tiền
                Object[] existingRow = aggregatedRows.get(serviceName);

                try {
                    // Giả định: cột số lượng (quantity) ở vị trí 1, đơn giá (unitPrice) ở vị trí 3
                    int oldQuantity = Integer.parseInt(existingRow[1].toString());
                    int quantityToAdd = sf.getQuantity(); // Giả định getQuantity() trả về int
                    int newQuantity = oldQuantity + quantityToAdd;

                    double unitPrice = Double.parseDouble(existingRow[3].toString());
                    double newLineTotal = newQuantity * unitPrice;

                    // Cập nhật lại dòng dữ liệu
                    existingRow[1] = newQuantity;       // Cập nhật số lượng mới
                    existingRow[4] = newLineTotal;      // Cập nhật thành tiền mới

                } catch (NumberFormatException e) {
                    // Xử lý nếu có lỗi khi chuyển đổi kiểu dữ liệu.
                    // Cần đảm bảo kiểu dữ liệu trả về của getQuantity() và getUnitPrice() là số.
                    System.err.println("Lỗi định dạng số: " + e.getMessage());
                }

            } else {
                // Nếu chưa có, thêm dòng mới vào map
                Object[] newRow = new Object[]{
                    sf.getServiceName(),
                    sf.getQuantity(),
                    sf.getUnit(),
                    sf.getUnitPrice(),
                    sf.getLineTotal()
                };
                aggregatedRows.put(serviceName, newRow);
            }
        }

        // Thêm các dòng đã được gộp vào bảng
        for (Object[] rowData : aggregatedRows.values()) {
            subscribedModel.addRow(rowData);
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
        apartmentPanel = new View.ColorPanel.gradientMenu();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        maCanHo = new javax.swing.JTextField();
        dienTich = new javax.swing.JTextField();
        trangThai = new javax.swing.JTextField();
        soDien = new javax.swing.JTextField();
        soNuoc = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        themButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
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
        jTextArea1 = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        invoiceID = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
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
        scrollPane = new javax.swing.JScrollPane();
        menuPanel = new javax.swing.JPanel();
        accountsButton = new javax.swing.JButton();
        profileButton = new javax.swing.JButton();
        otherButton = new javax.swing.JButton();
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
                .addGap(0, 1188, Short.MAX_VALUE))
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
                .addContainerGap(857, Short.MAX_VALUE))
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

        apartmentPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Diện tích");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Số nước");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Trạng thái");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Số điện");

        maCanHo.setText("Mã căn hộ");

        dienTich.setText("Diện tích");

        trangThai.setText("Trạng thái");

        soDien.setText("Số điện");

        soNuoc.setText("Số Nước");

        jLabel1.setBackground(new java.awt.Color(102, 102, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Thêm căn hộ");

        themButton.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        themButton.setText("Thêm");
        themButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                themButtonActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Mã căn hộ");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("V");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout apartmentPanelLayout = new javax.swing.GroupLayout(apartmentPanel);
        apartmentPanel.setLayout(apartmentPanelLayout);
        apartmentPanelLayout.setHorizontalGroup(
            apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apartmentPanelLayout.createSequentialGroup()
                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(apartmentPanelLayout.createSequentialGroup()
                        .addGap(339, 339, 339)
                        .addComponent(themButton, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(apartmentPanelLayout.createSequentialGroup()
                        .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, apartmentPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17))
                            .addGroup(apartmentPanelLayout.createSequentialGroup()
                                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(apartmentPanelLayout.createSequentialGroup()
                                        .addGap(57, 57, 57)
                                        .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(apartmentPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(soDien, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trangThai, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dienTich, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maCanHo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                            .addComponent(soNuoc)
                            .addGroup(apartmentPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(153, 153, 153)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        apartmentPanelLayout.setVerticalGroup(
            apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apartmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maCanHo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dienTich, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, apartmentPanelLayout.createSequentialGroup()
                        .addGroup(apartmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(soDien, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(soNuoc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(themButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(422, 422, 422))
        );

        contentPanel.add(apartmentPanel, "apartment");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 359, Short.MAX_VALUE)
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
            .addComponent(listParkingSlotScroll1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        listParkingSlotPanel1Layout.setVerticalGroup(
            listParkingSlotPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listParkingSlotScroll1)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/s1.jpg"))); // NOI18N
        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        serviceID.setBackground(new java.awt.Color(240, 240, 240));
        serviceID.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        serviceID.setText("s1");
        serviceID.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        serviceID.setFocusable(false);

        serviceName.setBackground(new java.awt.Color(240, 240, 240));
        serviceName.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        serviceName.setText("Gym");
        serviceName.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        serviceName.setFocusable(false);
        serviceName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceNameActionPerformed(evt);
            }
        });

        price.setBackground(new java.awt.Color(240, 240, 240));
        price.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        price.setText("25000");
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

        jTextArea1.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("A fully equipped fitness center featuring modern \nworkout machines, free weights, and exercise areas. \nResidents can enjoy a healthy lifestyle with access to \ncardio equipment, strength training machines.\n");
        jTextArea1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(30, 30, 30)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2, 2, 2))
                        .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 16, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(serviceID, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(serviceName, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serviceID, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serviceName, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(listParkingSlotPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parkingToolbar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jTextField5.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jTextField5.setText("0977771637");
        jTextField5.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));

        jTextField7.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jTextField7.setText("Vo Ngoc Hoang Lan");
        jTextField7.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray, 2));

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
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                "Service Name", "Quantity", "Unit", "Unit Price", "Line Total"
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 359, Short.MAX_VALUE)
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
                "ServiceID", "Service Name", "Service Provider Name", "Unit", "Price"
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

        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        scrollPane.setPreferredSize(new java.awt.Dimension(230, 551));

        menuPanel.setBackground(new java.awt.Color(255, 255, 255));

        accountsButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        accountsButton.setText("Accounts");
        accountsButton.setContentAreaFilled(false);

        profileButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        profileButton.setText("Profile");
        profileButton.setContentAreaFilled(false);

        otherButton.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        otherButton.setText("Other");
        otherButton.setContentAreaFilled(false);
        otherButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherButtonActionPerformed(evt);
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
            .addComponent(otherButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(accountsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(accountsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(otherButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void themButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_themButtonActionPerformed
        // TODO add your handling code here:
        int maCanHoText = Integer.parseInt(maCanHo.getText());
        double dienTichText = Double.parseDouble(dienTich.getText());
        String trangThaiText = trangThai.getText();
        int soDienText = Integer.parseInt(soDien.getText());
        int soNuocText = Integer.parseInt(soNuoc.getText());

        //Tao Doi Tuong Doi Bong
        CanHo ch = new CanHo();

        //Lay ket qua tu CSDL
        int countRecord = ch.themCanHo(maCanHoText, dienTichText, trangThaiText, soDienText, soNuocText);

        if (countRecord > 0) {
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            hide();
        }
    }//GEN-LAST:event_themButtonActionPerformed

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        // TODO add your handling code here:
        s.toggleMenu();
    }//GEN-LAST:event_jLabel10MouseClicked

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
        AddParkingForm a = new AddParkingForm(this, true); // 'this' là Frame cha
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
            UpdateParkingForm updateForm = new UpdateParkingForm(this, true, selectedSlot);
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

        // Mở dialog chọn giờ và truyền thông tin cần thiết
        BookFacilitiesForm timeDialog = new BookFacilitiesForm(this, true, selectedFacility, facilityService);
        timeDialog.setVisible(true);

        if (timeDialog.isConfirmed()) {
            List<String> selectedSlots = timeDialog.getSelectedTimeSlots();
            if (selectedSlots.isEmpty()) {
                return; // Không làm gì nếu người dùng không chọn khung giờ nào
            }

            // Tạo hóa đơn nếu cần
            int currentInvoiceId;
            if (invoiceID.getText().isEmpty()) {
                int newId = invoiceService.createInvoice(0);
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

            // Thêm từng khung giờ đã chọn vào CSDL
            for (String slot : selectedSlots) {
                SubscribedFacility newItem = new SubscribedFacility();
                newItem.setInvoiceId(currentInvoiceId);
                newItem.setServiceId(selectedFacility.getServiceId());
                newItem.setUnitPrice(selectedFacility.getPrice());
                newItem.setKhungGio(slot);
                
                subscribedFacilityService.addOrUpdateFacility(newItem);
            }

            // Cập nhật lại giao diện
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
        showPanel(contentPanel, "booking");
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

    private void otherButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_otherButtonActionPerformed

    private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServiceButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addServiceButtonActionPerformed

    private void updateServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateServiceButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateServiceButtonActionPerformed

    private void deleteServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteServiceButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteServiceButtonActionPerformed

    private void toggleMenuLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toggleMenuLabel1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_toggleMenuLabel1MouseClicked

    private void printInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printInvoiceButtonActionPerformed
        // TODO add your handling code here:
        if (invoiceID.getText().isEmpty()) {
            return; // Không có gì để làm
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn hoàn thành hóa đơn này và bắt đầu một hóa đơn mới không?",
                "Hoàn thành hóa đơn",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Reset lại toàn bộ các trường thông tin hóa đơn
            invoiceID.setText("");
            totalBill.setText("");
            cashReceived.setText("");
            changeReturned.setText("");

            // Gọi hàm cập nhật bảng chi tiết, nó sẽ tự xóa các dòng vì không có invoiceID
            updateSubscribedFacilitiesTable(); 

            JOptionPane.showMessageDialog(this, "Hóa đơn đã hoàn thành. Sẵn sàng cho hóa đơn tiếp theo.");
        }
    }//GEN-LAST:event_printInvoiceButtonActionPerformed
    
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
                HomeAdminForm h = new HomeAdminForm();
                h.setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to toàn màn hình
                h.setLocationRelativeTo(null); 
                h.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accountsButton;
    private javax.swing.JButton addParkingSlotButton;
    private javax.swing.JButton addServiceButton;
    private View.ColorPanel.gradientMenu apartmentPanel;
    private javax.swing.JButton bookFacilities;
    private javax.swing.JPanel bookingFacilities;
    private javax.swing.JTextField cashReceived;
    private javax.swing.JTextField changeReturned;
    private javax.swing.JButton complaintsButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton deleteParkingSlotButton;
    private javax.swing.JButton deleteServiceButton;
    private javax.swing.JButton deleteSubscribedFacilities;
    private javax.swing.JTextField dienTich;
    private javax.swing.JButton exportParkingSlotsButton;
    private javax.swing.JButton exportServiceButton;
    private javax.swing.JButton homeButton;
    private javax.swing.JPanel homePanel;
    private javax.swing.JTextField invoiceID;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
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
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTable listFacilitiesSubscribiedTable;
    private javax.swing.JTable listFacilitiesTable;
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
    private javax.swing.JTextField maCanHo;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainParking;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JButton otherButton;
    private javax.swing.JButton parkingManagementButton;
    private javax.swing.JPanel parkingToolbar;
    private javax.swing.JPanel parkingToolbar1;
    private javax.swing.JTextField price;
    private javax.swing.JButton printInvoiceButton;
    private javax.swing.JButton profileButton;
    private javax.swing.JButton residentManagementButton;
    private javax.swing.JPanel residentPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField searchParkingSlotField;
    private javax.swing.JTextField searchServiceField;
    private javax.swing.JButton serviceFacilityButton;
    private javax.swing.JTextField serviceID;
    private javax.swing.JTextField serviceName;
    private javax.swing.JPanel servicePanel;
    private javax.swing.JPanel serviceSub;
    private javax.swing.JPanel serviceToolbar;
    private javax.swing.JPanel slot;
    private javax.swing.JTextField soDien;
    private javax.swing.JTextField soNuoc;
    private javax.swing.JButton themButton;
    private javax.swing.JLabel toggleMenuLabel;
    private javax.swing.JLabel toggleMenuLabel1;
    private javax.swing.JTextField totalBill;
    private javax.swing.JTextField trangThai;
    private javax.swing.JButton updateParkingSlotButton;
    private javax.swing.JButton updateServiceButton;
    // End of variables declaration//GEN-END:variables

}
