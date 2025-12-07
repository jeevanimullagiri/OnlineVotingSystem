package voting;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField tfVoter;
    private JPasswordField pfPass;

    public LoginFrame() {
        setTitle("Online Voting System — Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);        // Full screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        init();
    }

    private JLabel header(String text) {
        JLabel h = new JLabel(text, SwingConstants.CENTER);
        h.setFont(new Font("Segoe UI", Font.BOLD, 48));
        h.setForeground(new Color(44, 62, 80));
        h.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        return h;
    }

    private JButton primary(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.BOLD, 22));
        b.setBackground(new Color(30,144,255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        return b;
    }

    private JButton secondary(String label) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return b;
    }

    private void init() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; c.gridwidth=2;
        main.add(header("ONLINE VOTING SYSTEM"), c);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210), 2),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        GridBagConstraints b = new GridBagConstraints();
        b.insets = new Insets(16, 16, 16, 16);
        b.fill = GridBagConstraints.HORIZONTAL;

        JLabel sub = new JLabel("VOTER LOGIN", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.BOLD, 28));
        sub.setForeground(new Color(52, 152, 219));
        b.gridx=0; b.gridy=0; b.gridwidth=2; card.add(sub, b);

        b.gridwidth=1;
        b.gridx=0; b.gridy=1; card.add(new JLabel("Voter ID:"), b);
        tfVoter = new JTextField(22); tfVoter.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        b.gridx=1; card.add(tfVoter, b);

        b.gridx=0; b.gridy=2; card.add(new JLabel("Password:"), b);
        pfPass = new JPasswordField(22); pfPass.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        b.gridx=1; card.add(pfPass, b);

        JButton btnLogin   = primary("LOGIN");
        JButton btnReg     = secondary("REGISTER");
        JButton btnAdmin   = secondary("ADMIN");
        JButton btnResults = secondary("VIEW RESULTS");

        b.gridx=0; b.gridy=3; b.gridwidth=2; card.add(btnLogin, b);
        b.gridy=4; card.add(btnReg, b);
        b.gridy=5; card.add(btnAdmin, b);
        b.gridy=6; card.add(btnResults, b);

        c.gridy=1; c.gridwidth=2;
        main.add(card, c);

        add(main);

        btnLogin.addActionListener(e -> login());
        btnReg.addActionListener(e -> new RegistrationFrame().setVisible(true));
        btnAdmin.addActionListener(e -> new AdminLoginFrame().setVisible(true));
        btnResults.addActionListener(e -> new ResultFrame().setVisible(true));
        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT has_voted FROM voters WHERE voter_id=? AND password=?")) {
            ps.setString(1, tfVoter.getText().trim());
            ps.setString(2, new String(pfPass.getPassword()));
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { JOptionPane.showMessageDialog(this,"Invalid credentials."); return; }
            if (rs.getBoolean(1)) { JOptionPane.showMessageDialog(this,"You have already voted."); return; }
            new VotingFrame(tfVoter.getText().trim()).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }
}
