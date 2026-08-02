import java.util.Scanner;

/**
 * Handles user input for creating and editing media entries.
 *
 * Relationships:
 * - Uses a Scanner object to receive user input.
 * - Interacts with the Library to manage media entries.
 * - Creates and modifies MediaEntry objects and its subclasses.
 * - Used by Main to process user input for media entries.
 */

public class MediaInput {
    private Scanner input = new Scanner(System.in);

    /**
     * Prompts the user to select a status for a media entry.
     *
     * @return the Status selected by the user
     */
    private Status statusChosen(){
        System.out.println("Select Status:");
        System.out.println("[1] Planned");
        System.out.println("[2] In Progress");
        System.out.println("[3] Completed");
        System.out.print(">> ");
        int choice = InputChecker.getValidInput(input, 1, 3);

        switch (choice){
            case 1: return Status.PLANNED;
            case 2: return Status.INPROGRESS;
            case 3: return Status.COMPLETED;
            default: return Status.PLANNED;
        }
    }

    /**
     * Prompts the user to select a genre for the a movie/anime media entry.
     *
     * @return the Genre selected by the user
     */
    public Genre selectGenre() {
        Genre[] genres = Genre.values();
        System.out.println("Select Genre:");
        for (int i = 0; i < genres.length; i++) {
            System.out.println("[" + (i + 1) + "] " + genres[i]);
        }
        System.out.print(">> ");
        int choice = InputChecker.getValidInput(input, 1, genres.length);
        return genres[choice - 1];
    }

    /**
     * Prompts the user to select a genre for a album media entry.
     *
     * @return the selected Genre
     */
    public MusicGenre selectMusicGenre() {
        MusicGenre[] genres = MusicGenre.values();
        System.out.println("Select Genre:");
        for (int i = 0; i < genres.length; i++) {
            System.out.println("[" + (i + 1) + "] " + genres[i]);
        }
        System.out.print(">> ");
        int choice = InputChecker.getValidInput(input, 1, genres.length);
        return genres[choice - 1];
    }

    /**
     * Creates a new media entry based on the user's selected media type.
     * Prompts the user to enter the required information.
     *
     * @param mediaChoice the selected media type
     * @param library the user's library
     * @return the created MediaEntry
     */
    public MediaEntry createEntry(int mediaChoice, Library library){
        int rating = -1, duration = 0, totalEpisodes = 0;
        String review = "", artist = "";
        MediaEntry result = null;
        Genre genre = null;
        MusicGenre mGenre = null;

        System.out.print("Enter Title: ");
        String title = input.nextLine();

        if (library.getEntry(title) != null) {
            System.out.println("Error: An Entry with the Title '" + title + "' Already Exists.");
        } else {
            Status status = statusChosen();

            if (mediaChoice == 1){
                System.out.print("Enter Total Number of Episodes: ");
                totalEpisodes = Integer.parseInt(input.nextLine());
            } else if (mediaChoice == 2){
                System.out.print("Enter Duration (minutes): ");
                duration = Integer.parseInt(input.nextLine());
            } else if (mediaChoice == 3){
                System.out.print("Enter Artist Name: ");
                artist = input.nextLine();
            }

            if (mediaChoice == 3) { 
                mGenre = selectMusicGenre();
                
            } else {
                genre = selectGenre();
            }

            if (status == Status.COMPLETED){
                System.out.print("Enter Rating (1-10): ");
                rating = InputChecker.getValidInput(input, 1, 10);

                System.out.print("Enter Review: ");
                review = input.nextLine();
            }

            if (mediaChoice == 3) { 
                Album album = new Album(title, status, rating, review, artist);
                album.setMusicGenre(mGenre);
                result = album;
            } else {
                if (mediaChoice == 1) {
                    Anime anime = new Anime(title, status, rating, review, totalEpisodes);
                    
                    if (status == Status.COMPLETED) {
                        anime.markAllEpisodesCompleted();
                    } else if (status == Status.INPROGRESS) {
                        System.out.print("Enter Current Episode Watched (1-" + totalEpisodes + "): ");
                        int currentEp = InputChecker.getValidInput(input, 0, totalEpisodes);
                        anime.updateEpisodeStatusesFromProgress(currentEp);
                    } else {
                        anime.markAllEpisodesPlanned();
                    }

                    result = anime;
                } else if (mediaChoice == 2) {
                    result = new Movie(title, status, rating, review, duration);
                }

                if (result != null) {
                    result.setGenre(genre);
                }
            }
        }

        return result;
    }

    /**
     * Prompts the user to pick an episode from a specific Anime entry
     * and updates its viewing status.
     *
     * @param anime the Anime object containing the episodes to be updated
     */
    private void editEpisodeStatus(Anime anime) {
        System.out.print("Enter Episode Number to Edit Status (1-" + anime.getTotalEpisodes() + "): ");
        int epNum = InputChecker.getValidInput(input, 1, anime.getTotalEpisodes());
        Episode ep = anime.getEpisode(epNum);

        System.out.println("\nSelect New Status for Episode " + epNum + ":");
        Status s = statusChosen();
        ep.setStatus(s);
        System.out.println("Episode " + epNum + " Status Updated to " + s + "!");
    }

