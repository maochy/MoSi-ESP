package data.Preprocess;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class data_preprocessing_main {

	static String inputPath = "data/data_6.1~6.15.csv";
	static String outputPath = "data/BaseStationInfo.csv";
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputPath)));
		writer.write("ID,latitude,longitude,UserAccessTime(min),UserAccessNum,worktime(min)");
		writer.newLine();

		double worktime = 0;
		int ID = 1;
		String date = "1";
		String startTime = "0:34:28";
		String endTime = "2:37:07";
		String location = "22.522803/114.218796";
		String coverTime = "";
		double time = 0;
		int count = 0;
		double total_count = 0;
		double total_time = 0;
		
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] items = line.split(",");
			if (items[4].equals("")) {
				continue;// 若基站位置信息缺失，则跳过
			}

			count++;
			// 处理跨日期的记录
			if (compareTime(startTime, endTime) == -1) {
				String[] temp = endTime.split(":");
				temp[0] = String.valueOf(Integer.valueOf(temp[0]) + 24);
				endTime = temp[0] + ":" + temp[1] + ":" + temp[2];
				coverTime = endTime;
			}

			if (!items[4].equals(location)) {
				time += getTime(startTime, endTime);// 计算最后的工作负载
				worktime += getTime(startTime, endTime);
				String[] latAndLon = location.split("/");
				writer.write(ID + "," + latAndLon[0] + "," + latAndLon[1] + "," + time / 60 + "," + count + ","
						+ worktime / 60);
				// 平均连接数=工作负载/（总负载/总连接数）
				writer.newLine();// 填充该基站数据
				total_count += count;
				total_time += time;
				// 初始化
				ID++;
				location = items[4];
				startTime = items[2];
				endTime = items[3];
				date = items[1];
				worktime = 0;
				count = 0;
				time = 0;
			} else {
				time += getTime(startTime, endTime);
				if (date.equals(items[1])) {// 同一天
					if (compareTime(items[2], endTime) != 1) {
						worktime += getTime(startTime, endTime);
						startTime = items[2];
						endTime = items[3];
					} else if (compareTime(items[2], endTime) == 1) {
						if (compareTime(items[3], endTime) != 1) {
							endTime = items[3];
						} else if (compareTime(items[3], endTime) == 1) {
							continue;
						}
					}
				} else {
					date = items[1];
					worktime += getTime(startTime, endTime);
					if (!coverTime.equals("")) {
						startTime = coverTime;
						endTime = coverTime;
						coverTime = "";
					} else {
						startTime = items[2];
						endTime = items[3];
					}
				}

			}

		}
		writer.write(total_count + "," + total_time / 60 + "," + (1.0 * total_time) / (total_count * 60));
		reader.close();
		writer.close();
	}

	public static int compareTime(String startTime, String endTime) {
		String[] startTimeArr = startTime.split(":");
		String[] endTimeArr = endTime.split(":");
		int[] startTimeInt = new int[endTimeArr.length];
		int[] endTimeInt = new int[endTimeArr.length];

		for (int i = 0; i < endTimeArr.length; i++) {
			startTimeInt[i] = Integer.valueOf(startTimeArr[i]);
			endTimeInt[i] = Integer.valueOf(endTimeArr[i]);
		}

		if (startTimeInt[0] < endTimeInt[0]) {
			return 1;
		} else if (startTimeInt[0] == endTimeInt[0]) {
			if (startTimeInt[1] < endTimeInt[1]) {
				return 1;
			} else if (startTimeInt[1] == endTimeInt[1]) {
				if (startTimeInt[2] < endTimeInt[2]) {
					return 1;
				} else if (startTimeInt[2] == endTimeInt[2]) {
					return 0;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public static int getTime(String startTime, String endTime) {

		String[] startTimeArr = startTime.split(":");
		String[] endTimeArr = endTime.split(":");
		int second = 0;

		second = (Integer.valueOf(endTimeArr[0]) * 3600
				+ Integer.valueOf(endTimeArr[1]) * 60 + Integer
					.valueOf(endTimeArr[2]))
				- (Integer.valueOf(startTimeArr[0]) * 3600
						+ Integer.valueOf(startTimeArr[1]) * 60 + Integer
							.valueOf(startTimeArr[2]));

		return second;
	}
}
