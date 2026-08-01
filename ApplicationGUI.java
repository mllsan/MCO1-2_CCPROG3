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
        title.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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
        libraryButton.addActionListener(e -> showLibrary());
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

    private void showLibrary() {
        getContentPane().removeAll();

        setTitle("Media Vault - Library");

        JPanel main = new JPanel(new BorderLayout(15,15));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(BG);

        JLabel title = new JLabel("📚 My Library", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);

        north.add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.setBackground(BG);

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
                                                            "ALL",
                                                            "PLANNED",
                                                            "INPROGRESS",
                                                            "COMPLETED" });
        JComboBox<String> mediaFilter = new JComboBox<>(new String[]{
                                                            "ALL",
                                                            "Anime",
                                                            "Movie",
                                                            "Album" });
        
        JButton refreshButton = createMenuButton("Refresh");

        filterPanel.add(new JLabel("Status"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Media"));
        filterPanel.add(mediaFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(refreshButton);

        north.add(filterPanel, BorderLayout.SOUTH);
        main.add(title, BorderLayout.NORTH);

        JPanel libraryPanel = new JPanel();
        libraryPanel.setLayout(new BoxLayout(libraryPanel, BoxLayout.Y_AXIS));
        libraryPanel.setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)),
                                                        BorderFactory.createEmptyBorder(15,15,15,15)));

        card.add(new JLabel("One Piece"));
        card.add(new JLabel("Anime"));
        card.add(new JLabel("Status: Completed"));
        card.add(new JLabel("Episodes: 1137"));
        card.add(new JLabel("Rating: 10"));
        card.add(new JLabel("Review: Peak Fiction"));

        libraryPanel.add(card);

        JScrollPane scrollPane = new JScrollPane(libraryPanel);

        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scrollPane, BorderLayout.CENTER);

        add(main);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicationGUI());
    }
}