/**
 * 
 */
package robot.ch2.shell;

import robot.ch2.ranking.DocRankMatrixBuilder;
import robot.ch2.ranking.PageRankMatrixH;
import robot.ch2.ranking.Rank;

/**
 * @author babis
 *
 */
public class DocRank extends Rank {

    DocRankMatrixBuilder docRankBuilder;
        
    public DocRank(String luceneIndexDir, int termsToKeep) {
        docRankBuilder = new DocRankMatrixBuilder(luceneIndexDir);
        docRankBuilder.setTermsToKeep(termsToKeep);
        docRankBuilder.run();
    }
    
    @Override
	public PageRankMatrixH getH() {
        return docRankBuilder.getH();
    }
}
