/**
 * Represents an anime entry in the media library.
 *
 * Relationships:
 * - Inherits from the MediaEntry superclass.
 * - Adds episode and season attributes specific to anime.
 */
public class Anime extends MediaEntry {
    private int totalEpisodes;
    private int currentEpisode;
    private int seasonNumber;

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
     * @param currentEpisode the current episode watched
     * @param seasonNumber the season number
     */
    public Anime(String title, Status status, int rating, String review, 
                 int totalEpisodes, int currentEpisode, int seasonNumber) {
        super(title, status, rating, review);
        this.totalEpisodes = totalEpisodes;
        this.currentEpisode = currentEpisode;
        this.seasonNumber = seasonNumber;
    }

    @Override
    public String getMediaType() {
        return "Anime";
    }

    public int getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }
}
