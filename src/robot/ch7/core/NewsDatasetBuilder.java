package robot.ch7.core;

import java.util.List;

import robot.ch2.webcrawler.CrawlData;
import robot.ch2.webcrawler.db.ProcessedDocsDB;
import robot.ch2.webcrawler.model.ProcessedDocument;
import robot.ch7.crawling.CrawlResultsNewsDataset;
import robot.ch7.crawling.FileListNewsDataset;

public class NewsDatasetBuilder {

    public static int TOP_N_TERMS = 50;
    public static final String TRAINING_FILES_DIR_CH7 = "C:/robot/data/ch07/training";
    public static final String TEST_FILES_DIR_CH7 = "C:/robot/data/ch07/test";
    
    public static NewsDataset createNewsDatasetFromFileList(
            String datasetName, String dir) {

    	FileListNewsDataset ds = new FileListNewsDataset(datasetName,dir);
        ds.setTopTerms(TOP_N_TERMS);
        ds.loadTopics();
        ds.loadStories();
        
        return ds;
    }
    
    public static NewsDataset createNewsDatasetFromCrawledData(String datasetName, String crawlDataDir) {
    	
        CrawlData crawlData = new CrawlData(crawlDataDir);
        crawlData.init();
        
        ProcessedDocsDB processedDocsDB = crawlData.getProcessedDocsDB();

        CrawlResultsNewsDataset dataset = new CrawlResultsNewsDataset(datasetName, crawlDataDir);
        dataset.setTopTerms(TOP_N_TERMS);
        dataset.loadTopics();
        dataset.loadStories();
        /* Load all document groups into dataset */
        List<String> allGroups = processedDocsDB.getAllGroupIds();
        for(String groupId : allGroups) {
            List<ProcessedDocument> docs = 
                processedDocsDB.loadAllDocumentsInGroup(groupId);
            dataset.addDocuments(docs);
        }
        
        return dataset;
    }
}
