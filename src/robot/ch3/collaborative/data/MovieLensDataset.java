package robot.ch3.collaborative.data;

import robot.ch3.collaborative.model.Dataset;
import robot.ch3.collaborative.model.Item;
import robot.ch3.collaborative.model.Rating;
import robot.ch3.collaborative.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Dataset implementation that we will use to work with MovieLens data. 
 * All data is loaded from three files: users, movies (items), and ratings.
 */
public class MovieLensDataset implements Dataset {

    public static final String USERS_FILENAME = "users.dat";
    public static final String ITEMS_FILENAME = "movies.dat";
    public static final String RATINGS_FILENAME = "ratings.dat";
    
    /*
     * Delimiter that is used by MovieLens data files.
     */
    private static final String FIELD_DELIMITER = "::";
    
    /*
     * All item ratings.
     */
    private List<Rating> allRatings = new ArrayList<Rating>();
    
    /*
     * Map of all users.
     */
    private Map<Integer, User> allUsers = new HashMap<Integer, User>();
    
    /*
     * Map of all items.
     */
    private Map<Integer, Item> allItems = new HashMap<Integer, Item>();
    
    /*
     * Parameters for test dataset
     */
    private int numberOfTestRatings = 0;
    private List<Rating> testRatings = new ArrayList<Rating>();
    
    
    /*
     * Map of item ratings by item id.
     */
    private Map<Integer, List<Rating>> ratingsByItemId =  
        new HashMap<Integer, List<Rating>>();
    
    /*
     * Map of item ratings by user id.
     */
    Map<Integer, List<Rating>> ratingsByUserId = 
        new HashMap<Integer, List<Rating>>();

    private String name;
    
    public MovieLensDataset(File users, File movies, File ratings) {
        name = getClass().getSimpleName() + System.currentTimeMillis();
        loadData(users, movies, ratings, null);
    }
    
    public MovieLensDataset(String name, File users,
            File movies, File ratings) {
        
        this.name = name;
        loadData(users, movies, ratings, null);
    }
    
    public MovieLensDataset(String name, File users,
            File items, List<Rating> ratings) {
        
        this.name = name;
        loadData(users, items, null, ratings);
    }
    
    public MovieLensDataset(File users, File movies, File ratings, 
            int numOfTestRatings) {
        name = getClass().getSimpleName() + System.currentTimeMillis();
        this.numberOfTestRatings = numOfTestRatings;
        loadData(users, movies, ratings, null);
    }
    
    public String getName() {
    	return name;
    }

    public double getAverageItemRating(int itemId) {
        return getItem(itemId).getAverageRating();
    }
    
    public double getAverageUserRating(int userId) {
        return getUser(userId).getAverageRating();
    }
    

    private void loadData(File usersFile, File itemsFile, 
            File ratingsFile, List<Rating> ratings) {
        try {
            /* Load all available ratings */
            if( ratings == null) {
                allRatings = loadRatings(ratingsFile);
            }
            else {
                allRatings = ratings;
            }
            
            /* Exclude ratings if needed */
            withholdRatings();
            
            /* build maps that provide access to ratings by userId or itemId */
            for (Rating rating : allRatings) {
                addRatingToMap(ratingsByItemId, rating.getItemId(), rating);
                addRatingToMap(ratingsByUserId, rating.getUserId(), rating);
            }
            /* load users and item. Each instance will have a set of ratings relevant to it */
            allUsers = loadUsers(usersFile);
            allItems = loadItems(itemsFile);
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to load MovieLens data: ", e);
        }
    }
    
    public int getRatingsCount() {
        return allRatings.size();
    }

    public int getUserCount() {
        return allUsers.size();
    }

    public int getItemCount() {
        return allItems.size();
    }

    public Collection<User> getUsers() {
        return allUsers.values();
    }

    public Collection<Item> getItems() {
        return allItems.values();
    }

    public User getUser(Integer userId) {
        return allUsers.get(userId);
    }

    public Item getItem(Integer itemId) {
        return allItems.get(itemId);
    }
    
    private Map<Integer, User> loadUsers(File usersFile) 
            throws IOException {
        Map<Integer, User> users = new HashMap<Integer, User>();
        
        BufferedReader reader = getReader(usersFile);
        String line = null;

        while( (line = reader.readLine()) != null ) {
            String[] tokens = parseLine(line);
            /* at the moment we are only interested in user id */
            int userId = Integer.parseInt(tokens[0]);
            List<Rating> userRatings = ratingsByUserId.get(userId);
            if( userRatings == null ) {
                userRatings = new ArrayList<Rating>();
            }
            User user = new User(userId, userRatings);
            users.put(user.getId(), user);
        }

        return users;
    }

