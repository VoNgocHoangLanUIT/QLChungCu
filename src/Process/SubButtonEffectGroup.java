package Process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class SubButtonEffectGroup {

    private JButton selectedButton = null;

    public SubButtonEffectGroup(JPanel panelSub) {
        panelSub.setVisible(false);
        Component[] components = panelSub.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                setupButton((JButton) comp);
            }
        }
    }

    private void setupButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));

        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                ButtonModel model = button.getModel();

                if (button == selectedButton) {
                    g2.setColor(new Color(0,153,153,150)); // Nền trắng khi được chọn
                    g2.fillRoundRect(5, 0, c.getWidth() - 10, c.getHeight(), 10, 10);

                    // Text mặc định
                    g2.setColor(Color.BLACK);
                } else if (model.isRollover()) {
                    g2.setColor(new Color(0,153,153, 60)); // Hover
                    g2.fillRoundRect(5, 0, c.getWidth() - 10, c.getHeight(), 10, 10);

                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(new Color(0, 0, 0, 0)); // Trong suốt
                    g2.fillRoundRect(5, 0, c.getWidth() - 10, c.getHeight(), 10, 10);

                    g2.setColor(Color.BLACK);
                }

                // Vẽ văn bản
                String text = button.getText();
                g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                int textX = 15; // Căn lề trái một chút
                int textY = (c.getHeight() - textHeight) / 2 + fm.getAscent();

                g2.drawString(text, textX, textY);

                g2.dispose();
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedButton = button;
                button.getParent().repaint();
            }
        });
    }
}
