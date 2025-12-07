package voting;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegistrationFrame extends JFrame {

    private JTextField tfId, tfName, tfAge, tfAddress;
    private JPasswordField pfPass;

    public RegistrationFrame() {
        setTitle("Register New Voter");
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
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(14,14,14,14);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0;c.gridy=0;c.gridwidth=2;
        main.add(header("REGISTER NEW VOTER"), c);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210), 2),
                BorderFactory.createEmptyBorder(30,50,30,50)
        ));
        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(12,12,12,12);
        f.fill = GridBagConstraints.HORIZONTAL;

        f.gridx=0; f.gridy=0;
        form.add(new JLabel("Voter ID:"), f);
        tfId = new JTextField(22);
        tfId.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1;
        form.add(tfId, f);

        f.gridx=0; f.gridy=1;
        form.add(new JLabel("Name:"), f);
        tfName = new JTextField(22);
        tfName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1;
        form.add(tfName, f);

        f.gridx=0; f.gridy=2;
        form.add(new JLabel("Password:"), f);
        pfPass = new JPasswordField(22);
        pfPass.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1;
        form.add(pfPass, f);

        f.gridx=0; f.gridy=3;
        form.add(new JLabel("Age:"), f);
        tfAge = new JTextField(22);
        tfAge.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1;
        form.add(tfAge, f);

        f.gridx=0; f.gridy=4;
        form.add(new JLabel("Address:"), f);
        tfAddress = new JTextField(22);
        tfAddress.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1;
        form.add(tfAddress, f);

        JButton save = primary("REGISTER");
        f.gridx=0; f.gridy=5; f.gridwidth=2;
        form.add(save, f);

        c.gridy=1;
        main.add(form, c);
        add(main);

        save.addActionListener(e -> registerNow());
    }

    private void registerNow() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO voters VALUES (?, ?, ?, ?, ?, FALSE)"
             )) {

            ps.setString(1, tfId.getText());
            ps.setString(2, tfName.getText());
            ps.setString(3, new String(pfPass.getPassword()));

            try {
                ps.setInt(4, Integer.parseInt(tfAge.getText()));
            } catch (NumberFormatException e) {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, tfAddress.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Voter Registered Successfully!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
