/**
 * Represents a generic media entry in the user's library.
 *
 * Relationships:
 * - Abstract superclass of Album, Anime, and Movie.
 * - Stores the attributes and behaviors shared by all media entry types.
 */
public abstract class MediaEntry implements java.io.Serializable {
    private String title;
    private Status status;
    private int rating;
    private String review;
    private Genre genre;

    /**
     * Constructor for the MediaEntry class.
     * Initializes the common attributes shared by all
     * media entries.
     *
     * Ratings and reviews are only stored when the
     * entry is marked as COMPLETED.
     *
     * @param title the title of the media
     * @param status the media status
     * @param rating the user's rating
     * @param review the user's review
     */
    public MediaEntry(String title, Status status, int rating, String review){
        this.title = title;
        this.status = status;
        this.genre = Genre.OTHER;

        // Ratings and reviews only apply to completed entries.
        if(status == Status.COMPLETED) {
            this.rating = rating;
            this.review = review;
        } else {
            this.rating = -1;
            this.review = "";
        }
    }

    /**
     * Returns the specific media type.
     *
     * Implemented by each subclass.
     *
     * @return the media type
     */
    public abstract String getMediaType();

    /**
     * Returns the title.
     *
     * @return the media title
     */
    public String getTitle(){
        return title;
    }

    /**
     * Updates the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        //Ensures that title is not empty
        if (title == null || title.isBlank()) {
        } else {
            this.title = title;
        }
    }

    /**
     * Returns the current status.
     *
     * @return the media status
     */
    public Status getStatus(){
        return status;
    }

    /**
     * Updates the media status.
     *
     * If the new status is not COMPLETED, any existing
     * rating and review are cleared because they are
     * only applicable to completed entries.
     *
     * @param status the new status
     */
    public void setStatus(Status status){
        this.status = status;

        if (status != Status.COMPLETED) {
            rating = -1;
            review = "";
        }
    }

    /**
     * Returns the stored numeric rating.
     *
     * @return the rating
     */
    public int getRating(){
        return rating;
    }

    /**
     * Updates the rating.
     *
     * @param rating the new rating
     */
    public void setRating(int rating){
        // Ratings are only allowed for completed entries.
        if (status == Status.COMPLETED) {
            // Ensures that its within the range of a rating (1-10)
            if (rating < 1 || rating > 10) 
                {}
            else
                this.rating = rating;
        } else {
            this.rating = -1;
        }
    }

    /**
     * Returns a formatted rating for display.
     *
     * @return "-" if no rating exists; otherwise the rating
     */
    public String getDisplayRating() {
        String rate;

        if (rating == -1)
            rate = "-";
        else
            rate = String.valueOf(rating);
        
        return rate;
    }

    /**
     * Returns the review.
     *
     * @return the review
     */
    public String getReview(){
        return review;
    }

    /**
     * Updates the review.
     *
     * @param review the new review
     */
    public void setReview(String review){
        // Reviews are only allowed for completed entries.
        if (status == Status.COMPLETED)
            this.review = review;
        else {
            this.review = "";
        }
    }

    /**
     * Returns a formatted review for display.
     *
     * @return "-" if no review exists; otherwise the review
     */
    public String getDisplayReview() {
        String rev;

        if (review.isBlank())
            rev = "-";
        else
            rev = review;
        
        return rev;
    }

    /**
     * Returns the genre.
     *
     * @return the genre
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * Updates the genre.
     *
     * @param genre the new genre
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * Returns the genre of the entry.
     *
     * @return "-" if no genre exists; otherwise the genre
     */
    public String getDisplayGenre() {
        return (genre == null) ? "-" : genre.toString();
    }
}
