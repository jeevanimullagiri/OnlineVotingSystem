import voting.DBConnection;
import voting.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DBConnection.initDatabase();
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
