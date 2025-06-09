package Process;

import javax.swing.*;
import java.awt.*;

public class SetLayoutPanel {
    private JScrollPane panelMenu;
    private JPanel panelContent;
    private JPanel panelMain;
    private boolean isMenuOpen = true;  // Mặc định menu đang mở

    public SetLayoutPanel(JScrollPane menu, JPanel content, JPanel mainPanel) {
        this.panelMenu = menu;
        this.panelContent = content;
        this.panelMain = mainPanel;
    }

    public void openMenu() {
        panelMenu.setPreferredSize(new Dimension(243, panelMenu.getHeight()));
        panelMenu.revalidate();
        panelMenu.repaint();

        panelContent.setBounds(243, 0, panelMain.getWidth() - 243, panelContent.getHeight());
        panelMain.revalidate();
        panelMain.repaint();
        isMenuOpen = true;
    }

    public void closeMenu() {
        panelMenu.setPreferredSize(new Dimension(0, panelMenu.getHeight()));
        panelMenu.revalidate();
        panelMenu.repaint();

        panelContent.setBounds(0, 0, panelMain.getWidth(), panelContent.getHeight());
        panelMain.revalidate();
        panelMain.repaint();
        isMenuOpen = false;
    }


    // Toggle giữa mở và đóng
    public void toggleMenu() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }
}
