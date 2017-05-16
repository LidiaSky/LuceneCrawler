package robot.ch2.webcrawler.parser.common;

import robot.ch2.webcrawler.model.FetchedDocument;
import robot.ch2.webcrawler.model.ProcessedDocument;

/**
 * Interface for parsing document that was retrieved/fetched during
 * collection phase.  
 */
public interface DocumentParser {
    public ProcessedDocument parse(FetchedDocument doc) 
        throws DocumentParserException;
}
