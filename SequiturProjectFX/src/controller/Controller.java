package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.scene.image.Image;

import java.io.File;
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
import utils.ConfigurationProperties;

import org.opencv.core.Core;

/**
 *
 * @author Utente
 */
public class Controller {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File lib = new File("../SequiturProjectFX/lib/" + System.mapLibraryName("opencv_videoio_ffmpeg411_64"));
		System.load(lib.getAbsolutePath());
		File lib2 = new File("../SequiturProjectFX/lib/" + System.mapLibraryName("opencv_java411"));
		System.load(lib2.getAbsolutePath());
	}
	private String lastUIDMapped = null;
	private String lastAddress = "";
	private String lastZone = "";
	private List<Camera> cameraList = new ArrayList<Camera>();
	private String serveraddr;
	private World actualWorld;

	public Controller(String serveraddr) {
		this.serveraddr = serveraddr;
	}

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
			System.exit(0);
		}
		return worldsList;

	}

	public Position getPosition(String devUID) {
		if (devUID == null) {
			return null;
		}
		URL url = null;
		try {
			Gson gson = new Gson();
			url = new URL("http://" + serveraddr + "/api/dev/uid/" + devUID + "?query=pos");
			InputStreamReader reader = new InputStreamReader(url.openStream());

			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
			JsonArray jsonArray = (JsonArray) jsonObject.get("device");
			if (jsonArray != null) {
				int len = jsonArray.size();
				for (int i = 0; i < len;) {
					jsonArray.get(i).getAsJsonObject().get("pos");
					Position pos = gson.fromJson(jsonArray.get(i).getAsJsonObject().get("pos").toString(),
							Position.class);
					return pos;
				}
			}
		} catch (IOException e) {
			System.err.println("Impossible to open URL: " + url);
		}
		return null;

	}

	public void addDefaultCameras() throws NumberFormatException, IOException {
		this.cameraList=ConfigurationProperties.getProperties();

	}

	public Image getMap(String UID) {
		lastUIDMapped = UID;
		Image image = null;
		try {
			image = new Image("http://" + serveraddr + "/api/dev/img/uid/" + UID);
		} catch (IllegalArgumentException e) {

		}
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

	public ArrayList<Zone> getZones(String zoneset) {
		ArrayList<Zone> devlist = new ArrayList<>();
		URL url = null;
		try {
			Gson gson = new Gson();
			url = new URL("http://" + serveraddr + "/api/zone/" + zoneset);
			System.out.println(url);
			InputStreamReader reader = new InputStreamReader(url.openStream());

			JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

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
			System.err.println("Impossible to open URL for getZones(): " + url);
		}
		return devlist;
	}

	public void openCameras() {
		List<Camera> list = this.getCameraList();
		if (!list.isEmpty()) {
			for (Camera c : list) {
				if (!c.isOpened()) {
					c.openStream();
					c.getFrame();
				}
			}
		}
	}

	public World getActualWorld() {
		return actualWorld;
	}

	public void setActualWorld(World actualWorld) {
		this.actualWorld = actualWorld;
	}

}
