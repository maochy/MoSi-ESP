package utils;

import java.util.ArrayList;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import method.SolutionItem;
import model.BaseStation;
import model.ConstNum;

public class AlgorithmUtils {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList[] generateBSGraph(BaseStation[] BSList) {
		int BSNum = BSList.length;
		ArrayList<Integer>[] BSGraph = new ArrayList[BSNum];
		for (int i = 0; i < BSNum; i++) {
			ArrayList<Integer> nearBSKey = new ArrayList<Integer>();
			for (int j = 0; j < BSNum; j++) {
				if (getDistance(BSList[i].getLat(), BSList[i].getLng(), BSList[j].getLat(), BSList[j].getLng())
						< ConstNum.scope && i != j) {
					nearBSKey.add(j);
				}
			}
			BSGraph[i] = nearBSKey;
		}
		return BSGraph;
	}
	
	public static int[][] updateResInfo(SolutionItem[] codeList, BaseStation[] BSList) {
		int[][] resInfo = new int[2][codeList.length];
		for (int i = 0; i < codeList.length; i++) {
			for (Integer index : codeList[i].getBSList()) {
				resInfo[0][i] += BSList[index].getWorktime();
				resInfo[1][i] += BSList[index].getConn();
			}
		}
		return resInfo;
	}
	
	public static Solution getBestSolution(SolutionSet solutionSet) {
		if ((solutionSet == null) || (solutionSet.size() == 0)) {
			return null;
		} else if (solutionSet.size() == 1) {
			return solutionSet.get(0);
		}
		
		ArrayList<Solution> tmp = new ArrayList<>();
		tmp.add(solutionSet.get(0));
		// Insertion sorting
		for (int i = 1; i < solutionSet.size(); i++) {
			int j = 0;
			for (; j < tmp.size(); j++) {
				if (tmp.get(j).getObjective(0) > solutionSet.get(i).getObjective(0)) {
					break;
				}
			}
			tmp.add(j, solutionSet.get(i));
		}
		
		double[] rank = new double[solutionSet.size()];
		int bestID = -1;
		int d = solutionSet.size();
		for (int i = 0; i < tmp.size(); i++) {
			rank[i] = Math.pow(Math.pow(ConstNum.Lambda * (i + 1), 2) + Math.pow((1 - ConstNum.Lambda) * (d - i), 2), 0.5) / (d + 1);
			if (bestID == -1 || rank[i] < rank[bestID]) {
				bestID = i;
			}
		}

		return tmp.get(bestID);
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * ConstNum.EARTH_RADIUS;
		
		return s; 
	} 
}
