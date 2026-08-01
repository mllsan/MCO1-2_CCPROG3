import java.util.ArrayList;

/**
 * Represents a user's media library that stores and manages media entries.
 *
 * Relationships:
 * - Has a collection of MediaEntry objects using an ArrayList.
 * - Manages MediaEntry objects and its subclasses.
 * - Is serializable, allowing the library and its contents to be saved and loaded.
 */
public class Library implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<MediaEntry> entries;

    /**
     * Constructs an empty Library.
     * Initializes the collection of media entries.
     */
    public Library() {
        entries = new ArrayList<>();
    }

    /**
     * Searches for a media entry by its title.
     *
     * @param title the title of the media entry to search for
     * @return the matching MediaEntry if found; otherwise, null
     */
    public MediaEntry getEntry(String title) {
        for(MediaEntry entry : entries) {
            if(entry.getTitle().equalsIgnoreCase(title)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Adds a media entry to the library.
     *
     * @param entry the MediaEntry to add
     */
    public void addEntry(MediaEntry entry) {
        entries.add(entry);
        System.out.println("Entry added successfully!");
    }

    /**
     * Removes a media entry from the library.
     *
     * @param entry the MediaEntry to remove
     */
    public void removeEntry(MediaEntry entry) {
        entries.remove(entry);
        System.out.println("Entry removed successfully!");
    }

    /**
     * Displays the details of a specific media entry.
     *
     * @param entry the MediaEntry to retrieve and display
     */
    public void retrieveEntry(MediaEntry entry) {
        displayEntry(entry, true, true, true);
    }

    /**
     * Displays all media entries stored in the library.
     * However, if the library is empty, a message is displayed.
     */
    public void displayEntries() {
        System.out.println("\n=========== ALL ENTRIES ===========");

        if(entries.isEmpty()) {
            System.out.println("Library is empty.");
        } else {
            for(MediaEntry entry : entries) {
                displayEntry(entry, true, true, false);
                System.out.println("-----------------------------------");
            }
        }
    }

    /**
     * Displays all media entries with the specified status.
     *
     * @param status the status used to filter media entries
     */
    public void displayEntriesByStatus(Status status) {
        boolean found = false;

        System.out.println("\n======== " + status + " ENTRIES =======");

        if(entries.isEmpty()) {
            System.out.println("Library is empty.");
        } else {
            for(MediaEntry entry : entries) {
                if(entry.getStatus() == status) {
                    found = true;
                    displayEntry(entry, false, true, false);
                    System.out.println("-----------------------------------");
                }
            }

            if(!found) {
                System.out.println("No " + status.toString().toLowerCase() + " entries found.");
                System.out.println("-----------------------------------");
            }
        }
    }

    /**
     * Displays all media entries of the specified media type.
     *
     * @param type the media type used to filter entries
     */
    public void displayEntriesByMediaType(String type) {
        boolean found = false;

        System.out.println("\n========= " + type.toUpperCase() + " ENTRIES ========");
        if(entries.isEmpty()) {
            System.out.println("Library is empty.");
        } else {
            for(MediaEntry entry : entries) {
                if(entry.getMediaType().equalsIgnoreCase(type)) {
                    found = true;
                    displayEntry(entry, true, false, false);
                    System.out.println("-----------------------------------");
                }
            }

            if(!found) {
                System.out.println("No " + type + " Entries Found.");
                System.out.println("-----------------------------------");
            }
        }
    }

    /**
     * Displays all media entries of the specific anime/movie genre.
     *
     * @param genre the genre used to filter entries
     */
    public void displayEntriesByGenre(Genre genre) {
        boolean found = false;
        System.out.println("\n========= " + genre + " ENTRIES ========");
        if (entries.isEmpty()) {
            System.out.println("Library is empty.");
        } else {
            for (MediaEntry entry : entries) {
                if (entry.getGenre() == genre) {
                    found = true;
                    displayEntry(entry, true, true, false);
                    System.out.println("-----------------------------------");
                }
            }
            if (!found) {
                System.out.println("No " + genre + " Entries Found.");
                System.out.println("-----------------------------------");
            }
        }
    }

    /**
     * Displays all media entries of the specific music genre.
     *
     * @param musicGenre the genre used to filter entries
     */
    public void displayEntriesByMusicGenre(MusicGenre musicGenre) {
        boolean found = false;
        System.out.println("\n========= " + musicGenre + " ALBUMS ========");
        if (entries.isEmpty()) {
            System.out.println("Library is empty.");
        } else {
            for (MediaEntry entry : entries) {
                if (entry instanceof Album) {
                    Album album = (Album) entry;
                    if (album.getMusicGenre() == musicGenre) {
                        found = true;
                        displayEntry(entry, true, true, false);
                        System.out.println("-----------------------------------");
                    }
                }
            }
            if (!found) {
                System.out.println("No " + musicGenre + " albums found.");
                System.out.println("-----------------------------------");
            }
        }
    }

    /**
     * Displays a summary of the library which includes the total number of entries,
     * number of planned, in-progress, and completed entries,
     * and the user's average rating of completed entries
     */
    public void displaySummary() {
        int planned = 0, inProgress = 0, completed = 0;
        int totalRating = 0, ratedEntries = 0;

        for(MediaEntry entry : entries) {
            switch(entry.getStatus()) {
                case PLANNED:
                    planned++;
                    break;
                case INPROGRESS:
                    inProgress++;
                    break;
                case COMPLETED:
                    completed++;

                    if(entry.getRating() != -1) {
                        ratedEntries++;
                        totalRating += entry.getRating();
                    }
                    break;
            }
        }

        System.out.println("\n========= LIBRARY SUMMARY =========");
        System.out.println("Total Entries: " + entries.size());
        System.out.println("-----------------------------------");
        System.out.println("Planned: " + planned);
        System.out.println("In Progress: " + inProgress);
        System.out.println("Completed: " + completed);
        System.out.println("-----------------------------------");

        if(ratedEntries > 0) {
            double average = (double) totalRating / ratedEntries;
            System.out.printf("Average Rating: %.2f\n", average);
        } else {
            System.out.println("Average Rating: N/A");
        }
    }

    /**
     * Helper method to display entry details.
     *
     * @param entry the MediaEntry to display
     * @param showStatus true to display status
     * @param showMediaType true to display media type
     * @param showEpisodes true to list out individual episodes of Anime
     */
    private void displayEntry(MediaEntry entry, boolean showStatus, boolean showMediaType, boolean showEpisodes) {
        System.out.println("Title: " + entry.getTitle());

        if (showMediaType)
            System.out.println("Media Type: " + entry.getMediaType());

        if (entry instanceof Album) {
            Album album = (Album) entry;
            System.out.println("Artist: " + album.getArtist());
            System.out.println("Music Type: " + album.getDisplayMusicGenre());
        } else if (entry instanceof Anime) {
            Anime anime = (Anime) entry;
            System.out.println("Genre: " + anime.getDisplayGenre());
            System.out.println("Total Episodes: " + anime.getTotalEpisodes());
        } else if (entry instanceof Movie) {
            System.out.println("Genre: " + entry.getDisplayGenre());
            System.out.println("Duration: " + ((Movie) entry).getDuration() + " minutes");
        }
        
        if (showStatus)
            System.out.println("Status: " + entry.getStatus());

        System.out.println("Rating: " + entry.getDisplayRating());
        System.out.println("Review: " + entry.getDisplayReview());

        if (entry instanceof Anime && showEpisodes) {
            System.out.println();
            ((Anime) entry).displayEpisodes();
        }
    }
}
