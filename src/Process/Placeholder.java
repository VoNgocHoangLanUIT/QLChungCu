package Process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Placeholder {

    // Thêm placeholder cho JTextField
    public static void addPlaceholderToTextField(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

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
                }
            }
        });
    }

    // Thêm placeholder cho JPasswordField
    public static void addPlaceholderToPasswordField(JPasswordField passwordField, String placeholder) {
        // Hiện placeholder dạng text (mặc định JPasswordField sẽ che ký tự)
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = new String(passwordField.getPassword());
                if (currentText.equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    // Bật chế độ ẩn ký tự mật khẩu
                    passwordField.setEchoChar('•');  // hoặc (char) 8226
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String currentText = new String(passwordField.getPassword());
                if (currentText.isEmpty()) {
                    // Hiển thị placeholder dạng text
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
    }
}
