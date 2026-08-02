import java.util.ArrayList;

/**
 * Controller class managing application logic and state mutations for the Media Vault library.
 * 
 * Responsibilities:
 * - Validates inputs and handles creation/updates of Anime, Movie, and Album entries.
 * - Bridges Model data operations with View components (CLI/GUI).
 */
public class LibraryController {
    private User activeUser;

    /**
     * Constructs a LibraryController for the specified active user session.
     *
     * @param activeUser the User currently logged into the system
     */
    public LibraryController(User activeUser) {
        this.activeUser = activeUser;
    }

    /**
     * Retrieves the currently active user profile.
     *
     * @return the active User object
     */
    public User getActiveUser() {
        return activeUser;
    }

    /**
     * Validates an entry title.
     *
     * @param title the media title string to validate
     * @return an error message String if invalid; null if title is valid
     */
    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Title cannot be empty!";
        }
        if (activeUser.getLibrary().getEntry(title.trim()) != null) {
            return "An entry with title '" + title.trim() + "' already exists!";
        }
        return null;
    }

    /**
     * Creates and adds a new Anime entry to the library.
     *
     * @param title title of the anime
     * @param status watch status
     * @param rating user's rating
     * @param review user's review
     * @param totalEpisodes total episode count
     * @param genre anime's genre
     * @param currentEp current episode progress
     * @return error message if validation fails; null on success
     */
    public String addAnime(String title, Status status, int rating, String review, int totalEpisodes, Genre genre, int currentEp) {
        String titleError = validateTitle(title);
        if (titleError != null) {
            return titleError;
        }
        if (totalEpisodes <= 0) {
            return "Total episodes cannot be 0!";
        }

        Anime anime = new Anime(title.trim(), status, rating, review, totalEpisodes);
        anime.setGenre(genre);

        if (status == Status.COMPLETED) {
            anime.markAllEpisodesCompleted();
        } else if (status == Status.INPROGRESS) {
            anime.updateEpisodeStatusesFromProgress(currentEp);
        } else {
            anime.markAllEpisodesPlanned();
        }

        activeUser.getLibrary().addEntry(anime);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Creates and adds a new Movie entry to the library.
     *
     * @param title title of the anime
     * @param status watch status
     * @param rating user's rating
     * @param review user's review
     * @param duration total runtime of movie
     * @param genre movie genre
     * @return error message if validation fails; null on success
     */
    public String addMovie(String title, Status status, int rating, String review, int duration, Genre genre) {
        String titleError = validateTitle(title);
        if (titleError != null) {
            return titleError;
        }
        if (duration <= 0) {
            return "Duration cannot be 0 minutes!";
        }

        Movie movie = new Movie(title.trim(), status, rating, review, duration);
        movie.setGenre(genre);

        activeUser.getLibrary().addEntry(movie);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Creates and adds a new Album entry to the library.
     *
     * @param title title of the anime
     * @param status watch status
     * @param rating user's rating
     * @param review user's review
     * @param artist music artist
     * @param musicGenre music genre
     * @return error message if validation fails; null on success
     */
    public String addAlbum(String title, Status status, int rating, String review, String artist, MusicGenre musicGenre) {
        String titleError = validateTitle(title);
        if (titleError != null) return titleError;

        if (artist == null || artist.trim().isEmpty()) {
            return "Artist name cannot be empty!";
        }

        Album album = new Album(title.trim(), status, rating, review, artist.trim());
        album.setMusicGenre(musicGenre);

        activeUser.getLibrary().addEntry(album);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Searches the user's library for a specific media entry by title.
     *
     * @param title the title to search for
     * @return the MediaEntry if found; otherwise null
     */
    public MediaEntry searchEntry(String title) {
        if (title == null || title.trim().isEmpty()) { 
            return null;
        }
        return activeUser.getLibrary().getEntry(title.trim());
    }

    /**
     * Updates an existing media entry's title.
     *
     * @param entry the media entry to update
     * @param newTitle the new title
     * @return error message if validation fails; null on success
     */
    public String updateTitle(MediaEntry entry, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            return "Title cannot be empty!";
        }
        String trimmed = newTitle.trim();
        if (!trimmed.equalsIgnoreCase(entry.getTitle()) && activeUser.getLibrary().getEntry(trimmed) != null) {
            return "An entry with that title already exists!";
        }
        entry.setTitle(trimmed);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Updates the status of episodes if anime media entry.
     *
     * @param entry the media entry
     * @param newStatus the new status chosen
     * @param currentEp current episode count
     */
    public void updateStatus(MediaEntry entry, Status newStatus, int currentEp) {
        entry.setStatus(newStatus);
        if (entry instanceof Anime) {
            Anime anime = (Anime) entry;
            if (newStatus == Status.COMPLETED) {
                anime.markAllEpisodesCompleted();
            } else if (newStatus == Status.INPROGRESS) {
                anime.updateEpisodeStatusesFromProgress(currentEp);
            } else {
                anime.markAllEpisodesPlanned();
            }
        }
        SaveData.saveAccount(activeUser);
    }

    /**
     * Updates the rating of a completed media entry.
     *
     * @param entry the media entry to update
     * @param rating user's new rating 
     * @return error message if invalid; null if success
     */
    public String updateRating(MediaEntry entry, int rating) {
        if (entry.getStatus() != Status.COMPLETED) {
            return "Only completed entries can be rated!";
        }
        if (rating < 1 || rating > 10) {
            return "Rating must be between 1 and 10!";
        }
        entry.setRating(rating);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Updates the review of a completed media entry.
     *
     * @param entry the media entry to update
     * @param review user's new review
     * @return error message if not completed; null on success
     */
    public String updateReview(MediaEntry entry, String review) {
        if (entry.getStatus() != Status.COMPLETED) {
            return "Only completed entries can be reviewed!";
        }
        entry.setReview(review != null ? review.trim() : "");
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Updates the genre of a media entry depending on its media type.
     *
     * @param entry the media entry to update
     * @param genre user's new genre chosen
     */
    public void updateGenre(MediaEntry entry, Object genre) {
        if (entry instanceof Album && genre instanceof MusicGenre) {
            ((Album) entry).setMusicGenre((MusicGenre) genre);
        } else if (genre instanceof Genre) {
            entry.setGenre((Genre) genre);
        }
        SaveData.saveAccount(activeUser);
    }

    /**
     * Updates the total episode count for an Anime entry.
     *
     * @param anime the Anime entry to update
     * @param newTotal new total episode count
     * @return error message if invalid; null on success
     */
    public String updateTotalEpisodes(Anime anime, int newTotal) {
        if (newTotal <= 0) {
            return "Total episodes must be greater than 0!";
        }
        anime.setTotalEpisodes(newTotal);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Updates the duration of a Movie entry.
     *
     * @param movie the Movie entry to update
     * @param newDuration the new duration
     * @return error message if invalid; null on success
     */
    public String updateDuration(Movie movie, int newDuration) {
        if (newDuration <= 0) {
            return "Duration must be greater than 0!";
        }
        movie.setDuration(newDuration);
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Updates the artist name of an Album entry.
     *
     * @param album the Album entry to update
     * @param newArtist the new artist name
     * @return error message if invalid; null on success
     */
    public String updateArtist(Album album, String newArtist) {
        if (newArtist == null || newArtist.trim().isEmpty()) {
            return "Artist name cannot be empty!";
        }
        album.setArtist(newArtist.trim());
        SaveData.saveAccount(activeUser);
        return null;
    }

    /**
     * Updates the status of an individual episode within an Anime entry.
     *
     * @param anime the Anime entry to update
     * @param epNum the episode number to update
     * @param newStatus new episode Status
     * @return true if episode was found and updated; false otherwise
     */
    public boolean updateEpisodeStatus(Anime anime, int epNum, Status newStatus) {
        Episode ep = anime.getEpisode(epNum);
        if (ep != null) {
            ep.setStatus(newStatus);
            SaveData.saveAccount(activeUser);
            return true;
        }
        return false;
    }

    /**
     * Removes a media entry from the active user's library.
     *
     * @param entry the Media Entry to remove
     * @return true if successfully removed; false if entry is null
     */
    public boolean removeEntry(MediaEntry entry) {
        if (entry == null) {
            return false;
        }
        activeUser.getLibrary().removeEntry(entry);
        SaveData.saveAccount(activeUser);
        return true;
    }

    /**
     * Saves current user library progress to storage.
     */
    public void saveProgress() {
        SaveData.saveAccount(activeUser);
    }

    /**
     * Retrieves all media entries stored in the user's library.
     *
     * @return ArrayList of all MediaEntry objects
     */
    public ArrayList<MediaEntry> getAllEntries() {
        return activeUser.getLibrary().getEntries();
    }

    /**
     * Retrieves a filtered list of media entries matching specified criteria.
     *
     * @param mediaType specific media type filter
     * @param status specific Status filter
     * @param genre specific genre filter
     * @return filtered ArrayList of MediaEntry objects
     */
    public ArrayList<MediaEntry> getFilteredEntries(String mediaType, Status status, String genre) {
        ArrayList<MediaEntry> filtered = new ArrayList<>();

        for (MediaEntry entry : activeUser.getLibrary().getEntries()) {
            boolean mediaMatch = mediaType.equals("ALL") || entry.getMediaType().equalsIgnoreCase(mediaType);
            boolean statusMatch = status == null || entry.getStatus() == status;
            
            boolean genreMatch = true;
            if (!genre.equals("ALL")) {
                if (entry instanceof Album) {
                    genreMatch = ((Album) entry).getMusicGenre().toString().equalsIgnoreCase(genre);
                } else if (entry instanceof Anime) {
                    genreMatch = ((Anime) entry).getGenre().toString().equalsIgnoreCase(genre);
                } else if (entry instanceof Movie) {
                    genreMatch = ((Movie) entry).getGenre().toString().equalsIgnoreCase(genre);
                }
            }

            if (mediaMatch && statusMatch && genreMatch) {
                filtered.add(entry);
            }
        }

        return filtered;
    }

    /**
     * Calculates total number of entries in the user's library.
     *
     * @return total entry count
     */
    public int getTotalEntriesCount() {
        return activeUser.getLibrary().getEntries().size();
    }

    /**
     * Counts how many entries match a specific Status.
     *
     * @param status Status to count
     * @return number of matching entries
     */
    public int getCountByStatus(Status status) {
        int count = 0;
        for (MediaEntry entry : activeUser.getLibrary().getEntries()) {
            if (entry.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts how many entries match a specific media type string.
     *
     * @param mediaType the media type name
     * @return count of matching entries
     */
    public int getCountByMediaType(String mediaType) {
        int count = 0;
        for (MediaEntry entry : activeUser.getLibrary().getEntries()) {
            if (entry.getMediaType().equalsIgnoreCase(mediaType)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the average numerical rating of all completed entries.
     *
     * @return String of average score
     */
    public String getAverageRatingFormatted() {
        int totalRating = 0;
        int ratedEntries = 0;

        for (MediaEntry entry : activeUser.getLibrary().getEntries()) {
            if (entry.getStatus() == Status.COMPLETED && entry.getRating() != -1) {
                ratedEntries++;
                totalRating += entry.getRating();
            }
        }

        if (ratedEntries > 0) {
            double average = (double) totalRating / ratedEntries;
            return String.format("%.2f / 10", average);
        }
        return "N/A";
    }
}
