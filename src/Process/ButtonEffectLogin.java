/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Process;
// File: ButtonEffect.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonEffectLogin {

    public static void applyGradient(JButton button, Color color1, Color color2) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);

        // Ghi đè paintComponent để vẽ gradient
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                int w = c.getWidth();
                int h = c.getHeight();

                GradientPaint gp = new GradientPaint(
                    0, 0, color1,
                    w, h, color2
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 15, 15); // Bo góc
                g2.dispose();

                super.paint(g, c);
            }
        });
    }
}

