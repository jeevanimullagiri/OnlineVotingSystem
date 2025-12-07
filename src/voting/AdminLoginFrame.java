package voting;

import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {
    private JPasswordField pf;
    private static final String ADMIN_PASS = "password"; // change if you like

    public AdminLoginFrame() {
        setTitle("Admin Login");
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

        c.gridx=0;c.gridy=0; main.add(header("ADMIN LOGIN"), c);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210),2),
                BorderFactory.createEmptyBorder(30,50,30,50)));
        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(12,12,12,12);
        f.fill = GridBagConstraints.HORIZONTAL;

        f.gridx=0; f.gridy=0; card.add(new JLabel("Password:"), f);
        pf = new JPasswordField(22); pf.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        f.gridx=1; card.add(pf, f);

        JButton login = primary("LOGIN");
        f.gridx=0; f.gridy=1; f.gridwidth=2; card.add(login, f);

        c.gridy=1; main.add(card, c);
        add(main);

        login.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        if (ADMIN_PASS.equals(new String(pf.getPassword()))) {
            new AdminDashboardFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid admin password.");
        }
    }
}
