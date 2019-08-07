package model;

public class Device {
    
    private Boolean configured;
    private String[] errorsMsg;
    private Boolean alive;
    private String color;
    private String shape;
    private String[] errorsCodes;
    private int runtime;
    private String[] warningsMsg;
    private String ip_address;
    private int type;
    private String uid;
    private int nodestate;
    private String alias;
    private int appcode;
    private String[] warningsCodes;
    private int family;
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
	public int getRuntime() {
		return runtime;
	}
	public String[] getWarningsMsg() {
		return warningsMsg;
	}
	public String getIp_address() {
		return ip_address;
	}
	public int getType() {
		return type;
	}
	public String getUid() {
		return uid;
	}
	public int getNodestate() {
		return nodestate;
	}
	public String getAlias() {
		return alias;
	}
	public int getAppcode() {
		return appcode;
	}
	public String[] getWarningsCodes() {
		return warningsCodes;
	}
	public int getFamily() {
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
