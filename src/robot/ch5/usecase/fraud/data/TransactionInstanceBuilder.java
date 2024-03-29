package robot.ch5.usecase.fraud.data;

import robot.ch4.similarity.JaccardCoefficient;
import robot.ch4.similarity.SimilarityMeasure;
import robot.ch5.classification.core.TrainingSet;
import robot.ch5.ontology.core.DoubleAttribute;
import robot.ch5.ontology.core.StringAttribute;
import robot.ch5.ontology.intf.Attribute;
import robot.ch5.ontology.intf.Instance;
import robot.ch5.usecase.fraud.TransactionConcept;
import robot.ch5.usecase.fraud.TransactionInstance;
import robot.ch5.usecase.fraud.util.UserStatistics;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class TransactionInstanceBuilder implements java.io.Serializable {


    private static final long serialVersionUID = -2334221990318430678L;

    /*
     * For every user we keep a set of user-specific values
     * to normalize data.
     */
    private Map<Integer, UserStatistics> userStatisticsMap;

    /*
     * Similarity measure that will be used to evaluate similarity between
     * transaction descriptions.
     */
    private SimilarityMeasure descriptionSim;
    
    public TransactionInstanceBuilder() {
        userStatisticsMap = new HashMap<Integer, UserStatistics>();
        descriptionSim = new JaccardCoefficient();
    }

    
    /**
	 * @param userStatisticsMap the userStatisticsMap to set
	 */
	public void setUserStatisticsMap(Map<Integer, UserStatistics> userStatisticsMap) {
		this.userStatisticsMap = userStatisticsMap;
	}


	public TrainingSet createTrainingSet(TransactionDataset data) {
        List<Transaction> txns = data.getTransactions();
        int nTxns = txns.size();
        Instance[] instances = new Instance[nTxns];
        for(int i = 0; i < nTxns; i++) {
            Transaction t = txns.get(i);
            instances[i] = createInstance(t);
        }
        return new TrainingSet(instances);
    }
    
    public TransactionInstance createInstance(Transaction t) {
        
        int userId = t.getUserId();
        UserStatistics userStats = getUserStatistics(userId);
        
        if( userStats == null ) {
            throw new RuntimeException(
                    "Can't create instance. There are no statistics for user: " + userId);
        }
        
        /* Calculate distance between user location centroid and instance location */
        TransactionLocation nLocation = normalizeLocation(t.getLocation(), userStats);
        TransactionLocation nCentroid = normalizeLocation(
                userStats.getLocationCentroid(), userStats);
        double nLocationDistance = nCentroid.distance(nLocation); 

        double nAmt = normalizeAmount(t.getAmount(), userStats); 
        
        double nDescriptionSim = calculateDescriptionSimilarity(
                t.getDescription(), userStats);

        double nUserId = t.getUserId();

        List<Attribute> attributes = new ArrayList<Attribute>();

        // Attributes that will be used by NN
        attributes.add(new DoubleAttribute(
                TransactionInstance.ATTR_NAME_N_TXN_AMT, nAmt));
        attributes.add(new DoubleAttribute(
                TransactionInstance.ATTR_NAME_N_LOCATION, nLocationDistance));
        attributes.add(new DoubleAttribute(
                TransactionInstance.ATTR_NAME_N_DESCRIPTION, nDescriptionSim));

        // Adding informational attributes
        attributes.add(new StringAttribute(
                TransactionInstance.ATTR_NAME_USERID, String.valueOf(nUserId)));
        attributes.add(new StringAttribute(
                TransactionInstance.ATTR_NAME_TXNID, String.valueOf(t.getTxnId())));

        attributes.add(new DoubleAttribute(
                TransactionInstance.ATTR_NAME_TXN_AMT, t.getAmount()));
        attributes.add(new DoubleAttribute(
                TransactionInstance.ATTR_NAME_LOCATION_X, t.getLocation().getX()));
        attributes.add(new DoubleAttribute(
                TransactionInstance.ATTR_NAME_LOCATION_Y, t.getLocation().getY()));
        attributes.add(new StringAttribute(
                TransactionInstance.ATTR_NAME_DESCRIPTION, t.getDescription()));

        
        
        
        TransactionConcept c = null;
        if( t.isFraud() ) {
            c = new TransactionConcept(TransactionConcept.CONCEPT_LABEL_FRAUD);
        }
        else {
            c = new TransactionConcept(TransactionConcept.CONCEPT_LABEL_VALID);
        }
        
        return new TransactionInstance(c, attributes.toArray(new Attribute[0]));
    }
    
    public UserStatistics getUserStatistics(int userId) {
        return userStatisticsMap.get(userId);
    }
    
    private Double normalizeAmount(Double amt, UserStatistics u) {
        Double min = u.getTxnAmtMin();
        Double max = u.getTxnAmtMax();
        Double v = (amt - min) / (max - min);
        return v; // Valid values should fall into [0..1] and fraud outside.
    }
    
    private TransactionLocation normalizeLocation(TransactionLocation location, UserStatistics u) {

        double nX = (location.getX() - u.getLocationMinX()) 
            / (u.getLocationMaxX() - u.getLocationMinX());

        double nY = (location.getY() - u.getLocationMinY()) 
            / (u.getLocationMaxY() - u.getLocationMinY());
        
        return new TransactionLocation(nX, nY); 
    }
    
    private Double calculateDescriptionSimilarity(
            String txnDescription, UserStatistics u) {
        
        String[] termsX = tokenizeTxnDescription(txnDescription);
        Set<String> validTxnDescriptions = u.getDescriptions();

        double bestSim = 0.0;
        for(String valueY : validTxnDescriptions) {
            String[] termsY = u.getDescriptionTokens(valueY);
            if( termsY == null ) { 
                termsY = tokenizeTxnDescription(valueY);
                u.setDescriptionTokens(valueY, termsY);
            }
            double sim = descriptionSim.similarity(termsX, termsY);
            if( sim > bestSim ) {
                bestSim = sim;
            }
        }
        
        return bestSim;
    }
    
    private String[] tokenizeTxnDescription(String description) {
        ArrayList<String> terms = new ArrayList<String>();

        StandardAnalyzer analyzer = new StandardAnalyzer();
        
        TokenStream stream = 
            analyzer.tokenStream("whatever", new StringReader(description));
        
        Token t = null;
        try {
            while ( (t = stream.next()) != null) {
                terms.add(new String(t.termBuffer(), 0, t.termLength()));
            }
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        
        return terms.toArray(new String[terms.size()]);
    }
    
    
    public Map<Integer, UserStatistics> getUserStatistics() {
        return userStatisticsMap;
    }
    
    public void printUserStats(int userId) {
        UserStatistics userProps = userStatisticsMap.get(userId);
        System.out.println("Properties for userId: " + userId + " calculated from training data:");
        System.out.println(userProps.toString());
    }

	/**
	 * @return the userStatisticsMap
	 */
	public Map<Integer, UserStatistics> getUserStatisticsMap() {
		return userStatisticsMap;
	}
    
}
