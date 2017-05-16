package robot.ch2.shell;

import robot.ch2.ranking.PageRankMatrixBuilder;
import robot.ch2.ranking.PageRankMatrixH;
import robot.ch2.ranking.Rank;
import robot.ch2.webcrawler.CrawlData;

public class PageRank extends Rank {
    
	PageRankMatrixBuilder pageRankBuilder;
	
	public PageRank(CrawlData crawlData) {
        pageRankBuilder = new PageRankMatrixBuilder(crawlData);
        pageRankBuilder.run();     
	}
	
	@Override
	public PageRankMatrixH getH() {
		return pageRankBuilder.getH();
	}
	
}
