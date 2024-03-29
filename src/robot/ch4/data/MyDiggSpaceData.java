package robot.ch4.data;

import robot.ch3.collaborative.data.DiggData;
import robot.ch3.collaborative.model.Content;
import robot.ch3.content.digg.DiggStoryItem;
import robot.ch4.clustering.dbscan.DBSCANAlgorithm;
import robot.ch4.clustering.hierarchical.Dendrogram;
import robot.ch4.clustering.rock.ROCKAlgorithm;
import robot.ch4.model.Attribute;
import robot.ch4.model.DataPoint;
import robot.ch4.similarity.CosineDistance;
import robot.ch4.utils.Attributes;

import java.util.List;

public class MyDiggSpaceData {

    public static MyDiggSpaceDataset createDataset() {
        return createDataset(10);
    }

    
    public static MyDiggSpaceDataset createDataset(int topNTerms) {
        DiggData.loadData("C:/robot/data/ch04/ch4_digg_stories.csv");
        
        List<DiggStoryItem> allStories = DiggData.allStories;
        
        DataPoint[] allDataPoints = new DataPoint[allStories.size()];
        
        for(int i = 0, n = allDataPoints.length; i < n; i++) {
            DiggStoryItem story = allStories.get(i);
            DataPoint di = createDataPoint(story, topNTerms);
            allDataPoints[i] = di;
        }
        return new MyDiggSpaceDataset(allDataPoints);
    }
    
    public static MyDiggSpaceDataset createDataset(int topNTerms, List<DiggStoryItem> allStories) {

    	DataPoint[] allDataPoints = new DataPoint[allStories.size()];
        
        for(int i = 0, n = allDataPoints.length; i < n; i++) {
            
        	DiggStoryItem story = allStories.get(i);
            story.print();
            
            DataPoint di = createDataPoint(story, topNTerms);
            allDataPoints[i] = di;
        }
        return new MyDiggSpaceDataset(allDataPoints);
    }

    private static DataPoint createDataPoint(DiggStoryItem story, int topNTerms) {
        String storyLabel = String.valueOf(story.getId() + ":" + story.getTitle());
        String storyText = story.getTitle() + " " + story.getDescription();
        Content content = new Content(storyLabel, storyText, topNTerms);
        String[] terms = content.getTerms();
        // using term as attribute name and value.
        Attribute[] attributes = Attributes.createAttributes(terms, terms);
        return new DataPoint(storyLabel, attributes);
    }
    
    public static void main(String[] args) {
        // testRockOnDigg();
        testDBSCAN();  
    }

    private static void testDBSCAN() {
        MyDiggSpaceDataset ds = MyDiggSpaceData.createDataset(3);
        double eps = 0.8;
        int minPts = 2;
        boolean useTermFreq = true;
        DBSCANAlgorithm dbscan = new DBSCANAlgorithm(ds.getData(), 
                new CosineDistance(), 
                eps, minPts, useTermFreq);
        
        dbscan.cluster();
        //dbscan.printDistances();
    }
    
    public static void testRockOnDigg() {
        MyDiggSpaceDataset ds = MyDiggSpaceData.createDataset(10);
        ROCKAlgorithm rock = new ROCKAlgorithm(ds.getData(), 4, 0.1);
        //rock.getLinkMatrix().printSimilarityMatrix();
        //rock.getLinkMatrix().printPointNeighborMatrix();
        //rock.getLinkMatrix().printPointLinkMatrix();
        Dendrogram dnd = rock.cluster();
        dnd.print(130); // if you get NPE here it means that level doesn't exist.
                        
                        // ROCK stops clustering if there are no links between clusters.
    } 
}
