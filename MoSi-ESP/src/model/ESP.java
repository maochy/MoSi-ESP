package model;

import java.util.ArrayList;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;
import jmetal_extended.ArrayArrListSolutionType;
import jmetal_extended.XArrayList;
import method.SolutionItem;
import utils.AlgorithmUtils;

/**
 * Edge Server Placement (ESP) Problem class
 */
@SuppressWarnings("serial")
public class ESP extends Problem {

	private BaseStation[] BSList;			// the set of the base stations
	private ArrayList<Integer>[] BSGraph;	// the base station communication network
	
	/**
	 * Constructor. Creates a default instance of the ESP problem.
	 * 
	 * @param solutionType
	 *            The solution type must "ArrayArrList".
	 */
	public ESP(String solutionType) throws ClassNotFoundException {
		this(solutionType, new BaseStation[0]);
	} // ESP

	/**
	 * Constructor. Creates a new instance of the ESP problem.
	 * 
	 * @param numberOfVariables
	 *            Number of variables of the problem
	 * @param solutionType
	 *            The solution type must "Real", "BinaryReal, and "ArrayReal".
	 */
	@SuppressWarnings("unchecked")
	public ESP(String solutionType, BaseStation[] BSList) {
		numberOfVariables_ = 1;		//  the codeList is one only variables 
		numberOfObjectives_ = 2;	// the objectives are energy consumption minimum and load balance
		numberOfConstraints_ = 0;
		problemName_ = "ESP";
		this.BSList = BSList;
		this.BSGraph = AlgorithmUtils.generateBSGraph(BSList);

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int i = 0; i < numberOfVariables_; i++) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = 1;
		} // for

		if (solutionType.compareTo("ArrayArrList") == 0)
			solutionType_ = new ArrayArrListSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType + " invalid");
			System.exit(-1);
		}
	} // ESP

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 * @throws JMException
	 */
	public void evaluate(Solution solution) throws JMException {
		XArrayList vars = new XArrayList(solution);
		double[] result = new double[2]; // objective values
		
		SolutionItem[] codeList = vars.getArray(); // the codeList of ESP solution
		int[][] resInfo = AlgorithmUtils.updateResInfo(codeList, BSList);

		// calculate energy consumption (kw¡¤h)
		result[0] = 0.0;
		int serverNum = 0;
		for (int i = 0; i < resInfo[0].length; i++) {
			if (resInfo[0][i] > 0) {
				double util = resInfo[1][i] / ConstNum.MAX_CONN;
				result[0] += (ConstNum.MIN_WATT * ConstNum.MAX_WORKTIME
						+ (ConstNum.MAX_WATT - ConstNum.MIN_WATT) * util * resInfo[0][i]) / (60 * 1000);
				serverNum++;
			}
		}

		// calculate load balance metric (gini coefficient)
		result[1] = 0.0;
		int totalConn = 0;
		for (int i = 0; i < resInfo[0].length; i++) 
			totalConn += resInfo[1][i];
		double avgConn = 1.0 * totalConn / serverNum;
		
		int aux = 0; 
		for (int i = 0; i < ConstNum.nBaseStation; i++) {
			if (resInfo[1][i] == 0) continue;
			for (int j = 0; j < ConstNum.nBaseStation; j++) {
				if (resInfo[1][j] == 0) continue;
				aux += Math.abs(resInfo[1][i] - resInfo[1][j]);
			}
		}
		result[1] = 1.0 * aux / (2 * serverNum * serverNum * avgConn);

		solution.setObjective(0, result[0]); // energy consumption (minimize)
		solution.setObjective(1, result[1]); // gini coefficient (minimize)
	} // evaluate

	public BaseStation[] getBSList() {
		return BSList;
	}
	
	public void setBSList(BaseStation[] BSList) {
		this.BSList = BSList;
	}

	public ArrayList<Integer>[] getBSGraph() {
		return BSGraph;
	}

}// Edge Server Placement
