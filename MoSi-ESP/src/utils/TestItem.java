package utils;


import jmetal.core.Algorithm;
import jmetal.core.Solution;

public class TestItem {
	Algorithm algorithm;
	public double avgEnergy = 0;
	public double avgGini = 0;
	public double avgTime = 0;
	public double avgServerNum = 0;
	
	public TestItem() {
		
	}
	
	public TestItem(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void addResult(int time, Solution solution, int serverNum, double timeConsumption) {
		this.avgEnergy = (this.avgEnergy * (time - 1)  + solution.getObjective(0)) / time;
		this.avgGini = (this.avgGini * (time - 1) + solution.getObjective(1)) / time;
		this.avgServerNum = (this.avgServerNum * (time - 1) + serverNum) / time;
		this.avgTime = (this.avgTime * (time - 1) + timeConsumption) / time;
	}
	
	

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	@Override
	public String toString() {
		String str = algorithm.getClass().getSimpleName()+ "," + avgEnergy + "," + avgGini + "," + avgServerNum + "," + avgTime;
		return str;
	}
	
}
