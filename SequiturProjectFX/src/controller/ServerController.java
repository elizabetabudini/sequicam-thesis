package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.scene.image.Image;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import model.Camera;
import model.Device;
import model.Position;
import model.World;
import model.Zone;
import utils.Configure;

import org.opencv.core.Core;

/**
 *
 * @author Utente
 */
public class ServerController {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.load(
				"C:\\Users\\Utente\\Documents\\NetBeansProjects\\SequiturProject\\lib\\opencv_videoio_ffmpeg411_64.dll");
		System.load("C:\\Users\\Utente\\Documents\\NetBeansProjects\\SequiturProject\\lib\\opencv_java411.dll");
	}
	private String lastUIDMapped = null;
	private String lastAddress = "";
	private String lastZone = "";
	public List<Camera> cameraList = new ArrayList<Camera>();
	private String serveraddr = "192.168.28.1:8688";

	public void setLastAddress(String uid) throws IOException {
		List<String> zones = this.getPosition(uid).getZones();
		if (!cameraList.isEmpty()) {
			for (Camera cam : cameraList) {
				if (zones.contains(cam.getZone().getName())) {
					this.lastAddress = cam.getAddress();
					this.lastZone = cam.getZone().getName();
					return;
				}
			}
		} else {
			if (!zones.isEmpty()) {
				lastZone = zones.get(0);
			}

		}

		this.lastAddress = "";
		this.lastZone = "";

	}

	public List<Device> getTags(Integer worldUID) throws IOException {
		ArrayList<Device> devlist = new ArrayList<>();
		URL url = new URL("http://" + serveraddr + "/api/dev/alltags/" + worldUID + "?query=info");
		try {
			Gson gson = new Gson();
			InputStreamReader reader = new InputStreamReader(url.openStream());

			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			jsonObject.get("device"); // returns a JsonElement for that name

			JsonArray jsonArray = (JsonArray) jsonObject.get("device");
			if (jsonArray != null) {
				int len = jsonArray.size();
				for (int i = 0; i < len; i++) {
					jsonArray.get(i).getAsJsonObject();
					Device dev = gson.fromJson(jsonArray.get(i).getAsJsonObject().toString(), Device.class);
					devlist.add(dev); // add dev object for each device detected
					
				}
			}
		} catch (IOException e) {
			System.err.println("Impossible to open URL: " + url.getPath());
		}
		return devlist;

	}

	public List<World> getWorlds() throws IOException {
		ArrayList<World> worldsList = new ArrayList<>();
		URL url = new URL("http://" + serveraddr + "/api/sys/environment");
		try {
			Gson gson = new Gson();
			InputStreamReader reader = new InputStreamReader(url.openStream());

			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			jsonObject.get("worlds"); // returns a JsonElement for that name
			JsonArray jsonArray = (JsonArray) jsonObject.get("worlds");

			if (jsonArray != null) {
				int len = jsonArray.size();
				for (int i = 0; i < len; i++) {
					jsonArray.get(i).getAsJsonObject();
					World wd = gson.fromJson(jsonArray.get(i).getAsJsonObject().toString(), World.class);
					worldsList.add(wd); // add dev object for each device detected
				}
			}
		} catch (IOException e) {
			System.err.println("Impossible to open URL: " + url);
			System.err.println("You can modify field serveraddr: "+serveraddr+" in Controller class");
			System.exit(0);
		}
		return worldsList;

	}

	public Position getPosition(String devUID) throws IOException {
		Gson gson = new Gson();
		URL url = new URL("http://" + serveraddr + "/api/dev/uid/" + devUID + "?query=pos");
		InputStreamReader reader = new InputStreamReader(url.openStream());

		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		JsonArray jsonArray = (JsonArray) jsonObject.get("device");
		if (jsonArray != null) {
			int len = jsonArray.size();
			for (int i = 0; i < len;) {
				jsonArray.get(i).getAsJsonObject().get("pos");
				Position pos = gson.fromJson(jsonArray.get(i).getAsJsonObject().get("pos").toString(), Position.class);
				return pos;
			}
		}
		return null;

	}

	public void addDefaultCameras() {
//		try {
////			this.cameraList.add(new Camera("192.168.0.112",
////					new Zone("def", 6), new Position(0, 0, 0),0.0, false));
////			this.cameraList.add(new Camera("192.168.0.114",
////					new Zone("def", 2), new Position(0, 0, 0),0.0, false));
////			 this.cameraList.add(new Camera("192.168.200.100", new
////			 Zone("def", 0), new Position(8.3,3.4,3.0),0.0, true));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	public Image getMap(String UID) throws IOException {
		lastUIDMapped = UID;
		Image image = new Image("http://" + serveraddr + "/api/dev/img/uid/" + UID);
		return image;
	}

	public String getLastUIDMapped() {
		return lastUIDMapped;
	}

	public String getLastAddress() {
		return lastAddress;
	}

	public String getLastZone() {
		return lastZone;
	}

	public List<Camera> getCameraList() {
		return this.cameraList;
	}

	public void setCameraList(List<Camera> list) {
		this.cameraList = list;
	}

	public void addCamera(Camera cam) {
		cameraList.add(cam);
	}

	public void removeCamera(Camera cam) {
		cameraList.remove(cam);
	}

	public ArrayList<Zone> getZones() {
		ArrayList<Zone> devlist = new ArrayList<>();
		try {
			Gson gson = new Gson();
			URL url = new URL("http://" + serveraddr + "/api/zone/" + Configure.zone_family);
			InputStreamReader reader = new InputStreamReader(url.openStream());

			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			// returns a JsonElement for that name
			// System.out.println(jsonObject.get("zones"));

			JsonArray jsonArray = (JsonArray) jsonObject.get("zones");
			if (jsonArray != null) {
				int len = jsonArray.size();
				for (int i = 0; i < len; i++) {
					jsonArray.get(i).getAsJsonObject();
					Zone dev = gson.fromJson(jsonArray.get(i).getAsJsonObject().toString(), Zone.class);
					devlist.add(dev); // add dev object for each device detected
				}
			}
		} catch (IOException e) {
			System.err.println("Impossible to open URL, connect to server first");
		}
		return devlist;
	}

}
