package voting;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CandidateRegistrationFrame extends JFrame {
    private JTextField tfName, tfParty;

    public CandidateRegistrationFrame() {
        setTitle("Add Candidate");
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

        c.gridx=0;c.gridy=0; add(header("REGISTER CANDIDATE"), c);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210),2),
                BorderFactory.createEmptyBorder(30,50,30,50)));
        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(12,12,12,12);
        f.fill = GridBagConstraints.HORIZONTAL;

        f.gridx=0; f.gridy=0; form.add(new JLabel("Name*:"), f);
        tfName = new JTextField(24); tfName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1; form.add(tfName, f);

        f.gridx=0; f.gridy=1; form.add(new JLabel("Party:"), f);
        tfParty = new JTextField(24); tfParty.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1; form.add(tfParty, f);

        JButton save = primary("SAVE");
        f.gridx=0; f.gridy=2; f.gridwidth=2; form.add(save, f);

        c.gridy=1; add(form, c);

        save.addActionListener(e -> save());
    }

    private void save() {
        String name = tfName.getText().trim();
        String party = tfParty.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this,"Name is required."); return; }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO candidates (name,party) VALUES (?,?)")) {
            ps.setString(1, name);
            if (party.isEmpty()) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2, party);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Candidate added.");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }
}
