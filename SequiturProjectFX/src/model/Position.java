package model;

import java.util.List;
/**
 * 
 * @author Elizabeta Budini
 *
 * 
 */
public class Position {
	private double elevation;
	private double wid;
	private double sx;
	private double sy;
	private double sz;
	private double latitude;
	private double x;
	private double y;
	private double z;
	private Boolean validity;
	private float longitude;
	private float ts;
	private List<String> zones;
	
	public Position(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public List<String> getZones() {
		return zones;
	}
	public double getElevation() {
		return elevation;
	}
	public double getWid() {
		return wid;
	}
	public double getSx() {
		return sx;
	}
	public double getSy() {
		return sy;
	}
	public double getSz() {
		return sz;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public Boolean getValidity() {
		return validity;
	}
	public float getLongitude() {
		return longitude;
	}
	public float getTs() {
		return ts;
	}
	
	
}
