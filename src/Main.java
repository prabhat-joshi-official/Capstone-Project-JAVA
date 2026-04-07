import ui.ModernExamApp;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModernExamApp app = new ModernExamApp();
            app.setVisible(true);
        });
    }
}
