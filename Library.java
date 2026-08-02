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

    public ArrayList<MediaEntry> getEntries() {
        return entries;
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
    }

    /**
     * Removes a media entry from the library.
     *
     * @param entry the MediaEntry to remove
     */
    public void removeEntry(MediaEntry entry) {
        entries.remove(entry);
    }
}
