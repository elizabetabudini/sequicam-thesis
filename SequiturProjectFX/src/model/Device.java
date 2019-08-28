package model;

public class Device {
    
    private Boolean configured;
    private String[] errorsMsg;
    private Boolean alive; //true
    private String color;
    private String shape;
    private String[] errorsCodes;
    private long runtime;
    private String[] warningsMsg;
    private String ip_address;
    private long type;// 1, 3 sono tag
    private String uid;
    private long nodestate;
    private String alias;
    private long appcode;
    private String[] warningsCodes;
    private long family;
    private String firmware;
    private String hardware;
	public Boolean getConfigured() {
		return configured;
	}
	public String[] getErrorsMsg() {
		return errorsMsg;
	}
	public Boolean getAlive() {
		return alive;
	}
	public String getColor() {
		return color;
	}
	public String getShape() {
		return shape;
	}
	public String[] getErrorsCodes() {
		return errorsCodes;
	}
	public long getRuntime() {
		return runtime;
	}
	public String[] getWarningsMsg() {
		return warningsMsg;
	}
	public String getIp_address() {
		return ip_address;
	}
	public long getType() {
		return type;
	}
	public String getUid() {
		return uid;
	}
	public long getNodestate() {
		return nodestate;
	}
	public String getAlias() {
		return alias;
	}
	public long getAppcode() {
		return appcode;
	}
	public String[] getWarningsCodes() {
		return warningsCodes;
	}
	public long getFamily() {
		return family;
	}
	public String getFirmware() {
		return firmware;
	}
	public String getHardware() {
		return hardware;
	}
	@Override
    public String toString() {
		return "Alias: " + this.alias + ", UID: "+ this.uid + ", IP_address: "+ this.ip_address;
		
	}

}
