package Process;

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
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

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
                    filterTable(rowSorter, "");
                }
            }
        });

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

    private void filterTable(TableRowSorter<TableModel> rowSorter, String query) {
        if (query.isEmpty() || query.equals("Search...")) {
            rowSorter.setRowFilter(null);
        } else {
            // Sử dụng (?i) để tìm kiếm không phân biệt chữ hoa, chữ thường
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void setupTable(JTable table) {
        // --- THAY ĐỔI 1: TĂNG CHIỀU CAO CỦA DÒNG ---
        // Tăng chiều cao của mỗi dòng để dễ nhìn hơn
        table.setRowHeight(45); // Giá trị cũ là 36

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
                // --- THAY ĐỔI 2: TĂNG CỠ CHỮ HEADER ---
                label.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Cỡ chữ cũ là 14
                label.setBackground(new Color(230, 230, 230));
                label.setForeground(Color.BLACK);
                label.setOpaque(true);
                label.setPreferredSize(new Dimension(label.getPreferredSize().width, 40)); // Tăng chiều cao header
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

                return label;
            }
        });

        // Tùy chỉnh cell (ô chứa dữ liệu)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                            boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // --- THAY ĐỔI 3: TĂNG CỠ CHỮ NỘI DUNG CELL ---
                label.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Thêm dòng này để set font

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

                // Thêm hiệu ứng khi chọn dòng
                if (isSelected) {
                    label.setBackground(new Color(70, 113, 141));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)); // Hiệu ứng dòng chẵn lẻ
                    label.setForeground(Color.BLACK);
                }

                return label;
            }
        });
    }
}
