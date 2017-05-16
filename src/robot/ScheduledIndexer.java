package robot;

import robot.ch2.shell.FetchAndProcessCrawler;
import robot.ch2.shell.LuceneIndexer;
import robot.ch2.webcrawler.utils.FileUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Лидия
 * Date: 16.12.13
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class ScheduledIndexer {
    static final int maxDepth = 7;
    static final int maxDocuments = 50;

    public static void main(String [] args)
    {
        FileUtils.deleteDir("search_result/fetched");
        FileUtils.deleteDir("search_result/knownurls");
        FileUtils.deleteDir("search_result/lucene-index");
        FileUtils.deleteDir("search_result/pagelinks");
        FileUtils.deleteDir("search_result/processed");
        FetchAndProcessCrawler crawler = new FetchAndProcessCrawler("search_result/",maxDepth,maxDocuments);
        crawler.setDefaultUrls();
        crawler.run();
        LuceneIndexer luceneIndexer = new LuceneIndexer("search_result/");
        luceneIndexer.run();
    }
}
