import java.util.ArrayList;

/**
 * Represents an anime entry in the media library.
 *
 * Relationships:
 * - Inherits from the MediaEntry superclass.
 * - Stores total episodes and an array of Episode objects.
 */
public class Anime extends MediaEntry {
    private int totalEpisodes;
    private Episode[] episodes;

    /**
     * Constructor for the Anime class.
     * Initializes the common MediaEntry attributes and
     * the anime-specific attributes.
     *
     * @param title the title of the anime
     * @param status the current status
     * @param rating the user's rating
     * @param review the user's review
     * @param totalEpisodes the total number of episodes
     */
    public Anime(String title, Status status, int rating, String review, int totalEpisodes) {
        super(title, status, rating, review);
        this.totalEpisodes = totalEpisodes;
        this.episodes = new Episode[totalEpisodes];

        for (int i = 0; i < totalEpisodes; i++) {
            this.episodes[i] = new Episode(i + 1);
        }
    }

    @Override

    /**
     * Returns the media type of this entry.
     *
     * @return "Anime"
     */
    public String getMediaType() {
        return "Anime";
    }

    /**
     * Returns the total number of episodes.
     *
     * @return the total number of episodes
     */
    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    /**
     * Updates the total number of episodes.
     *
     * @param totalEpisodes the total number of episodes
     */
    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
        Episode[] newEpisodes = new Episode[totalEpisodes];
        
        for (int i = 0; i < totalEpisodes; i++) {
            if (episodes != null && i < episodes.length) {
                newEpisodes[i] = episodes[i];
            } else {
                newEpisodes[i] = new Episode(i + 1);
            }
        }
        this.episodes = newEpisodes;
    }

    /**
     * Retrieves the entire array of Episode objects belonging to this anime.
     *
     * @return array containing all episodes of the anime
     */
    public Episode[] getEpisodes() {
        return episodes;
    }

    /**
     * Retrieves a single Episode object by its index episode number.
     *
     * @param episodeNumber the index episode number to retrieve
     * @return the matching Episode object
     */
    public Episode getEpisode(int episodeNumber) {
        if (episodeNumber >= 1 && episodeNumber <= totalEpisodes) {
            return episodes[episodeNumber - 1];
        }
        return null;
    }

    /**
     * Marks all episodes as COMPLETED.
     */
    public void markAllEpisodesCompleted() {
        for (Episode ep : episodes) {
            ep.setStatus(Status.COMPLETED);
        }
    }

    /**
     * Marks all episodes as PLANNED.
     */
    public void markAllEpisodesPlanned() {
        for (Episode ep : episodes) {
            ep.setStatus(Status.PLANNED);
        }
    }

    /**
     * Marks episodes up to watched Episode as COMPLETED, and the rest as INPROGRESS,
     * used when anime's status is set to ONGOING.
     * 
     * @param currentEpisode the latest episode watched by the user
     */
    public void updateEpisodeStatusesFromProgress(int currentEpisode) {
        for (int i = 0; i < totalEpisodes; i++) {
            if ((i + 1) <= currentEpisode) {
                episodes[i].setStatus(Status.COMPLETED);
            } else {
                episodes[i].setStatus(Status.INPROGRESS);
            }
        }
    }

    /**
     * Displays all episodes and its status.
     */
    public void displayEpisodes() {
        System.out.println("--- Episode Progress ---");
        for (Episode ep : episodes) {
            System.out.println(ep);
        }
    }
}
