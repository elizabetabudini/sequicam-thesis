package model;

public class Zone {
	private String color;
    private Boolean edit;
    private String name;
    private int id;
    private String type;
    //private List<List<Integer>> points;
	
    public Zone(String string, int ID) {
		this.name=string;
		this.id=ID;
	}
	public String getColor() {
		return color;
	}
	public Boolean getEdit() {
		return edit;
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public String getType() {
		return type;
	}
//	public List<List<Integer>> getPoints() {
//		return points;
//	}
	@Override
	public String toString() {
		return "Zone [name=" + name + ", id=" + id + "]";
	}


}
