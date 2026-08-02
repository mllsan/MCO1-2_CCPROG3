import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * GUI of the Media Vault application.
 * 
 * Relationships:
 * - View component of the MVC pattern.
 * - Delegates all logic and state updates to LibraryController.
 * - Utilizes Java Swing for custom styling.
 */
public class ApplicationGUI extends JFrame {
    private LibraryController controller;
    private static final Color BG = new Color(250, 248, 245);
    private static final Color TEXT = new Color(70, 75, 85);

    /**
     * Constructor of the GUI window.
     * Sets initial state of screen.
     */
    public ApplicationGUI() {
        setTitle("Login Portal");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        applyTheme();

        if (handleAuthentication()) {
            buildMainMenu();
        } else {
            System.exit(0);
        }
    }

    /**
     * Sets the default colors across dialogs and text fields.
     */
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

    /**
     * Handles initial user authentication.
     *
     * @return true if account authentication succeeds and system begins; false if user exits
     */
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
                    JTextField usernameField = (JTextField) createInputField(false);
                    usernameField.setText(inputUN); 
                    JPasswordField passwordField = (JPasswordField) createInputField(true);

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
                            this.controller = new LibraryController(user);
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
                    } else {
                        loggingIn = false;
                    }
                }
            } 
            // new account
            else if (choice == 1) {
                JTextField newUsernameField = (JTextField) createInputField(false);
                JPasswordField newPasswordField = (JPasswordField) createInputField(true);

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
                        User newUser = new User(newUsername, newPassword);
                        SaveData.saveAccount(newUser);
                        this.controller = new LibraryController(newUser);
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

    /**
     * Prompts the user with choices when password authentication fails.
     *
     * @param loadedProfile the profile being accessed
     * @return option key
     */
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

        if (option == 0) { 
            return "retry";
        } else if (option == 1) { 
            JPasswordField newPassword = (JPasswordField) createInputField(true);
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

    /**
     * Builds and displays the main menu.
     */
    private void buildMainMenu() {
        getContentPane().removeAll();

        setTitle("Media Vault");
        setSize(450, 480);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // TITLE
        JLabel title = new JLabel("MEDIA VAULT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel welcome = new JLabel("Welcome, " + controller.getActiveUser().getUsername(), SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        welcome.setForeground(TEXT);

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
        center.setBorder(BorderFactory.createEmptyBorder(10,60,30,60));

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

        // ACTIONS DELEGATED TO CONTROLLER
        libraryButton.addActionListener(e -> showLibrary());
        addButton.addActionListener(e -> handleAddEntry());
        searchButton.addActionListener(e -> searchEntry());
        summaryButton.addActionListener(e -> showLibrarySummary());
        exitButton.addActionListener(e -> {
            controller.saveProgress();
            JOptionPane.showMessageDialog(this, "Progress Saved!");
            dispose(); 
        });
    
        mainPanel.add(north, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);

        add(mainPanel);

        revalidate();
        repaint();

        setVisible(true);
    }

    /**
     * Builds and displays the view Library screen.
     */
    private void showLibrary() {
        getContentPane().removeAll();

        setTitle("Media Vault - Library");

        JPanel main = new JPanel(new BorderLayout(15,15));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // TITLE
        JLabel title = new JLabel("📚 My Library", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);

        main.add(title, BorderLayout.NORTH);

        // FILTERS
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

        JComboBox<String> genreFilter = new JComboBox<>();
        genreFilter.addItem("ALL");

        mediaFilter.addActionListener(e -> {
            genreFilter.removeAllItems();
            genreFilter.addItem("ALL");
            String selectedMedia = (String) mediaFilter.getSelectedItem();

            if ("Anime".equals(selectedMedia) || "Movie".equals(selectedMedia)) {
                genreFilter.setEnabled(true);
                for (Genre g : Genre.values()) {
                    genreFilter.addItem(g.toString());
                }
            } else if ("Album".equals(selectedMedia)) {
                genreFilter.setEnabled(true);
                for (MusicGenre mg : MusicGenre.values()) {
                    genreFilter.addItem(mg.toString());
                }
            } else {
                genreFilter.setEnabled(false);
            }
        });
        
        JButton refresh = createMenuButton("Refresh");
        JButton back = createMenuButton("← Back");

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Media:"));
        filterPanel.add(mediaFilter);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Genre:"));
        filterPanel.add(genreFilter);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(refresh);
        filterPanel.add(back);

        main.add(filterPanel, BorderLayout.SOUTH);

        // LIBRARY
        JPanel libraryPanel = new JPanel();
        libraryPanel.setLayout(new BoxLayout(libraryPanel, BoxLayout.Y_AXIS));
        libraryPanel.setBackground(BG);

        JScrollPane scrollPane = new JScrollPane(libraryPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scrollPane, BorderLayout.CENTER);

        refresh.addActionListener(e -> {
            libraryPanel.removeAll();

            String media = (String) mediaFilter.getSelectedItem();
            String statusText = (String) statusFilter.getSelectedItem();
            String selectedGenre = genreFilter.isEnabled() ? (String) genreFilter.getSelectedItem() : "ALL";

            Status status = null;

            if(!statusText.equals("ALL")) {
                status = Status.valueOf(statusText);
            }

            for (MediaEntry entry : controller.getFilteredEntries(media, status, selectedGenre)) {
                libraryPanel.add(createEntryCard(entry, false));
                libraryPanel.add(Box.createVerticalStrut(15));
            }

            if (libraryPanel.getComponentCount() == 0) {
                JLabel empty = new JLabel("No entries Found.");
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);

                libraryPanel.add(empty);
            }

            libraryPanel.revalidate();
            libraryPanel.repaint();
        });

        back.addActionListener(e -> buildMainMenu());

        add(main);

        revalidate();
        repaint();

        refresh.doClick();
    }

    /**
     * Generates a UI card panel representing a media entry.
     *
     * @param entry the Media Entry to represent
     * @param showEpisodes true if scrollable episode table for Anime entries should be shown
     * @return JPanel containing entry details
     */
    private JPanel createEntryCard(MediaEntry entry, boolean showEpisodes) {
        JPanel card = createStyledCard(15, 15, 15, 15);

        JLabel title = new JLabel(entry.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(title);
        card.add(new JLabel("Type: " + entry.getMediaType()));
        card.add(new JLabel("Status: " + entry.getStatus()));

        if (entry instanceof Anime) {
            Anime anime = (Anime) entry;
            card.add(new JLabel("Genre: " + anime.getDisplayGenre()));
            card.add(new JLabel("Episodes: " + anime.getTotalEpisodes()));

            if (showEpisodes) {
                card.add(Box.createVerticalStrut(10));

                JPanel epTablePanel = new JPanel();
                epTablePanel.setLayout(new BoxLayout(epTablePanel, BoxLayout.Y_AXIS));
                epTablePanel.setBackground(Color.WHITE);

                for (Episode ep : anime.getEpisodes()) {
                    JPanel row = new JPanel(new BorderLayout());
                    row.setBackground(Color.WHITE);
                    row.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            BorderFactory.createEmptyBorder(6, 8, 6, 8)
                    ));

                    JLabel epLabel = new JLabel("Episode " + ep.getEpisodeNumber());
                    epLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    epLabel.setForeground(TEXT);

                    JLabel epStatusLabel = new JLabel(ep.getStatus().toString());
                    epStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    epStatusLabel.setForeground(TEXT);

                    row.add(epLabel, BorderLayout.WEST);
                    row.add(epStatusLabel, BorderLayout.EAST);
                    epTablePanel.add(row);
                }

                JScrollPane epTableScroll = new JScrollPane(epTablePanel);
                epTableScroll.setPreferredSize(new Dimension(550, 220));
                epTableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
                epTableScroll.getVerticalScrollBar().setUnitIncrement(12);

                card.add(epTableScroll);
            }

        } else if (entry instanceof Movie) {
            Movie movie = (Movie) entry;
            card.add(new JLabel("Genre: " + movie.getDisplayGenre()));
            card.add(new JLabel("Duration: " + movie.getDuration() + " mins"));
        } else if (entry instanceof Album) {
            Album album = (Album) entry;
            card.add(new JLabel("Artist: " + album.getArtist()));
            card.add(new JLabel("Music Genre: " + album.getDisplayMusicGenre()));
        }

        if (entry.getStatus() == Status.COMPLETED) {
            card.add(new JLabel("Rating: " + entry.getDisplayRating()));
            card.add(new JLabel("Review: " + entry.getDisplayReview()));
        }

        return card;
    }

    /**
     * Initiates adding a new media entry.
     */
    private void handleAddEntry() {
        showEntryForm(null);
    }

    /**
     * Displays the search entry screen.
     */
    private void searchEntry() {
        getContentPane().removeAll();

        JPanel main = new JPanel(new BorderLayout(20,20));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("Search Entry", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);

        JPanel top = new JPanel(new FlowLayout());
        top.setBackground(BG);

        JTextField searchField = (JTextField) createInputField(false);
        searchField.setPreferredSize(new Dimension(220,35));

        JButton searchButton = createMenuButton("Search");
        JButton backButton = createMenuButton("Back");

        top.add(searchField);
        top.add(searchButton);
        top.add(backButton);

        JPanel north = new JPanel();
        north.setLayout(new BorderLayout());

        north.add(title, BorderLayout.NORTH);
        north.add(top, BorderLayout.SOUTH);
        main.add(north, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(BG);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setBorder(null);

        main.add(scroll, BorderLayout.CENTER);

        add(main);
        revalidate();
        repaint();

        searchButton.addActionListener(e -> {
            resultPanel.removeAll();

            String search = searchField.getText().trim();

            MediaEntry entry = controller.searchEntry(search);

            if (entry == null) {
                JLabel none = new JLabel("Entry not found.");
                none.setFont(new Font("Segoe UI", Font.BOLD, 18));
                none.setAlignmentX(Component.CENTER_ALIGNMENT);

                resultPanel.add(Box.createVerticalGlue());
                resultPanel.add(none);
                resultPanel.add(Box.createVerticalGlue());
            } else {
                JPanel card = createEntryCard(entry, true);

                JPanel buttons = new JPanel(new FlowLayout());
                buttons.setBackground(BG);

                JButton editButton = createMenuButton("✏ Edit");
                JButton deleteButton = createMenuButton("🗑 Remove");

                buttons.add(editButton);
                buttons.add(deleteButton);
                buttons.add(backButton);

                resultPanel.add(card);
                resultPanel.add(Box.createVerticalStrut(15));
                resultPanel.add(buttons);

                editButton.addActionListener(ev -> editEntry(entry));
                
                deleteButton.addActionListener(ev -> {
                    int choice = JOptionPane.showConfirmDialog(null, card, "Delete this entry?", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        controller.removeEntry(entry);
                        JOptionPane.showMessageDialog(this, "Entry removed successfully!");
                        buildMainMenu();
                    }
                });
            }

            resultPanel.revalidate();
            resultPanel.repaint();
        });

        backButton.addActionListener(e -> buildMainMenu());
    }

    /**
     * Navigates to the entry form in editing mode for a specific media entry.
     *
     * @param entry the Media Entry to edit
     */
    private void editEntry(MediaEntry entry) {
        showEntryForm(entry);
    }

    /**
     * Interface for creating a new media entry or editing an existing one.
     * 
     * @param entry target MediaEntry to edit, or null if creating a new entry
     */
    private void showEntryForm(MediaEntry entry) {
        boolean editing = (entry != null);

        getContentPane().removeAll();

        JPanel main = new JPanel(new BorderLayout(20,20));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titleLabel = new JLabel(editing ? "✏ Edit Entry" : "➕ Add Entry", SwingConstants.CENTER);

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT);

        main.add(titleLabel, BorderLayout.NORTH);

        JTextField titleField = (JTextField) createInputField(false);
        JComboBox<Status> statusBox = new JComboBox<>(Status.values());
        
        String[] types = {"Anime", "Movie", "Album"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JTextField ratingField = (JTextField) createInputField(false);
        JTextField reviewField = (JTextField) createInputField(false);

        JComboBox<Genre> genreBox = new JComboBox<>(Genre.values());
        JComboBox<MusicGenre> musicGenreBox = new JComboBox<>(MusicGenre.values());
        JTextField durationField = (JTextField) createInputField(false);
        JTextField artistField = (JTextField) createInputField(false);
        JTextField totalEpField = (JTextField) createInputField(false);
        JTextField currentEpField = (JTextField) createInputField(false);

        ArrayList<JComboBox<Status>> epStatusBoxes = new ArrayList<>();

        if (editing) {
            titleField.setText(entry.getTitle());
            statusBox.setSelectedItem(entry.getStatus());
            typeBox.setSelectedItem(entry.getMediaType());
            typeBox.setEnabled(false);
        
            if (entry.getStatus() == Status.COMPLETED) {
                ratingField.setText(String.valueOf(entry.getRating()));
                reviewField.setText(entry.getReview());
            }

            if (entry instanceof Anime) {
                Anime anime = (Anime) entry;

                genreBox.setSelectedItem(anime.getGenre());
                totalEpField.setText(String.valueOf(anime.getTotalEpisodes()));

                int completed = 0;
                for (Episode ep : anime.getEpisodes()) {
                    if (ep.getStatus() == Status.COMPLETED)
                        completed++;
                }

                currentEpField.setText(String.valueOf(completed));
            } else if (entry instanceof Movie) {
                Movie movie = (Movie) entry;

                genreBox.setSelectedItem(movie.getGenre());
                durationField.setText(String.valueOf(movie.getDuration()));
            } else {
                Album album = (Album) entry;

                artistField.setText(album.getArtist());
                musicGenreBox.setSelectedItem(album.getMusicGenre());
            }
        }

        JPanel dynamicPanel = new JPanel();
        dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
        dynamicPanel.setOpaque(false);

        Runnable updateFormFields = () -> {
            dynamicPanel.removeAll();
            epStatusBoxes.clear();

            String selectedType = (String) typeBox.getSelectedItem();
            Status selectedStatus = (Status) statusBox.getSelectedItem();

            if ("Anime".equals(selectedType)) {
                dynamicPanel.add(createStyledLabel("Genre:"));
                dynamicPanel.add(genreBox);
                dynamicPanel.add(Box.createVerticalStrut(5));
                dynamicPanel.add(createStyledLabel("Total Episodes:"));
                dynamicPanel.add(totalEpField);
                dynamicPanel.add(Box.createVerticalStrut(8));

                if (editing && entry instanceof Anime) {
                    Anime anime = (Anime) entry;
                    dynamicPanel.add(createStyledLabel("Manage Individual Episodes:"));
                    dynamicPanel.add(Box.createVerticalStrut(6));

                    for (Episode ep : anime.getEpisodes()) {
                        JPanel epRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
                        epRow.setOpaque(false);

                        JLabel epLabel = createStyledLabel("Ep " + ep.getEpisodeNumber() + ":");

                        JComboBox<Status> epBox = new JComboBox<>(Status.values());
                        epBox.setSelectedItem(ep.getStatus());
                        epBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        epStatusBoxes.add(epBox);

                        epRow.add(epLabel);
                        epRow.add(epBox);
                        dynamicPanel.add(epRow);
                    }
                    dynamicPanel.add(Box.createVerticalStrut(8));
                }
            } else if ("Movie".equals(selectedType)) {
                dynamicPanel.add(createStyledLabel("Genre:"));
                dynamicPanel.add(genreBox);
                dynamicPanel.add(Box.createVerticalStrut(5));
                dynamicPanel.add(createStyledLabel("Duration (minutes):"));
                dynamicPanel.add(durationField);
                dynamicPanel.add(Box.createVerticalStrut(5));
            } else if ("Album".equals(selectedType)) {
                dynamicPanel.add(createStyledLabel("Artist Name:"));
                dynamicPanel.add(artistField);
                dynamicPanel.add(Box.createVerticalStrut(5));
                dynamicPanel.add(createStyledLabel("Music Genre:"));
                dynamicPanel.add(musicGenreBox);
                dynamicPanel.add(Box.createVerticalStrut(5));
            }

            if (selectedStatus == Status.INPROGRESS) {
                if ("Anime".equals(selectedType) && !editing) {
                    dynamicPanel.add(createStyledLabel("Current Episode Watched:"));
                    dynamicPanel.add(currentEpField);
                    dynamicPanel.add(Box.createVerticalStrut(5));
                }
            } else if (selectedStatus == Status.COMPLETED) {
                dynamicPanel.add(createStyledLabel("Rating (1-10):"));
                dynamicPanel.add(ratingField);
                dynamicPanel.add(Box.createVerticalStrut(5));
                dynamicPanel.add(createStyledLabel("Review:"));
                dynamicPanel.add(reviewField);
                dynamicPanel.add(Box.createVerticalStrut(5));
            }

            dynamicPanel.revalidate();
            dynamicPanel.repaint();
        };

        typeBox.addActionListener(e -> updateFormFields.run());
        statusBox.addActionListener(e -> updateFormFields.run());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        formPanel.add(createStyledLabel("Media Type:"));
        formPanel.add(typeBox);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(createStyledLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(createStyledLabel("Status:"));
        formPanel.add(statusBox);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(dynamicPanel);

        updateFormFields.run();

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG);

        main.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG);

        JButton saveButton = createMenuButton(editing ? "💾 Save Changes" : "➕ Add Entry");
        JButton cancelButton = createMenuButton("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        main.add(buttonPanel, BorderLayout.SOUTH);

        add(main);
        revalidate();
        repaint();

        cancelButton.addActionListener(e -> {
            if (editing)
                searchEntry();
            else 
                buildMainMenu();
        });

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            Status status = (Status) statusBox.getSelectedItem();
            int rating = -1;
            String review = "";

            if (status == Status.COMPLETED) {
                try {
                    rating = Integer.parseInt(ratingField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Rating must be a valid number between 1 and 10!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                review = reviewField.getText().trim();
            }

            String selectedType = (String) typeBox.getSelectedItem();
            String error = null;

            try {
                if (!editing) {
                    if ("Anime".equals(selectedType)) {
                        int totalEp = Integer.parseInt(totalEpField.getText().trim());
                        int curEp = (status == Status.INPROGRESS && !currentEpField.getText().trim().isEmpty()) 
                                    ? Integer.parseInt(currentEpField.getText().trim()) : 0;
                        error = controller.addAnime(title, status, rating, review, totalEp, (Genre) genreBox.getSelectedItem(), curEp);
                    } else if ("Movie".equals(selectedType)) {
                        int duration = Integer.parseInt(durationField.getText().trim());
                        error = controller.addMovie(title, status, rating, review, duration, (Genre) genreBox.getSelectedItem());
                    } else if ("Album".equals(selectedType)) {
                        String artist = artistField.getText().trim();
                        error = controller.addAlbum(title, status, rating, review, artist, (MusicGenre) musicGenreBox.getSelectedItem());
                    }
                } else {
                    error = controller.updateTitle(entry, title);

                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error);
                        return;
                    }

                    if ("Anime".equals(selectedType)) {
                        Anime anime = (Anime) entry;
                        int curEp = (status == Status.INPROGRESS && !currentEpField.getText().trim().isEmpty()) 
                                    ? Integer.parseInt(currentEpField.getText().trim()) : 0;
                        controller.updateStatus(anime, status, curEp);
                        controller.updateGenre(anime, genreBox.getSelectedItem());
                        error = controller.updateTotalEpisodes(anime, Integer.parseInt(totalEpField.getText().trim()));

                        for (int i = 0; i < epStatusBoxes.size(); i++) {
                            Status newEpStatus = (Status) epStatusBoxes.get(i).getSelectedItem();
                            controller.updateEpisodeStatus(anime, i + 1, newEpStatus);
                        }

                    } else if ("Movie".equals(selectedType)) {
                        Movie movie = (Movie) entry;
                        controller.updateStatus(movie, status, 0);
                        controller.updateGenre(movie, genreBox.getSelectedItem());
                        error = controller.updateDuration(movie, Integer.parseInt(durationField.getText().trim()));
                    } else if ("Album".equals(selectedType)) {
                        Album album = (Album) entry;
                        controller.updateStatus(album, status, 0);
                        controller.updateGenre(album, musicGenreBox.getSelectedItem());
                        error = controller.updateArtist(album, artistField.getText().trim());
                    }

                    if (error != null) {
                        JOptionPane.showMessageDialog(this, error);
                        return;
                    }

                    if (status == Status.COMPLETED) {
                        error = controller.updateRating(entry, rating);
                        if (error != null) {
                            JOptionPane.showMessageDialog(this, error);
                            return;
                        }

                        error = controller.updateReview(entry, review);
                        if (error != null) {
                            JOptionPane.showMessageDialog(this, error);
                            return;
                        }
                    }
                    controller.saveProgress();
                }

                if (error != null) {
                    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, editing ? "Entry updated successfully!" : "Entry added successfully!");
                    buildMainMenu();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numerical values for episode or duration numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Builds and displays the Library Summary screen.
     */
    private void showLibrarySummary() {
        getContentPane().removeAll();

        setTitle("Media Vault - Summary");

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BG);
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("📊 Library Summary", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT);

        main.add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel overviewCard = createSummaryCard("OVERVIEW");
        overviewCard.add(createSummaryRow("Total Media Entries:", String.valueOf(controller.getTotalEntriesCount())));
        overviewCard.add(createSummaryRow("Average Rating (Completed):", controller.getAverageRatingFormatted()));

        JPanel statusCard = createSummaryCard("STATUSES");
        statusCard.add(createSummaryRow("Planned:", String.valueOf(controller.getCountByStatus(Status.PLANNED))));
        statusCard.add(createSummaryRow("In Progress:", String.valueOf(controller.getCountByStatus(Status.INPROGRESS))));
        statusCard.add(createSummaryRow("Completed:", String.valueOf(controller.getCountByStatus(Status.COMPLETED))));

        JPanel mediaCard = createSummaryCard("MEDIA TYPES");
        mediaCard.add(createSummaryRow("Anime:", String.valueOf(controller.getCountByMediaType("Anime"))));
        mediaCard.add(createSummaryRow("Movies:", String.valueOf(controller.getCountByMediaType("Movie"))));
        mediaCard.add(createSummaryRow("Albums:", String.valueOf(controller.getCountByMediaType("Album"))));

        contentPanel.add(overviewCard);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(statusCard);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(mediaCard);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        main.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);

        JButton backButton = createMenuButton("← Back to Menu");
        backButton.addActionListener(e -> buildMainMenu());
        bottomPanel.add(backButton);

        main.add(bottomPanel, BorderLayout.SOUTH);

        add(main);
        revalidate();
        repaint();
    }

    // HELPERS

    /**
     * Creates a styled label component formatted for form field headers.
     *
     * @param text text content for the label
     * @return styled JLabel instance
     */
    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT);
        return lbl;
    }

    /**
     * Creates a styled text field or password field component.
     *
     * @param isPassword if true, returns JPasswordField; otherwise returns JTextField
     * @return styled text input field component
     */
    private JTextField createInputField(boolean isPassword) {
        JTextField tf = isPassword ? new JPasswordField() : new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 205, 220), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    /**
     * Creates a styled button component.
     *
     * @param text button display text
     * @return styled JButton instance
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(125, 102, 196));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Creates a white background card container with gray borders and specified padding.
     *
     * @param top top inset border padding
     * @param left left inset border padding
     * @param bottom bottom inset border padding
     * @param right right inset border padding
     * @return formatted JPanel card wrapper
     */
    private JPanel createStyledCard(int top, int left, int bottom, int right) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        ));
        return card;
    }

    /**
     * Creates a styled summary overview card.
     *
     * @param headerTitle section header text
     * @return formatted summary card JPanel
     */
    private JPanel createSummaryCard(String headerTitle) {
        JPanel card = createStyledCard(12, 16, 12, 16);
        card.setAlignmentX(Component.CENTER_ALIGNMENT); 

        JLabel header = new JLabel(headerTitle);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(new Color(125, 102, 196));
        header.setAlignmentX(Component.LEFT_ALIGNMENT); 

        card.add(header);
        card.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);

        card.add(Box.createVerticalStrut(8));
        return card;
    }

    /**
     * Creates a horizontal summary row panel.
     *
     * @param labelText label for the statistic
     * @param valueText calculated numerical value
     * @return formatted single-row JPanel
     */
    private JPanel createSummaryRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT); 
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 13));
        value.setForeground(TEXT);

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);

        return row;
    }
}
