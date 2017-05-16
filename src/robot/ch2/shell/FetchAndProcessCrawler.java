package robot.ch2.shell;

import robot.ch2.webcrawler.BasicWebCrawler;
import robot.ch2.webcrawler.CrawlData;
import robot.ch2.webcrawler.URLFilter;
import robot.ch2.webcrawler.URLNormalizer;

import java.util.ArrayList;
import java.util.List;

public class FetchAndProcessCrawler {
    //извлечение данных и выполнение их синтаксического разбора

    public static final int DEFAULT_MAX_DEPTH = 3;
    public static final int DEFAULT_MAX_DOCS = 100;

    CrawlData crawlData;

    // Здесь будет храниться собранная информация
    String rootDir;

    // число итераций
    int maxDepth = DEFAULT_MAX_DEPTH;

    // максимум страниц,которые будут просмотрены в процессе выполнения одного поиска
    int maxDocs = DEFAULT_MAX_DOCS;

    List<String> seedUrls;

    URLFilter urlFilter;

    public FetchAndProcessCrawler(String dir, int maxDepth, int maxDocs) {

        rootDir = dir;

        this.maxDepth = maxDepth;

        this.maxDocs = maxDocs;

        this.seedUrls = new ArrayList<String>();
    	

        this.urlFilter = new URLFilter();
        urlFilter.setAllowFileUrls(true);
        urlFilter.setAllowHttpUrls(true);
    }



    public void run() {

        crawlData = new CrawlData(rootDir);

        BasicWebCrawler webCrawler = new BasicWebCrawler(crawlData);
        webCrawler.addSeedUrls(getSeedUrls());

        webCrawler.setURLFilter(urlFilter);

        long t0 = System.currentTimeMillis();

        /* выполняется краулинг */
        webCrawler.fetchAndProcess(maxDepth, maxDocs);

        System.out.println("Timer (s): [Crawler processed data] --> " +
                (System.currentTimeMillis()-t0)*0.001);

    }

    public List<String> getSeedUrls() {

        return seedUrls;
    }

    public void addUrl(String val) {
        URLNormalizer urlNormalizer = new URLNormalizer();
        seedUrls.add(urlNormalizer.normalizeUrl(val));
    }

    public void setAllUrls() {

        setDefaultUrls();


        addUrl("http://sciencedirect.com");//ch
    }

    public void setDefaultUrls() {

        addUrl("http://sciencedirect.com");
    }

    public void setUrlFilter(URLFilter urlFilter) {
        this.urlFilter = urlFilter;
    }

    private void setFilesOnlyUrlFilter() {
        /* выставляем конфигурацию url -фильтра:допусраются файлы видаfile:// urls   */
        URLFilter urlFilter = new URLFilter();
        urlFilter.setAllowFileUrls(true);
        urlFilter.setAllowHttpUrls(false);
        setUrlFilter(urlFilter);
    }

    public void setUrls(String val) {

        setFilesOnlyUrlFilter();

        this.seedUrls.clear();



    }

    public void addDocSpam() {

        }


    public String getRootDir() {
        return rootDir;
    }


    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }


    public int getMaxNumberOfCrawls() {
        return maxDepth;
    }


    public void setMaxNumberOfCrawls(int maxNumberOfCrawls) {
        this.maxDepth = maxNumberOfCrawls;
    }


    public int getMaxNumberOfDocsPerCrawl() {
        return maxDocs;
    }


    public void setMaxNumberOfDocsPerCrawl(int maxNumberOfDocsPerCrawl) {
        this.maxDocs = maxNumberOfDocsPerCrawl;
    }


    public CrawlData getCrawlData() {
        return crawlData;
    }

}
