package voting;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame() {
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        init();
    }

    private JLabel header(String t) {
        JLabel h = new JLabel(t, SwingConstants.CENTER);
        h.setFont(new Font("Segoe UI", Font.BOLD, 44));
        h.setForeground(new Color(44,62,80));
        h.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
        return h;
    }

    private JButton primary(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.BOLD, 22));
        b.setBackground(new Color(30,144,255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12,24,12,24));
        return b;
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(14,14,14,14);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0;c.gridy=0; add(header("ADMIN DASHBOARD"), c);

        JPanel grid = new JPanel(new GridLayout(2,2,20,20));
        grid.setOpaque(false);
        JButton add = primary("Add Candidate");
        JButton list = primary("View/Delete Candidates");
        JButton rel  = primary("Release Results");
        JButton seal = primary("Seal Results");
        grid.add(add); grid.add(list); grid.add(rel); grid.add(seal);
        c.gridy=1; add(grid, c);

        add.addActionListener(e -> new CandidateRegistrationFrame().setVisible(true));
        list.addActionListener(e -> new CandidateListFrame().setVisible(true));
        rel.addActionListener(e -> setReleased(true));
        seal.addActionListener(e -> setReleased(false));
    }

    private void setReleased(boolean yes) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE app_settings SET setting_value=? WHERE setting_key='results_released'")) {
            ps.setString(1, yes ? "true" : "false");
            int n = ps.executeUpdate();
            if (n == 0) {
                try (PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO app_settings (setting_key, setting_value) VALUES ('results_released', ?)")) {
                    ps2.setString(1, yes ? "true" : "false");
                    ps2.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, yes ? "Results released." : "Results sealed.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }
}
