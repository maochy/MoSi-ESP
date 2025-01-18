//  MoSi_ESP_main.java

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal_extended.ArrayArrList;
import method.MoSi_ESP;
import model.BaseStation;
import model.ConstNum;
import model.ESP;
import utils.AlgorithmUtils;
import utils.ExperimentalUtils;
import utils.TestItem;

/**
 * Class for configuring and running the MoSi-ESP algorithm
 */
public class MoSi_ESP_main {
	
	public static int maxTimes = 200;

	public static void main(String[] args) throws JMException, IOException, ClassNotFoundException {
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Result#Test.csv")));
//		ExperimentTemplate("Test", 5, 500, bw, 600, 50);
		
		// the scope of the base station 5km(3-10)
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Result#Scope.csv", true)));
		bw.append("BSNum,Scope,Energy,Gini,TimeConsumption");
		bw.newLine();
		for (int i = 3; i <= 10; i++) {
			ExperimentTemplate("Scope", i, 500, bw, 600, 50);
		}
		bw.close();
		
		// the number of the base station 500(100-1000)
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("MoSi-ESP#BSNum.csv", true)));
		bw.append("BSNum,Scope,Energy,Gini,TimeConsumption");
		bw.newLine();
		for (int i = 50; i <= 400; i += 50) {
			ExperimentTemplate("BSNum", 5, i, bw, 600, 50);
		}
		bw.close();

	}// main

	public static void ExperimentTemplate(String id, double scope, int BSNum, BufferedWriter bw, int maxIterations, int swarmSize)
			throws JMException, IOException, ClassNotFoundException {
		
		BufferedWriter tmpBW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				"Result#" + id + "_" + scope + "-" + BSNum + ".csv")));
		
		// Parameters
		ConstNum.scope = scope;
		ConstNum.nBaseStation = BSNum;
		
		System.out.println(ConstNum.nBaseStation + "\t" + ConstNum.scope);

		// Base Station Set
		BaseStation[] BSList = ExperimentalUtils.getBSList("data/BaseStationSet.csv");
		
		// ESP problem class
		Problem esp = new ESP("ArrayArrList", BSList);

		// MoSi-ESP
		Algorithm algorithm = new MoSi_ESP(esp);
		algorithm.setInputParameter("swarmSize", swarmSize);
		algorithm.setInputParameter("archiveSize", 20);
		algorithm.setInputParameter("maxIterations", maxIterations);

		TestItem item = new TestItem(algorithm);

		double initTime;
		double estimatedTime;
		SolutionSet solutionSet;
		Solution solution;
		int serverNum = 0;

		int time = 1;
		while (time <= maxTimes) {
//			// Base station dataset
//			BSList = ExperimentalUtils.readBaseStationSubset(time - 1);
//			((ESP) esp).setBSList(BSList);

			// Execute the algorithm
			initTime = System.currentTimeMillis();
			solutionSet = algorithm.execute();
			estimatedTime = 1.0 * (System.currentTimeMillis() - initTime) / 1000;

			// Get the best solution
			solution = AlgorithmUtils.getBestSolution(solutionSet);

			// Calculate the number of server
			serverNum = ExperimentalUtils.getServerNum(((ArrayArrList) solution.getDecisionVariables()[0]).getArray());

			// print the result
			item.addResult(time, solution, serverNum, estimatedTime);
			System.out.println(time + "\t" + algorithm.getClass().getSimpleName() + "\t" + solution.getObjective(0) + "\t"
					+ solution.getObjective(1) + "\t" + serverNum + "\t" + estimatedTime);
			tmpBW.append(algorithm.getClass().getSimpleName() + "," + solution.getObjective(0) + ","
					+ solution.getObjective(1) + "," + serverNum + "," + estimatedTime + ",");

			tmpBW.newLine();
			tmpBW.flush();
			time++;
		}

		bw.append(ConstNum.nBaseStation + "," + ConstNum.scope + "," + item.avgEnergy + "," + item.avgGini + "," + item.avgServerNum + "," + item.avgTime);
		bw.newLine();
		bw.flush();
		tmpBW.close();
	}

} // MoSi_ESP_main
