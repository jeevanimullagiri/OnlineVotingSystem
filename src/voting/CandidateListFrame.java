package voting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CandidateListFrame extends JFrame {
    private JTable table;

    public CandidateListFrame() {
        setTitle("Candidates");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        init();
        load();
    }

    private JLabel header(String t) {
        JLabel h = new JLabel(t, SwingConstants.CENTER);
        h.setFont(new Font("Segoe UI", Font.BOLD, 44));
        h.setForeground(new Color(44,62,80));
        h.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));
        return h;
    }

    private JButton danger(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.BOLD, 20));
        b.setBackground(new Color(231, 76, 60));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        return b;
    }

    private void init() {
        setLayout(new BorderLayout(10,10));
        add(header("CANDIDATE LIST"), BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(new Object[]{"ID","Name","Party","Votes"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        });
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        JButton refresh = new JButton("Refresh");
        refresh.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JButton delete = danger("Delete Selected");
        south.add(refresh); south.add(delete);
        add(south, BorderLayout.SOUTH);

        refresh.addActionListener(e -> load());
        delete.addActionListener(e -> deleteSelected());
    }

    private void load() {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT candidate_id,name,party,votes FROM candidates ORDER BY candidate_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                m.addRow(new Object[]{ rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4) });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row to delete."); return; }
        int id = (int) table.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Delete candidate ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM candidates WHERE candidate_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            ((DefaultTableModel) table.getModel()).removeRow(row);
            JOptionPane.showMessageDialog(this, "Candidate deleted.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }
}
