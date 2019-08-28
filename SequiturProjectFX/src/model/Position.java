package model;

import java.util.List;
/**
 * 
 * @author Elizabeta Budini
 *
 * 
 */
public class Position {
	private float elevation;
	private float wid;
	private float sx;
	private float sy;
	private float sz;
	private float latitude;
	private float x;
	private float y;
	private float z;
	private Boolean validity;
	private float longitude;
	private float ts;
	private List<String> zones;
	
	public Position(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public List<String> getZones() {
		return zones;
	}
	public float getElevation() {
		return elevation;
	}
	public float getWid() {
		return wid;
	}
	public float getSx() {
		return sx;
	}
	public float getSy() {
		return sy;
	}
	public float getSz() {
		return sz;
	}
	public float getLatitude() {
		return latitude;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getZ() {
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
