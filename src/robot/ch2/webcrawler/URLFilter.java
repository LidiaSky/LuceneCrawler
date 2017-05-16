package robot.ch2.webcrawler;
import java.util.Vector;


/**
 * Performs url filtering before url is registered in 'known urls' database.
 */
public class URLFilter {

    private boolean allowFileUrls = true;
    private boolean allowHttpUrls = true;

    private Vector<String> BlackList;

    public URLFilter() {
        // empty
        BlackList = new Vector<String>();
        BlackList.add("http://javascript:");
        BlackList.add("http://ad.doubleclick.net");
        BlackList.add("http://icd-10online.com/");
    }

    /**
     * Basic implementation of url filter. Only allows urls that start 
     * with 'http:' and 'file:'
     *
     * <p>
     * Other features that can be added are: 
     * <ul>
     *   <li>extract host from the url and check against robots.txt</li>
     *   <li>check against the list of excluded urls</li>
     *   <li>user defined criteria</li>
     * </ul>
     * </p>
     */
    public boolean accept(String url) {
        boolean acceptUrl = true;
        url = url.trim();
        if( allowFileUrls && url.startsWith("file:") ) {
            acceptUrl = true;
        }
        else if( allowHttpUrls && url.startsWith("http:") ) {
            for(String s : BlackList)
            {
                if(url.startsWith(s))
                    acceptUrl = false;
            }
            if(!acceptUrl)
            {
                System.out.println("DEBUG: Url is in black list: '" + url + "'");
            }
            else
            {
                System.out.println("DEBUG: Accepted url: '" + url + "'");
            }
        }
        else {
            acceptUrl = false;
            System.out.println("DEBUG: Filtered url: '" + url + "'");
        }

        return acceptUrl;
    }

    public void setAllowFileUrls(boolean flag) {
        this.allowFileUrls = flag;
    }

    public void setAllowHttpUrls(boolean flag) {
        this.allowHttpUrls = flag;
    }
}
