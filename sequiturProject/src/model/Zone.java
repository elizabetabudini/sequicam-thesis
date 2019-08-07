package model;

import java.util.List;

public class Zone {
	private String color;
    private Boolean edit;
    private String name;
    private int id;
    private String type;
    private List<List<Integer>> points;
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
	public List<List<Integer>> getPoints() {
		return points;
	}

}
