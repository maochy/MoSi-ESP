package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import jmetal.util.JMException;
import method.SolutionItem;
import model.BaseStation;
import model.ConstNum;

public class ExperimentalUtils {
	
	public static BaseStation[] readBaseStationSubset(int time){
		BaseStation[] BaseStationSubset = new BaseStation[ConstNum.nBaseStation];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					"data/BaseStationSubset/" + ConstNum.nBaseStation + "/" + time + "_BSSubset_" + ConstNum.nBaseStation + ".csv")));
			String line = null;
			int i=0;
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				BaseStation bs = new BaseStation();
				bs.setID(Integer.valueOf(items[0]) - 1);
				bs.setLat(Double.valueOf(items[1]));
				bs.setLng(Double.valueOf(items[2]));
				bs.setWorktime(Integer.valueOf(items[3]));
				bs.setConn(Integer.valueOf(items[4]));
				BaseStationSubset[i] = bs;
				i++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return BaseStationSubset;
	}
	
	public static BaseStation[] getBSList(String path) {
		BaseStation[] BSList = new BaseStation[ConstNum.nBaseStation];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = null;
			line = reader.readLine();
			for (int i = 0; i < BSList.length; i++) {// (line = reader.readLine()) != null
				line = reader.readLine();
				String[] items = line.split(",");
				BaseStation bs = new BaseStation();
				bs.setID(Integer.valueOf(items[0]) - 1);
				bs.setLat(Double.valueOf(items[1]));
				bs.setLng(Double.valueOf(items[2]));
				bs.setWorktime(Integer.valueOf(items[3]));
				bs.setConn(Integer.valueOf(items[4]));
				BSList[i] = bs;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BSList;
	}
	
	public static int getServerNum(SolutionItem[] codeList) throws JMException {
		int serverNum = 0;
		for (SolutionItem solutionItem : codeList) {
			if (solutionItem.isServer()) {
				serverNum++;
			}
		}
		return serverNum;
	}
	
}
