import java.io.Serializable;

/**
 * Represents a single episode entry of an Anime.
 *
 * Relationships:
 * - Contained by the Anime class via aggregation.
 */
public class Episode implements Serializable {
    private static final long serialVersionUID = 1L;

    private int episodeNumber;
    private Status status;

    /**
     * Constructs for the Episode with a given episode number 
     * and initializes the default status to PLANNED.
     *
     * @param episodeNumber the number of the episode
     */
    public Episode(int episodeNumber) {
        this.episodeNumber = episodeNumber;
        this.status = Status.PLANNED;
    }

    /**
     * Returns the episode number.
     *
     * @return the episode number
     */
    public int getEpisodeNumber() {
        return episodeNumber;
    }

    /**
     * Returns the status of the current episode.
     *
     * @return the status of the episode
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Updates the status of the episode.
     *
     * @param status the new Status enum value
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
