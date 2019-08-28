package model;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javafx.scene.image.Image;
import utils.Utils;

public class Camera {
	private String streamAddress;
	private VideoCapture vCapture;
	private Zone zone;
	private Position pos;
	private boolean ptz;

	private Image img = null;

	public Camera(String address, Zone zone, Position pos, boolean ptz) {
		this.streamAddress = address;
		vCapture = new VideoCapture();
		this.zone = zone;
		this.pos = pos;
		this.ptz = ptz;
	}

	public String getAddress() {
		return streamAddress;
	}

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

	public void setAddress(String address) {
		this.streamAddress = address;
	}

	public boolean openStream() {
		if (vCapture.open(this.streamAddress)) {
			System.out.println("Camera opened from " + this.streamAddress);
			return true;
		} else {
			System.out.println(
					"No camera found at " + this.streamAddress + ", please check if the streamAddress is correct.");
			return false;
		}
	}

	public boolean isOpened() {
		if (vCapture != null) {
			return vCapture.isOpened();
		} else {
			return false;
		}
	}

	public void getFrame() {
		Mat mat = new Mat();
		Thread t = new Thread() {
		    public void run() {
		    	if (vCapture != null) {
					while (vCapture.isOpened()) {
						/*if (vCapture.grab()) {
							vCapture.retrieve(mat);
							img = Utils.mat2Image(mat);
						} else {
							System.out.println("Grab method didn't work ");
						}*/
						vCapture.grab();
						vCapture.retrieve(mat);
						if(!mat.empty())
						img = Utils.mat2Image(mat);

					}
				} else {
					System.out.println(
							"VideoCapture is null, remember to call openStream() before. Cam streamAddress: " + streamAddress);
				}
		    }
		};
		t.start();

	}
	public Image getActualFrame() {
		return this.img;
	}

	public Position getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return this.zone + "- " + this.streamAddress;

	}

	public void move(Integer direction) {
		if (this.ptz && direction >= 1 && direction <= 8) {
			try {
				URL url2 = new URL("http://192.168.200.100/cgi/ptz_set?Channel=1&Group=PTZCtrlInfo&Direction="
						+ direction + "&PanSpeed=2&TiltSpeed=3");
				// URL url2 = new URL
				// ("http://192.168.200.100/cgi/ptz_set?Channel=1&Group=PTZCtrlInfo&Model=0&Pan=0&Tilt=0&ZoomPosition=0");
				String encoding = Base64.getEncoder().encodeToString(("admin:admin").getBytes("UTF-8"));

				HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic " + encoding);
				/*InputStream content = (InputStream) connection.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = in.readLine()) != null) {
					// System.out.println(line);
				}*/
			} catch (Exception e) {
				System.out.println("Cannot move the camera");
				e.printStackTrace();
			}
		}

	}

	public void stop() {
		if (this.ptz) {
			try {
				URL url2 = new URL("http://192.168.200.100/cgi/ptz_set?Channel=1&Group=PTZCtrlInfo&Stop=0");
				// URL url2 = new URL
				// ("http://192.168.200.100/cgi/ptz_set?Channel=1&Group=PTZCtrlInfo&Model=0&Pan=0&Tilt=0&ZoomPosition=0");
				String encoding = Base64.getEncoder().encodeToString(("admin:admin").getBytes("UTF-8"));

				HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic " + encoding);
				/*InputStream content = (InputStream) connection.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = in.readLine()) != null) {
					// System.out.println(line);
				}
				*/
			} catch (Exception e) {
				System.out.println("Cannot stop the camera");
				e.printStackTrace();
			}
		}

	}

}
