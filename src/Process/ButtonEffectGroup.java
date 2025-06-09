package Process;

import View.ColorPanel.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class ButtonEffectGroup {

    private JButton selectedButton = null;
    private JPanel subPanel; // thêm biến subPanel
    private JButton service;
    private JPanel menuPanel; 
    private JButton logout;

    public ButtonEffectGroup(JButton[] buttons, String[] iconPaths, JPanel subPanel, JButton service, JPanel menuPanel, JButton logout) {
        this.subPanel = subPanel;
        this.menuPanel = menuPanel;
        this.service = service;
        this.logout = logout;
        // Gán icon cho mỗi button
        assignIconsToButtons(buttons, iconPaths);

        // Thiết lập hiệu ứng cho các button
        for (JButton button : buttons) {
            setupButton(button);
        }
    }

    private void assignIconsToButtons(JButton[] buttons, String[] iconPaths) {
        for (int i = 0; i < buttons.length && i < iconPaths.length; i++) {
            // Tải icon từ đường dẫn
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPaths[i]));

            // Resize icon ngay lúc khởi tạo
            int iconWidth = 32;
            int iconHeight = 32;

            if (i == buttons.length - 1) {
                // Nếu là nút cuối cùng thì đặt icon 70x70
                iconWidth = 70;
                iconHeight = 70;
            }

            Image image = icon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(image);

            buttons[i].setIcon(scaledIcon);

            // Căn chỉnh vị trí icon và text
            buttons[i].setHorizontalTextPosition(SwingConstants.RIGHT);
            buttons[i].setIconTextGap(10);
        }
    }

    private void setupButton(JButton button) {
        // Tắt style mặc định để tự vẽ
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.BLACK);
        boolean isLogoutButton = (button == logout); // Kiểm tra nếu button là nút logout
        int fontSize = isLogoutButton ? 24 : 15;
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));

        // Vẽ lại UI với hiệu ứng hover và click
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = button.getModel();

                if (button == selectedButton) {
                    g2.setColor(new Color(0,153,153,150)); // Nền khi click
                    g2.fillRoundRect(5, 0, c.getWidth() - 10, c.getHeight(), 10, 10);
                    
                    // Text mặc định
                    g2.setColor(Color.BLACK);
                } else if (model.isRollover()) {
                    g2.setColor(new Color(0,153,153, 60)); // Hover
                    g2.fillRoundRect(5, 0, c.getWidth() - 10, c.getHeight(), 10, 10);
                    
                    // Text mặc định
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(new Color(0, 0, 0, 0)); // Trong suốt
                    g2.fillRoundRect(5, 0, c.getWidth() - 10, c.getHeight(), 10, 10);
                    
                    // Text mặc định
                    g2.setColor(Color.BLACK);
                }
                
                // Lấy và vẽ icon mà không thay đổi
                Icon icon = button.getIcon();
                int iconWidth = 0;
                int iconHeight = 0;
                int iconX = 10;
                int iconY = 0;
                if (icon != null) {
                    iconWidth = icon.getIconWidth();
                    iconHeight = icon.getIconHeight();
                    iconY = (c.getHeight() - iconHeight) / 2;
                    icon.paintIcon(button, g2, iconX, iconY);
                }
                
                g2.setFont(button.getFont());
                
                // Sau khi vẽ nền, vẽ lại văn bản 
                String text = button.getText();
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                int textX = iconX + iconWidth + 10; // cách icon 10px
                int textY = (c.getHeight() - textHeight) / 2 + fm.getAscent();

                g2.drawString(text, textX, textY);

                g2.dispose();
            }
        });

        // Sự kiện chuột
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(subPanel.isVisible() == true && button != service){
                    subPanel.setVisible(false);
                    menuPanel.setPreferredSize(new Dimension(menuPanel.getWidth(), 545));
                    menuPanel.setSize(menuPanel.getPreferredSize());
                    menuPanel.revalidate();
                    menuPanel.repaint();
                }
                selectedButton = button;
                button.getParent().repaint(); // Cập nhật lại tất cả button trong cùng panel
            }
        });
    }
    
}
