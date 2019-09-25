package model;

public class Device {

	private String uid;
	private boolean valid;
	private Info info;

	@Override
	public String toString() {
		return "Alias: " + this.info.getAlias() + ", UID: " + this.uid + ", IP_address: " + this.info.getIp_address();

	}

	public String getUid() {
		return uid;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

}
