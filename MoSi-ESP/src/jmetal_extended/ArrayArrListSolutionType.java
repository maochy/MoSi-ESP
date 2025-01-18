//  ArrayArrListSolutionType.java

package jmetal_extended;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import model.ESP;

/**
* Class representing the solution type of solutions composed of an ArrayArrList 
* encodings.variable
*/
public class ArrayArrListSolutionType extends SolutionType {

	public ArrayArrListSolutionType(Problem problem) {
		super(problem);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Creates the variables of the solution
	 */
	public Variable[] createVariables() {
		Variable[] variables = new Variable[1];

		variables[0] = new ArrayArrList(
				((ESP) problem_).getBSList().length, problem_);

		return variables;
	} // createVariables
	
	/**
	 * Copy the variables
	 * @param vars Variables to copy
	 * @return An array of variables
	 */
	public Variable[] copyVariables(Variable[] vars) {
		Variable[] variables = new Variable[1];
		variables[0] = vars[0].deepCopy();
		
		return variables ;
	} // copyVariables
} // ArrayArrListType
