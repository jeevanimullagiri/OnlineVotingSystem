package voting;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class VotingFrame extends JFrame {
    private final String voterId;
    private final ButtonGroup group = new ButtonGroup();
    private final JPanel listPanel = new JPanel(new GridLayout(0,1,20,20));

    public VotingFrame(String voterId) {
        this.voterId = voterId;
        setTitle("Cast Your Vote");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        init();
        loadCandidates();
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
        setLayout(new BorderLayout(10,10));
        add(header("SELECT YOUR CANDIDATE"), BorderLayout.NORTH);
        listPanel.setBackground(Color.WHITE);
        add(new JScrollPane(listPanel), BorderLayout.CENTER);
        JButton vote = primary("SUBMIT VOTE");
        add(vote, BorderLayout.SOUTH);
        vote.addActionListener(e -> submitVote());
    }

    private void loadCandidates() {
        listPanel.removeAll();
        group.clearSelection();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT candidate_id,name,party FROM candidates ORDER BY candidate_id");
             ResultSet rs = ps.executeQuery()) {
            boolean any = false;
            while (rs.next()) {
                any = true;
                int id = rs.getInt("candidate_id");
                String name = rs.getString("name");
                String party = rs.getString("party");
                String label = name + (party != null && !party.isBlank() ? "  —  " + party : "");
                JRadioButton rb = new JRadioButton(label);
                rb.setFont(new Font("Segoe UI", Font.PLAIN, 22));
                rb.setBackground(Color.WHITE);
                rb.setActionCommand(String.valueOf(id));
                group.add(rb);
                listPanel.add(rb);
            }
            if (!any) listPanel.add(new JLabel("No candidates available.", SwingConstants.CENTER));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
        listPanel.revalidate(); listPanel.repaint();
    }

    private void submitVote() {
        if (group.getSelection() == null) { JOptionPane.showMessageDialog(this,"Please select a candidate."); return; }
        int candidateId = Integer.parseInt(group.getSelection().getActionCommand());

        String check = "SELECT has_voted FROM voters WHERE voter_id=? FOR UPDATE";
        String inc   = "UPDATE candidates SET votes = votes + 1 WHERE candidate_id = ?";
        String mark  = "UPDATE voters SET has_voted = TRUE WHERE voter_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psC = conn.prepareStatement(check);
                 PreparedStatement psI = conn.prepareStatement(inc);
                 PreparedStatement psM = conn.prepareStatement(mark)) {

                psC.setString(1, voterId);
                try (ResultSet rs = psC.executeQuery()) {
                    if (!rs.next()) { conn.rollback(); JOptionPane.showMessageDialog(this,"Voter not found."); return; }
                    if (rs.getBoolean(1)) { conn.rollback(); JOptionPane.showMessageDialog(this,"You already voted."); return; }
                }

                psI.setInt(1, candidateId); psI.executeUpdate();
                psM.setString(1, voterId);  psM.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Thank you — your vote has been recorded.");
                dispose();
               // new ResultFrame().setVisible(true);
               new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }
}
