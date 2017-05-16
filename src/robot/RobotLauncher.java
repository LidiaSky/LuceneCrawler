package robot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import robot.ch2.data.SearchResult;
import robot.ch2.shell.LuceneIndexer;
import robot.ch2.shell.MySearcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class RobotLauncher {

    private String ConvertToXml(SearchResult [] results)
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);
            for(int i = 0; i < results.length; i++) {
                Element resultElement = doc.createElement("result");
                Element titleElement = doc.createElement("t");
                titleElement.appendChild(doc.createTextNode(results[i].getTitle()));
                resultElement.appendChild(titleElement);
                Element linkElement = doc.createElement("link");
                linkElement.appendChild(doc.createTextNode(results[i].getUrl()));
                resultElement.appendChild(linkElement);
                rootElement.appendChild(resultElement);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
            return output;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return "<root />";
    }

    public String Search(String stringToSearch){
        LuceneIndexer luceneIndexer = new LuceneIndexer("search_result/");
        MySearcher searcher = new MySearcher(luceneIndexer.getLuceneDir());
        SearchResult[] results = searcher.search(stringToSearch, 200);
        //System.out.print(ConvertToXml(results));
        return ConvertToXml(results);
    }
}
