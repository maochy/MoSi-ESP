package jmetal_extended;

import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import method.SolutionItem;

/**
 * Wrapper for accessing ArrayList-coded solutions
 */
public class XArrayList {

	private Solution solution_;
	private SolutionType type_;

	/**
	 * Constructor
	 */
	public XArrayList() {
	} // Constructor

	/**
	 * Constructor
	 * 
	 * @param solution
	 */
	public XArrayList(Solution solution) {
		this();
		type_ = solution.getType();
		solution_ = solution;
	}
	
	public SolutionItem[] getArray() throws JMException {
		if (type_.getClass() == ArrayArrListSolutionType.class) {
			return ((ArrayArrList) solution_.getDecisionVariables()[0]).getArray();
		} else {
			Configuration.logger_.severe("jmetal.util.wrapper.XArrayList.getValue, solution type " + type_ + "+ invalid");
		}
		return null;
	}

	public void setArray(SolutionItem[] array_) throws JMException {
		if (type_.getClass() == ArrayArrListSolutionType.class) {
			((ArrayArrList) solution_.getDecisionVariables()[0]).setArray(array_);
		} else {
			Configuration.logger_.severe("jmetal.util.wrapper.XArrayList.getValue, solution type " + type_ + "+ invalid");
		}
	}

}