    private Map<Integer, Item> loadItems(File moviesFile) 
            throws IOException {

        Map<Integer, Item> items = new HashMap<Integer, Item>();
        
        BufferedReader reader = getReader(moviesFile);
        String line = null;
        int lastId = 0;
        while( (line = reader.readLine()) != null ) {
            
        	String[] tokens = parseLine(line);
            
            /* at the moment we are only interested in movie id */
            int itemId = Integer.parseInt(tokens[0]);
            String title = tokens[1];
            
            /* In some cases we need to create items for missing ids. 
             * Movies file from MovieLens dataset skips over some ids. 
             * To keep things simple we made assumption that 
             * user and movie (item) ids are sequences without gaps that start with 1. 
             */
            if( itemId > lastId + 1 ) {
                
            	for( int i = lastId + 1; i < itemId; i++) {
//                	System.out.println("DEBUG:\n");
//                	System.out.println("Movies file has a gap in ID sequence. ");
//                	System.out.println("Creating artificial item for ID: " + i);
                	
                    Item missingItem = createNewItem(i,"Missing-Item-"+i);
                    items.put(missingItem.getId(), missingItem);
                }
            }
            
            Item item = createNewItem(itemId, title); 
            
            items.put(item.getId(), item);
            lastId = item.getId();
        }
        return items;
    }

    private static String[] parseLine(String line) {
        // possible field delimiters: "::", "\t", "|"
        return line.split("::|\t|\\|"); 
    }
    
    
    private Item createNewItem(int itemId, String name) {
        List<Rating> ratings = ratingsByItemId.get(itemId);
        if( ratings == null ) {
            ratings = new ArrayList<Rating>();
        }
        
        Item item = new Item(itemId, name, ratings);
        
        // establish link between rating and item
        for(Rating r : ratings) {
            r.setItem(item);
        }
        
        return item;
    }
    
    private void addRatingToMap(Map<Integer, List<Rating>> map,
            Integer key, Rating rating) {
        List<Rating> ratingsForKey = map.get(key);
        if (ratingsForKey == null) {
            ratingsForKey = new ArrayList<Rating>();
            map.put(key, ratingsForKey);
        }
        ratingsForKey.add(rating);
    }
    
    private static BufferedReader getReader(File f) throws FileNotFoundException {
        return new BufferedReader(new FileReader(f));        
    }
    
    public Collection<Rating> getRatings() {
        return this.allRatings;
    }
    
    /**
     * Saves provided ratings into a new file. Used to split ratings provided as part of
     * MovieLens data set into files that represent various rating sets for training and testing.
     * @param f file to write to.
     * @param ratings ratings to save.
     */
    public static void createNewRatingsFile(File f, Collection<Rating> ratings) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            for(Rating rating : ratings ) {
                pw.println(rating.getUserId() + FIELD_DELIMITER + rating.getItemId() + FIELD_DELIMITER + rating.getRating() );
            }
            pw.flush();
            pw.close();
        }
        catch(IOException e) {
            throw new RuntimeException(
                    "Failed to save rating into file (file: '" + 
                    f.getAbsolutePath() + "').", e);
        }
    }
    
    public static List<Rating> loadRatings(File f) {
        List<Rating> allRatings = new ArrayList<Rating>();
        
        BufferedReader reader = null;
        String line = null;
        try {
            reader = getReader(f);
            while( (line = reader.readLine()) != null ) {
                String[] tokens = parseLine(line);
                int userId = Integer.parseInt(tokens[0]);
                int itemId = Integer.parseInt(tokens[1]);
                int rating = Integer.parseInt(tokens[2]);
                allRatings.add(new Rating(userId, itemId, rating));            
            }
        }
        catch(IOException e) {
            throw new RuntimeException(
                    "Failed to load rating from file (file: '" + 
                    f.getAbsolutePath() + "'): ", e);
        }
        finally {
            if( reader != null ) {
                try {
                    reader.close();
                }
                catch(Exception e) {
                	System.out.println("ERROR: \n");
                	System.out.println(e.getMessage()+"\n while closing file reader (file: '" +
                            f.getAbsolutePath() + "'): ");
                }
            }
        }
        
        return allRatings;
    }

    public boolean isIdMappingRequired() {
        return false;
    }

    
    public String[] getAllTerms() {
        return new String[0];
    }
    
    public Collection<Rating> getTestRatings() {
        return this.testRatings;
    }

    public void setTestRatingsCount(int numberOfRatings) {
        this.numberOfTestRatings = numberOfRatings;
    }
    
    private void withholdRatings() {
        Random rnd = new Random();
        while( testRatings.size() < this.numberOfTestRatings ) {
            int randomIndex = rnd.nextInt(allRatings.size());
            Rating rating = allRatings.remove(randomIndex);
            testRatings.add(rating);
        }
    }
    
    
}
