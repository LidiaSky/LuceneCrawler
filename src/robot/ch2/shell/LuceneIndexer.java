package robot.ch2.shell;

import robot.ch2.lucene.LuceneIndexBuilder;
import robot.ch2.webcrawler.CrawlData;
import robot.ch2.webcrawler.CrawlDataProcessor;
import robot.ch2.webcrawler.utils.FileUtils;

import java.io.File;

public class LuceneIndexer {
	
	private String baseDir;
	
	private String luceneIndexDir;
	
	public LuceneIndexer(String dir) {
		
		baseDir = dir;
		luceneIndexDir = baseDir+System.getProperty("file.separator")+"lucene-index";
	}
	
    public void run() {
    
        // load existing data
        CrawlData crawlData = new CrawlData(baseDir);
        crawlData.init(); 
        
        File luceneIndexRootDir = new File(getLuceneDir());
        
        // Delete the index directory, if it exists
        FileUtils.deleteDir(luceneIndexRootDir);
        //FileUtils.deleteDir(baseDir+System.getProperty("file.separator")+"fetched");
        FileUtils.deleteDir(baseDir+System.getProperty("file.separator")+"knownurls");
        luceneIndexRootDir.mkdirs();
        
        CrawlDataProcessor luceneIndexBuilder = new LuceneIndexBuilder(luceneIndexRootDir, crawlData);
        
        System.out.print("Starting the indexing ... ");
        
        luceneIndexBuilder.run();

        System.out.println("Indexing completed! \n");        
    }
    
    public String getLuceneDir() {
    	
    	return luceneIndexDir;
    }
    
}
