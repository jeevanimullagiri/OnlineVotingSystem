package voting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ResultFrame extends JFrame {
    private JTable table;
    private JLabel status;

    public ResultFrame() {
        setTitle("Election Results");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        init();
        refresh();
    }

    private JLabel header(String t) {
        JLabel h = new JLabel(t, SwingConstants.CENTER);
        h.setFont(new Font("Segoe UI", Font.BOLD, 44));
        h.setForeground(new Color(44,62,80));
        h.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));
        return h;
    }

    private void init() {
        setLayout(new BorderLayout(10,10));
        add(header("RESULTS"), BorderLayout.NORTH);
        table = new JTable(new DefaultTableModel(new Object[]{"Candidate","Party","Votes"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);
        status = new JLabel("Loading...", SwingConstants.CENTER);
        status.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        status.setBorder(BorderFactory.createEmptyBorder(8,8,16,8));
        add(status, BorderLayout.SOUTH);
    }

    private boolean released() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT setting_value FROM app_settings WHERE setting_key='results_released'");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return "true".equalsIgnoreCase(rs.getString(1));
        } catch (Exception ignored) {}
        return false;
    }

    private void refresh() {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        if (!released()) {
            status.setText("🔒 Results are sealed. Admin must release them.");
            return;
        }
        status.setText("✅ Results released.");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, party, votes FROM candidates ORDER BY votes DESC, name ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                m.addRow(new Object[]{ rs.getString(1), rs.getString(2), rs.getInt(3) });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }
}
