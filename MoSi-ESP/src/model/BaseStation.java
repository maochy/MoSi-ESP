package model;

public class BaseStation {
	public int ID;
	public double lat;		// the latitude of the base station's coordinate
	public double lng;		// the longitude of the base station's coordinate
	public int worktime;	// the worktime of the base station
	public int conn;		// the number of connection that user request to
	
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public int getWorktime() {
		return worktime;
	}
	public void setWorktime(int worktime) {
		this.worktime = worktime;
	}
	public int getConn() {
		return conn;
	}
	public void setConn(int conn) {
		this.conn = conn;
	}
	


}
