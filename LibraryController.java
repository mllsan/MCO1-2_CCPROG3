import java.util.ArrayList;

public class LibraryController {
    private User activeUser;

    public LibraryController(User activeUser) {
        this.activeUser = activeUser;
    }

    public User getActiveUser() {
        return activeUser;
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Title cannot be empty!";
        }
        if (activeUser.getLibrary().getEntry(title.trim()) != null) {
            return "An entry with title '" + title.trim() + "' already exists!";
        }
        return null;
    }

    public String addAnime(String title, Status status, int rating, String review, int totalEpisodes, Genre genre, int currentEp) {
        String titleError = validateTitle(title);
        if (titleError != null){
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

    public String addMovie(String title, Status status, int rating, String review, int duration, Genre genre) {
        String titleError = validateTitle(title);
        if (titleError != null){
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

    public MediaEntry searchEntry(String title) {
        if (title == null || title.trim().isEmpty()){ 
            return null;
        }
        return activeUser.getLibrary().getEntry(title.trim());
    }

    public String updateTitle(MediaEntry entry, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()){
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

    public String updateRating(MediaEntry entry, int rating) {
        if (entry.getStatus() != Status.COMPLETED){
            return "Only completed entries can be rated!";
        }
        if (rating < 1 || rating > 10){
            return "Rating must be between 1 and 10!";
        }
        entry.setRating(rating);
        SaveData.saveAccount(activeUser);
        return null;
    }

    public String updateReview(MediaEntry entry, String review) {
        if (entry.getStatus() != Status.COMPLETED){
            return "Only completed entries can be reviewed!";
        }
        entry.setReview(review != null ? review.trim() : "");
        SaveData.saveAccount(activeUser);
        return null;
    }

    public void updateGenre(MediaEntry entry, Object genre) {
        if (entry instanceof Album && genre instanceof MusicGenre) {
            ((Album) entry).setMusicGenre((MusicGenre) genre);
        } else if (genre instanceof Genre) {
            entry.setGenre((Genre) genre);
        }
        SaveData.saveAccount(activeUser);
    }

    public String updateTotalEpisodes(Anime anime, int newTotal) {
        if (newTotal <= 0) {
            return "Total episodes must be greater than 0!";
        }
        anime.setTotalEpisodes(newTotal);
        SaveData.saveAccount(activeUser);
        return null;
    }

    public String updateDuration(Movie movie, int newDuration) {
        if (newDuration <= 0) {
            return "Duration must be greater than 0!";
        }
        movie.setDuration(newDuration);
        SaveData.saveAccount(activeUser);
        return null;
    }

    public String updateArtist(Album album, String newArtist) {
        if (newArtist == null || newArtist.trim().isEmpty()) {
            return "Artist name cannot be empty!";
        }
        album.setArtist(newArtist.trim());
        SaveData.saveAccount(activeUser);
        return null;
    }

    public boolean updateEpisodeStatus(Anime anime, int epNum, Status newStatus) {
        Episode ep = anime.getEpisode(epNum);
        if (ep != null) {
            ep.setStatus(newStatus);
            SaveData.saveAccount(activeUser);
            return true;
        }
        return false;
    }

    public boolean removeEntry(MediaEntry entry) {
        if (entry == null) {
            return false;
        }
        activeUser.getLibrary().removeEntry(entry);
        SaveData.saveAccount(activeUser);
        return true;
    }

    public void saveProgress() {
        SaveData.saveAccount(activeUser);
    }

    public ArrayList<MediaEntry> getAllEntries() {
        return activeUser.getLibrary().getEntries();
    }

    public ArrayList<MediaEntry> getFilteredEntries(String mediaType, Status status) {
        ArrayList<MediaEntry> filtered = new ArrayList<>();

        for (MediaEntry entry : activeUser.getLibrary().getEntries()) {
            boolean mediaMatch = mediaType.equals("ALL") || entry.getMediaType().equalsIgnoreCase(mediaType);
            boolean statusMatch = status == null || entry.getStatus() == status;

            if (mediaMatch && statusMatch) {
                filtered.add(entry);
            }
        }

        return filtered;
    }
}