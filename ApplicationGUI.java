import javax.swing.*;
import java.awt.*;

public class ApplicationGUI extends JFrame {
    private LibraryController controller;
    private static final Color BG = new Color(250, 248, 245);
    private static final Color TEXT = new Color(70, 75, 85);

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
        
        JButton refresh = createMenuButton("Refresh");
        JButton back = createMenuButton("← Back");

        filterPanel.add(new JLabel("Status"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(new JLabel("Media"));
        filterPanel.add(mediaFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
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

        // BUTTONS
        refresh.addActionListener(e -> {
            libraryPanel.removeAll();

            String media = (String) mediaFilter.getSelectedItem();
            String statusText = (String) statusFilter.getSelectedItem();

            Status status = null;

            if(!statusText.equals("ALL")) {
                status = Status.valueOf(statusText);
            }

            for (MediaEntry entry : controller.getFilteredEntries(media, status)) {
                libraryPanel.add(createEntryCard(entry));
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

    private JPanel createEntryCard(MediaEntry entry) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBackground(BG);

        card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220,220,220)),
                        BorderFactory.createEmptyBorder(15,15,15,15)));

        JLabel title = new JLabel(entry.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(title);
        card.add(new JLabel(entry.getMediaType()));
        card.add(new JLabel("Status: " + entry.getStatus()));

        if (entry instanceof Anime) {
            Anime anime = (Anime) entry;
            card.add(new JLabel("Genre: " + anime.getDisplayGenre()));
            card.add(new JLabel("Episodes: " + anime.getTotalEpisodes()));
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

    // add entry
    private void handleAddEntry() {
        showEntryForm(null);
    }

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

        JTextField searchField = createStyledTextField();
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
                JPanel card = createEntryCard(entry);

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
                    }
                });
            }

            resultPanel.revalidate();
            resultPanel.repaint();
        });

        backButton.addActionListener(e -> buildMainMenu());
    }

    private void editEntry(MediaEntry entry) {
        showEntryForm(entry);
    }

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

        JTextField titleField = createStyledTextField();
        JComboBox<Status> statusBox = new JComboBox<>(Status.values());
        
        String[] types = {"Anime", "Movie", "Album"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JTextField ratingField = createStyledTextField();
        JTextField reviewField = createStyledTextField();

        JComboBox<Genre> genreBox = new JComboBox<>(Genre.values());
        JComboBox<MusicGenre> musicGenreBox = new JComboBox<>(MusicGenre.values());
        JTextField durationField = createStyledTextField();
        JTextField artistField = createStyledTextField();
        JTextField totalEpField = createStyledTextField();
        JTextField currentEpField = createStyledTextField();

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

        statusBox.addActionListener(e -> {
            boolean isCompleted = statusBox.getSelectedItem() == Status.COMPLETED;
            ratingField.setEnabled(isCompleted);
            reviewField.setEnabled(isCompleted);
            if (!isCompleted) {
                ratingField.setText("");
                reviewField.setText("");
            }
        });

        JPanel specificPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        specificPanel.setOpaque(false);

        Runnable updateSpecificFields = () -> {
            specificPanel.removeAll();
            String selectedType = (String) typeBox.getSelectedItem();

            if ("Anime".equals(selectedType)) {
                specificPanel.add(createStyledLabel("Genre:"));
                specificPanel.add(genreBox);
                specificPanel.add(createStyledLabel("Total Episodes:"));
                specificPanel.add(totalEpField);
                specificPanel.add(createStyledLabel("Current Episode Watched (if In Progress):"));
                specificPanel.add(currentEpField);
            } else if ("Movie".equals(selectedType)) {
                specificPanel.add(createStyledLabel("Genre:"));
                specificPanel.add(genreBox);
                specificPanel.add(createStyledLabel("Duration (minutes):"));
                specificPanel.add(durationField);
            } else if ("Album".equals(selectedType)) {
                specificPanel.add(createStyledLabel("Artist Name:"));
                specificPanel.add(artistField);
                specificPanel.add(createStyledLabel("Music Genre:"));
                specificPanel.add(musicGenreBox);
            }
            specificPanel.revalidate();
            specificPanel.repaint();
        };

        typeBox.addActionListener(e -> updateSpecificFields.run());
        updateSpecificFields.run();

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
        formPanel.add(specificPanel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(createStyledLabel("Rating (1-10) (Completed Only):"));
        formPanel.add(ratingField);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(createStyledLabel("Review (Completed Only):"));
        formPanel.add(reviewField);

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
                if(!editing) {
                    if ("Anime".equals(selectedType)) {
                        int totalEp = Integer.parseInt(totalEpField.getText().trim());
                        int curEp = currentEpField.getText().trim().isEmpty() ? 0 : Integer.parseInt(currentEpField.getText().trim());
                        error = controller.addAnime(title, status, rating, review, totalEp, (Genre) genreBox.getSelectedItem(), curEp);
                    } else if ("Movie".equals(selectedType)) {
                        int duration = Integer.parseInt(durationField.getText().trim());
                        error = controller.addMovie(title, status, rating, review, duration, (Genre) genreBox.getSelectedItem());
                    } else if ("Album".equals(selectedType)) {
                        String artist = artistField.getText().trim();
                        error = controller.addAlbum(title, status, rating, review, artist, (MusicGenre) musicGenreBox.getSelectedItem());
                    }
                } else {
                    error = controller.updateTitle(entry,title);

                    if(error != null) {
                        JOptionPane.showMessageDialog(this,error);
                        return;
                    }

                    if ("Anime".equals(selectedType)) {
                        Anime anime = (Anime) entry;
                        controller.updateStatus(anime, status, Integer.parseInt(currentEpField.getText().trim()));
                        controller.updateGenre(anime, genreBox.getSelectedItem());
                        error = controller.updateTotalEpisodes(anime, Integer.parseInt(totalEpField.getText().trim()));
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
                        if(error != null) {
                            JOptionPane.showMessageDialog(this, error);
                            return;
                        }

                        error = controller.updateReview(entry,review);
                        if(error != null) {
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
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numerical values for episode or duration numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void showLibrarySummary() {
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicationGUI());
    }
}
