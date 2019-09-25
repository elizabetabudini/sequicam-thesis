package model;

import javafx.scene.image.Image;

public abstract class Camera implements CameraInterface {

	private Zone zone;
	private Position pos;
	private boolean ptz;
	private String IP;
	private double differenceAngle;

	public Camera(String ip, Zone zone, Position pos, double differenceAngle, boolean ptz) {
		this.IP = ip;
		this.differenceAngle = differenceAngle;

		this.zone = zone;
		this.pos = pos;
		this.ptz = ptz;
	}

	public abstract String getAddress();

	public boolean isPtz() {
		return ptz;
	}

	public void setPtz(boolean ptz) {
		this.ptz = ptz;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public void setPosition(Position pos) {
		this.pos = pos;
	}

	public abstract void setAddress(String address);

	public abstract boolean openStream();

	public abstract boolean isOpened();

	public abstract void getFrame();

	public abstract Image getActualFrame();

	public Position getPos() {
		return pos;
	}

	@Override
	public abstract void followTag(Position tagPos);

	public String getIP() {
		return IP;
	}

	public double getDiff() {
		return this.differenceAngle;
	}

}
