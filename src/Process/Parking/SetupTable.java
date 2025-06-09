package Process.Parking;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SetupTable {

    public SetupTable(JTextField textField, JTable table) {
        setupTextField(textField, table);
        setupTable(table);
    }
    
    public SetupTable(JTable table){
        setupTable(table);
    }

    private void setupTextField(JTextField textField, JTable table) {
        String placeholder = "Search...";
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        // Viền màu cho textField (không thay đổi khi focus)
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

        // Tạo TableRowSorter cho JTable
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        // Thêm placeholder và lắng nghe sự kiện focus
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                    filterTable(rowSorter, ""); // Nếu không có gì, lọc lại bảng
                }
            }
        });

        // Thêm DocumentListener để lắng nghe sự thay đổi trong textField
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable(rowSorter, textField.getText().trim());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable(rowSorter, textField.getText().trim());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable(rowSorter, textField.getText().trim());
            }
        });
    }

    // Định nghĩa phương thức filterTable để lọc dữ liệu
    private void filterTable(TableRowSorter<TableModel> rowSorter, String query) {
        // Nếu không có từ khóa tìm kiếm thì không lọc
        if (query.isEmpty()) {
            rowSorter.setRowFilter(null); // Hiển thị tất cả các dòng
        } else {
            // Thực hiện lọc với từ khóa nhập vào
            rowSorter.setRowFilter(RowFilter.regexFilter(query)); // (?i) giúp tìm kiếm không phân biệt chữ hoa/thường
        }
    }

    // Thiết lập bảng (JTable)
    private void setupTable(JTable table) {
        table.setRowHeight(36);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // Tùy chỉnh header
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                            boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setHorizontalAlignment(CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setBackground(new Color(230, 230, 230));
                label.setForeground(Color.BLACK);
                label.setOpaque(true);
                label.setPreferredSize(new Dimension(label.getPreferredSize().width, 36));
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

                return label;
            }
        });

        // Tùy chỉnh cell
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                            boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                return label;
            }
        });
    }
}
