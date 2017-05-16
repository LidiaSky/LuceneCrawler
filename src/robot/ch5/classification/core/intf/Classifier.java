package robot.ch5.classification.core.intf;

import robot.ch5.ontology.intf.Concept;
import robot.ch5.ontology.intf.Instance;

/** 
 * Every classifier must be:
 * <UL> 
 *   <LI> able to load a <CODE>TrainingSet</CODE>, and </LI>
 *   <LI> able to classify an <CODE>Instance</CODE></LI>
 * </UL>
 * 
 * This interface reflects these two elementary methods.
 * 
 * @author babis
 */
public interface Classifier {

    public String getName();
    
	public boolean train();

	public Concept classify(Instance instance);
}