    /**
     * Allows the user to edit the attributes of an existing media entry.
     * Prompts the user to enter the updated information of the 
     * attributes they wish to edit.
     *
     * @param entry the MediaEntry to edit
     * @param library the user's library
     */
    public void editEntry(MediaEntry entry, Library library) {
        int choice = 0; 
        boolean isAnime = entry.getMediaType().equals("Anime");
        int exitChoice = isAnime ? 8 : 7;

        do {
            System.out.println("\n--- Editing " + entry.getTitle() + " ---");
            System.out.println("[1] Edit Title");
            System.out.println("[2] Edit Status");
            System.out.println("[3] Edit Rating");
            System.out.println("[4] Edit Review");
            System.out.println("[5] Edit Genre");

            if (isAnime) {
                System.out.println("[6] Edit Total Episodes");
                System.out.println("[7] Edit Episode Statuses");
                System.out.println("[8] Finish & Go Back");
                System.out.print(">> ");
                choice = InputChecker.getValidInput(input, 1, 8);
            } else if (entry.getMediaType().equals("Movie") || entry.getMediaType().equals("Album")) {
                String specific = (entry instanceof Movie) ? "Edit Duration" : "Edit Artist Name";
                System.out.println("[6] " + specific);
                System.out.println("[7] Finish & Go Back");
                System.out.print(">> ");
                choice = InputChecker.getValidInput(input, 1, 7);
            }
            System.out.println();

            switch (choice) {
                case 1:
                    System.out.print("Enter New Title: ");
                    String newTitle = input.nextLine();
                    if (library.getEntry(newTitle) != null) {
                        System.out.println("An Entry with that Title Already Exists.");
                    } else {
                        entry.setTitle(newTitle);
                        System.out.println("Title Updated!");
                    }
                    break;
                case 2:
                    Status newStatus = statusChosen();
                    entry.setStatus(newStatus);

                    if (isAnime) {
                        Anime anime = (Anime) entry;
                        if (newStatus == Status.COMPLETED) {
                            anime.markAllEpisodesCompleted();
                        } else if (newStatus == Status.INPROGRESS) {
                            System.out.print("Enter Current Episode Watched (1-" + anime.getTotalEpisodes() + "): ");
                            int currentEp = InputChecker.getValidInput(input, 0, anime.getTotalEpisodes());
                            anime.updateEpisodeStatusesFromProgress(currentEp);
                        } else {
                            anime.markAllEpisodesPlanned();
                        }
                    }

                    if (newStatus == Status.COMPLETED) {
                        System.out.print("Enter Rating (1-10): ");
                        entry.setRating(InputChecker.getValidInput(input, 1, 10));
                        System.out.print("Enter Review: ");
                        entry.setReview(input.nextLine());
                    }

                    System.out.println("Status Updated!");
                    break;
                case 3:
                    if (entry.getStatus() != Status.COMPLETED) {
                        System.out.println("Only Completed Entries can be Rated.");
                    } else {
                        System.out.print("Enter New Rating (1-10): ");
                        entry.setRating(InputChecker.getValidInput(input, 1, 10));
                        System.out.println("Rating Updated!");
                    }
                    break;
                case 4:
                    if (entry.getStatus() != Status.COMPLETED) {
                        System.out.println("Only Completed Entries can be Reviewed.");
                    } else {
                        System.out.print("Enter New Review: ");
                        entry.setReview(input.nextLine());
                        System.out.println("Review Updated!");
                    }
                    break;
                case 5:
                    if (entry instanceof Album) {
                        MusicGenre mg = selectMusicGenre();
                        ((Album) entry).setMusicGenre(mg);
                        System.out.println("Genre Updated!");
                    } else {
                        Genre g = selectGenre();
                        entry.setGenre(g);
                        System.out.println("Genre Updated!");
                    }
                    break;
                case 6:
                    if (entry instanceof Anime) {
                        System.out.print("Enter New Total Episodes: ");
                        ((Anime) entry).setTotalEpisodes(Integer.parseInt(input.nextLine()));
                        System.out.println("Total Episodes Updated!");
                    } else if (entry instanceof Movie) {
                        System.out.print("Enter New Duration (minutes): ");
                        ((Movie) entry).setDuration(Integer.parseInt(input.nextLine()));
                        System.out.println("Duration Updated!");
                    } else if (entry instanceof Album) {
                        System.out.print("Enter New Artist Name: ");
                        ((Album) entry).setArtist(input.nextLine());
                        System.out.println("Artist Updated!");
                    }
                    break;
                case 7:
                    if (isAnime) {
                        editEpisodeStatus((Anime) entry);
                    } else {
                        System.out.println("Returning to Main Menu . . .");
                    }
                    break;
                case 8:
                    if (isAnime) {
                        System.out.println("Returning to Main Menu . . .");
                    } else {
                        System.out.println("Error: Invalid Option");
                    }
                    break;
                default:
                    System.out.println("Error: Invalid Option");
            }
        } while (choice != exitChoice);
    }
}
