import javax.swing.*;
import java.awt.*;

public class ApplicationGUI extends JFrame {
    private User activeUser;

    // color palette
    private static final Color BG = new Color(250, 248, 245);           // cream
    private static final Color TEXT = new Color(70, 75, 85);            // grayish black

    public ApplicationGUI() {
        setTitle("Login Portal");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        applyTheme();

        if (handleAuthentication()) {
            buildMainMenu();
        } 
        else {
            System.exit(0);
        }
    }

    private void applyTheme() {
        UIManager.put("OptionPane.background", BG);
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.caretForeground", TEXT);
    }

    private boolean handleAuthentication() {
        String inputUN = ""; 

        while (true) {
            String[] options = {"Login", "Create Account", "Exit"};
            
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome! \nChoose an option to proceed",
                    "Account Authorization",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]
            );

            // login
            if (choice == 0) {
                boolean loggingIn = true;

                while (loggingIn) {
                    JTextField usernameField = createStyledTextField();
                    usernameField.setText(inputUN); 
                    JPasswordField passwordField = createStyledPasswordField();

                    JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
                    panel.setOpaque(false);
                    panel.add(createStyledLabel("Username:"));
                    panel.add(usernameField);
                    panel.add(createStyledLabel("Password:"));
                    panel.add(passwordField);

                    int result = JOptionPane.showConfirmDialog(null, panel, "Sign In", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        String username = usernameField.getText().trim();
                        String password = new String(passwordField.getPassword());

                        User user = SaveData.loadAccount(username);
                        if (user == null) {
                            JOptionPane.showMessageDialog(null, "Account does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else if (user.getPassword().equals(password)) {
                            this.activeUser = user;
                            return true;
                        } else {
                            String nextAction = handleWrongPassword(user);

                            if (nextAction.equals("retry")) {
                                inputUN = username;
                            } else if (nextAction.equals("reset")) {
                                inputUN = username; 
                                JOptionPane.showMessageDialog(null, "Please log in with your new password.", "Login", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                loggingIn = false; 
                            }
                        }
                    } 
                    else {
                        loggingIn = false;
                    }
                }
            } 
            // new account
            else if (choice == 1) {
                JTextField newUsernameField = createStyledTextField();
                JPasswordField newPasswordField = createStyledPasswordField();

                JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
                panel.setOpaque(false);
                panel.add(createStyledLabel("New Username:"));
                panel.add(newUsernameField);
                panel.add(createStyledLabel("New Password:"));
                panel.add(newPasswordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Create Account", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String newUsername = newUsernameField.getText().trim();
                    String newPassword = new String(newPasswordField.getPassword());

                    if (newUsername.isEmpty() || newPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.WARNING_MESSAGE);
                    } else if (SaveData.accountExists(newUsername)) {
                        JOptionPane.showMessageDialog(null, "Username already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        this.activeUser = new User(newUsername, newPassword);
                        SaveData.saveAccount(this.activeUser);
                        JOptionPane.showMessageDialog(null, "Account created successfully!");
                        return true;
                    }
                }
            } 
            // exit
            else {
                return false;
            }
        }
    }

    private String handleWrongPassword(User loadedProfile) {
        String[] choices = {"Try Again", "Reset Password", "Cancel"};
        int option = JOptionPane.showOptionDialog(
                null,
                "Incorrect password for: " + loadedProfile.getUsername(),
                "Authentication Failed",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null, choices, choices[0]
        );

        // try again
        if (option == 0) { 
            return "retry";
        } 
        // reset pw
        else if (option == 1) { 
            JPasswordField newPassword = createStyledPasswordField();
            int reset = JOptionPane.showConfirmDialog(null, newPassword, "Enter New Password:", JOptionPane.OK_CANCEL_OPTION);
            
            if (reset == JOptionPane.OK_OPTION) {
                String passStr = new String(newPassword.getPassword());
                if (!passStr.isEmpty()) {
                    loadedProfile.setPassword(passStr);
                    SaveData.saveAccount(loadedProfile);
                    JOptionPane.showMessageDialog(null, "Password reset successfully!");
                    return "reset"; 
                }
            }
        }
        
        return "cancel";
    }

    // helpers 
    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 205, 220), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 205, 220), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return pf;
    }

    private void buildMainMenu() {
        getContentPane().removeAll();

        setTitle("Media Vault");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // TITLE
        JLabel title = new JLabel("MEDIA VAULT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel welcome = new JLabel("Welcome, " + activeUser.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        title.setForeground(TEXT);

        JPanel north = new JPanel();
        north.setBackground(BG);
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        north.add(Box.createVerticalStrut(20));
        north.add(title);
        north.add(Box.createVerticalStrut(5));
        north.add(welcome);
        north.add(Box.createVerticalStrut(20));

        // BUTTONS
        JPanel center = new JPanel();
        center.setBackground(BG);
        center.setLayout(new GridLayout(5,1,15,15));
        center.setBorder(BorderFactory.createEmptyBorder(20,60,30,60));

        JButton libraryButton = createMenuButton("📚 View Library");
        center.add(libraryButton);
        JButton addButton = createMenuButton("➕ Add Media");
        center.add(addButton);
        JButton searchButton = createMenuButton("🔍 Search Entry");
        center.add(searchButton);
        JButton summaryButton = createMenuButton("📊 Library Summary");
        center.add(summaryButton);
        JButton exitButton = createMenuButton("💾 Save & Exit");
        center.add(exitButton);

        // ACTIONS
        libraryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Library goes here."));
        addButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add Media goes here."));
        searchButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Search Entry goes here."));
        summaryButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Library Summary goes here."));
        exitButton.addActionListener(e -> {SaveData.saveAccount(activeUser);
                                             JOptionPane.showMessageDialog(this, "Progress Saved!");
                                             dispose(); });
    
        mainPanel.add(north, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);

        add(mainPanel);

        revalidate();
        repaint();

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
       JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(125,102,196));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12,15,12,15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicationGUI());
    }
}