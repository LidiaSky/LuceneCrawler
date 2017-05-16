package robot.ch5.usecase.fraud.data;

import robot.ch5.usecase.fraud.util.DataUtils;

import java.util.List;

public class TransactionLoader {

    public static final String TRAINING_TXNS_FILE = 
            "c:/robot/data/ch05/fraud/training-txns.txt";
    
    public static final String TEST_TXNS_FILE = 
            "c:/robot/data/ch05/fraud/test-txns.txt";
    
    public static List<Transaction> loadTxns(String filename) {
        return DataUtils.loadTransactions(filename);
    }
    
    public static TransactionDataset loadTrainingDataset() {
        List<Transaction> allTxns = loadTxns(TRAINING_TXNS_FILE);
        return new TransactionDataset(allTxns);
    }

    public static TransactionDataset loadTestDataset() {
        List<Transaction> allTxns = loadTxns(TEST_TXNS_FILE);
        return new TransactionDataset(allTxns);
    }
}
