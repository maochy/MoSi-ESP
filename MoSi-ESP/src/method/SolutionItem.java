package method;

import java.util.ArrayList;

public class SolutionItem {
	private ArrayList<Integer> BSList; // the index list of base station served (Only be server, or be empty)
	private boolean serverFlag;

	public SolutionItem() {
		BSList = new ArrayList<Integer>();
		serverFlag = false;
	}

	public ArrayList<Integer> getBSList() {
		return BSList;
	}

	public void setBSList(ArrayList<Integer> BSList) {
		this.BSList = BSList;
	}

	public boolean isServer() {
		return serverFlag;
	}

	public void setServerFlag(boolean serverFlag) {
		this.serverFlag = serverFlag;
	}

}
