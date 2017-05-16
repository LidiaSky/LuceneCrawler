/**
 * 
 */
package robot.ch2.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author bmarmanis
 *
 */
public class SearchResult {

	private String docId;
    private String docType;	
    private String title;
	private String url;
	
	private double score;
	
    public SearchResult(String docId, String docType, String title, String url,double score) {
		
		this.docId = docId;
		this.docType = docType;
		this.title = title;
		this.url   = url;
		this.score = score;
	}


	public String getDocId() {
		return docId;
	}


	public void setDocId(String docId) {
		this.docId = docId;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public double getScore() {
		return score;
	}


	public void setScore(double score) {
		this.score = score;
	}


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    
	public String print() {
		StringBuilder strB = new StringBuilder();
//		strB.append("Document ID    : ").append(docId).append("\n");
		strB.append("Document Type: ").append(docType).append("\n");
		strB.append("Document Title : ").append(title).append("\n");
		strB.append("Document URL: ").append(url).append("  -->  ");
		strB.append("Relevance Score: ").append(score).append("\n");
		return strB.toString();
	}
	

    public static void sortByScore(List<SearchResult> values) {
        Collections.sort(values, new Comparator<SearchResult>() {
            public int compare(SearchResult r1, SearchResult r2) { 
                int result = 0;

                if( r1.getScore() < r2.getScore() ) {
                    result = 1;
                }
                else if( r1.getScore() > r2.getScore() ) {
                    result = -1;
                }
                else {
                    result = 0;
                }
                return result;
            }
        });
    }
    
    /**
     * Сортировка массива по убыванию значений релевантности
            */
    public static void sortByScore(SearchResult[] values) {
        Arrays.sort(values, new Comparator<SearchResult>() {
            public int compare(SearchResult r1, SearchResult r2) { 
                int result = 0;

                if( r1.getScore() < r2.getScore() ) {
                    result = 1;
                }
                else if( r1.getScore() > r2.getScore() ) {
                    result = -1;
                }
                else {
                    result = 0;
                }
                return result;
            }
        });
    }
}
