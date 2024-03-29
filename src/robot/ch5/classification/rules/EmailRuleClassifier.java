package robot.ch5.classification.rules;

import robot.ch5.ontology.core.BaseConcept;
import robot.ch5.ontology.intf.Concept;
import robot.ch5.usecase.email.data.Email;
import robot.ch5.usecase.email.data.EmailData;
import robot.ch5.usecase.email.data.EmailDataset;

public class EmailRuleClassifier {

    private String ruleFilename;
    private RuleEngine re;
    private Concept spam;
    private Concept notSpam;
    
    public EmailRuleClassifier(String ruleFilename) {
        this.ruleFilename = ruleFilename;
    }
    
    public void train() {
        re = new RuleEngine(ruleFilename);
        spam = new BaseConcept("SPAM");
        notSpam = new BaseConcept("NOT-SPAM");
        
    }
    
    public Concept classify(Email email) {
        ClassificationResult result = new ClassificationResult();
        re.executeRules(result, email);
        if( result.isSpamEmail() ) {
            return spam;
        }
        else {
            return notSpam;
        }
    }
    
    public void run(EmailDataset ds, String msg) {
        System.out.println("\n");        
        System.out.println(msg);
        System.out.println("__________________________________________________");        
        for(Email email : ds.getEmails() ) {
            System.out.println("\nClassifying email: " + email.getId() + " ...");

            Concept c = classify(email);
            
            System.out.println("Rules classified email: " + email.getId() + " as: " + c.getName());
        }
        System.out.println("__________________________________________________");        
    }
    
    public static void main(String[] args) {
        EmailDataset ds = EmailData.createTestDataset();
        
        EmailRuleClassifier classifier = new EmailRuleClassifier("c:/robot/data/ch05/spamfilter/spamRules1.drl");

        classifier.train();

        System.out.println("1. Expecting one spam email...");
        System.out.println("\n");        
        for(Email email : ds.getEmails() ) {
            System.out.println("\nClassifying email: " + email.getId() + " ...");

            Concept c = classifier.classify(email);
            
            System.out.println("Rules classified email: " + email.getId() + 
                    " as: " + c.getName());
        }
        System.out.println("\n");
        
        
        /* 
         * There should be no spam emails - rule that checks for known email address
         * should win over rules that detect spam content.
         */
        classifier = new EmailRuleClassifier(
            "c:/robot/data/ch05/spamfilter/spamRulesWithConflictResolution.drl");

        classifier.train();
        System.out.println("2. There should be no spam emails.\n" + 
                "Rule that checks for known email address should\n" +
                "win over rules that detect spam content.");
        System.out.println("\n");
        
        for(Email email : ds.getEmails() ) {
            Concept c = classifier.classify(email);
            
            System.out.println("Email: " + email.getId() + " classified as: " + c.getName());
        }

        
    }
}
