import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        MediaInput media = new MediaInput();
        
        UserInput account = new UserInput();
        User activeAccount = account.handleAuthentication(scanner);

        boolean running = true;
        while (running && activeAccount != null){
            System.out.println("\n===================================");
            System.out.println("     MAIN MENU - " + activeAccount.getUsername().toUpperCase());
            System.out.println("===================================");
            System.out.println("[1] View My Library");
            System.out.println("[2] Add Media Entry");
            System.out.println("[3] Search for an Existing Entry");
            System.out.println("[4] View Library Summary");
            System.out.println("[0] Save and Exit");
            System.out.print(">> ");
            int mainChoice = InputChecker.getValidInput(scanner,0,4);
            System.out.println("");

            switch (mainChoice){
                case 1:
                    System.out.println("View Library By:");
                    System.out.println("[1] All Entries");
                    System.out.println("[2] Status");
                    System.out.println("[3] Media Type");
                    System.out.println("[4] Genre Type");
                    System.out.print(">> ");
                    int viewChoice = InputChecker.getValidInput(scanner,1,4);
                    System.out.println();

                    switch (viewChoice) {
                        case 1:
                            activeAccount.getLibrary().displayEntries();
                            break;
                        case 2:
                            System.out.println("Choose Status:");
                            System.out.println("[1] Planned");
                            System.out.println("[2] In Progress");
                            System.out.println("[3] Completed");
                            System.out.print(">> ");
                            int statusChoice = InputChecker.getValidInput(scanner,1,3);

                            switch (statusChoice){
                                case 1: 
                                    activeAccount.getLibrary().displayEntriesByStatus(Status.PLANNED);
                                    break;
                                case 2: 
                                    activeAccount.getLibrary().displayEntriesByStatus(Status.INPROGRESS);
                                    break;
                                case 3: 
                                    activeAccount.getLibrary().displayEntriesByStatus(Status.COMPLETED);
                                    break;  
                                default:
                                    System.out.println("Error: Invalid Option");
                            }
                            break;
                        case 3:
                            System.out.println("Choose Media Type:");
                            System.out.println("[1] Anime");
                            System.out.println("[2] Movie");
                            System.out.println("[3] Album");
                            System.out.print(">> ");
                            int typeChoice = InputChecker.getValidInput(scanner,1,3);

                            switch (typeChoice) {
                                case 1: 
                                    activeAccount.getLibrary().displayEntriesByMediaType("Anime");
                                    break;
                                case 2:
                                    activeAccount.getLibrary().displayEntriesByMediaType("Movie");
                                    break;
                                case 3:
                                    activeAccount.getLibrary().displayEntriesByMediaType("Album");
                                    break;
                                default:
                                    System.out.println("Error: Invalid Option");
                            }
                            break;
                        case 4:
                            System.out.println("Filter by:");
                            System.out.println("[1] Anime/Movie Genre");
                            System.out.println("[2] Album Genre");
                            System.out.print(">> ");
                            int genreChoice = InputChecker.getValidInput(scanner, 1, 2);
                            System.out.println();

                            if (genreChoice == 1) {
                                Genre selectedGenre = media.selectGenre();
                                activeAccount.getLibrary().displayEntriesByGenre(selectedGenre);
                            } else {
                                MusicGenre selectedMusicGenre = media.selectMusicGenre();
                                activeAccount.getLibrary().displayEntriesByMusicGenre(selectedMusicGenre);
                            }
                            break;
                    }
                    break;
                    
                case 2:
                    System.out.println("Choose which Media to add:");
                    System.out.println("[1] Anime");
                    System.out.println("[2] Movie");
                    System.out.println("[3] Album");
                    System.out.print(">> ");
                    int mediaChoice = InputChecker.getValidInput(scanner,1,3);
                    System.out.println();
                    MediaEntry newEntry = media.createEntry(mediaChoice, activeAccount.getLibrary());
                    if (newEntry != null) {
                        activeAccount.getLibrary().addEntry(newEntry);
                    }
                    break;
                    
                case 3:
                    System.out.println("Enter the Title of the Entry");
                    System.out.print(">> ");
                    String title = scanner.nextLine();

                    MediaEntry entry = activeAccount.getLibrary().getEntry(title);

                    if (entry == null) 
                        System.out.println("Entry not found.");
                    else {
                        System.out.println();
                        System.out.println("-----------------------------------");
                        activeAccount.getLibrary().retrieveEntry(entry);
                        System.out.println("-----------------------------------");
                        System.out.println();
                        System.out.println("[1] Edit Entry");
                        System.out.println("[2] Remove Entry");
                        System.out.println("[3] Go Back to Menu");
                        System.out.print(">> ");
                        int editOption = InputChecker.getValidInput(scanner, 1, 3);

                        switch(editOption) {
                            case 1: media.editEntry(entry, activeAccount.getLibrary()); break;
                            case 2: activeAccount.getLibrary().removeEntry(entry); break;
                            case 3: System.out.println("Returning to Main Menu . . ."); break;
                        }
                    }
                    break;

                case 4:
                    activeAccount.getLibrary().displaySummary();
                    break;
                    
                case 0:
                    SaveData.saveAccount(activeAccount);
                    System.out.println("Exiting Application...");
                    running = false;
                    break;
                    
                default:
                    System.out.println("Error: Invalid Action");
            }
        }
        scanner.close();
    }
}
