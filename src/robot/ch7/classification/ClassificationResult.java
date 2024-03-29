package robot.ch7.classification;

import robot.ch5.ontology.intf.Concept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClassificationResult {
    private Concept concept;
    private double score;
    
    public ClassificationResult(Concept concept, double score) {
        this.concept = concept;
        this.score = score;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        // only take into account concept name
        result = prime * result + ((concept == null) ? 0 : concept.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        // only take into account concept name
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (! (obj instanceof ClassificationResult))
            return false;
        final ClassificationResult other = (ClassificationResult) obj;
        // only take into account concept name        
        if (concept == null) {
            if (other.concept != null)
                return false;
        } else if (!concept.getName().equals(other.concept.getName()))
            return false;
        return true;
    }
    
    @Override
	public String toString() {
        String name = null;
        if( concept != null ) {
            name = concept.getName();
        }
        return "[" + name + "->" + score + "]";
    }
    
    public static void sort(List<ClassificationResult> results) {
        
        Collections.sort(results, new Comparator<ClassificationResult>() {
            
            public int compare(ClassificationResult f1, ClassificationResult f2) {
                
                int result = 0;
                if( f1.getScore() < f2.getScore() ) {
                    result = 1; 
                }
                else if( f1.getScore() > f2.getScore() ) {
                    result = -1;
                }
                else {
                    result = 0;
                }
                return result;
            }
        });
    }
    
    public static List<ClassificationResult> getTopResults(
            List<ClassificationResult> results, int topN) {
        
        // sort friends based on itemAgreement
        ClassificationResult.sort(results);
        
        // select top N friends
        List<ClassificationResult> bestScores = new ArrayList<ClassificationResult>();
        for(ClassificationResult f : results) {
            if( bestScores.size() >= topN ) {
                // have enough items.
                break;
            }
            bestScores.add(f);
        }
        
        return bestScores;
    }
    
    
}
