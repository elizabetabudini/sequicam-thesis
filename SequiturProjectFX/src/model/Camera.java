package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private String IP;
	private double differenceAngle;

	private Image img = null;

	public Camera(String ip, Zone zone, Position pos, double differenceAngle, boolean ptz) throws IOException {
		this.IP = ip;
		this.differenceAngle=differenceAngle;
		if(ptz) {
			
			this.streamAddress="http://"+ip+"/axis-cgi/mjpg/video.cgi?";
			/*try {
				URL url = new URL(this.streamAddress);
				String encoding = Base64.getEncoder().encodeToString(("root:admin").getBytes("UTF-8"));

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic " + encoding);
				InputStream content = (InputStream) connection.getInputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = in.readLine()) != null) {
					 System.out.println(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			/*try {
	             
	            // Sets the authenticator that will be used by the networking code
	            // when a proxy or an HTTP server asks for authentication.
	            Authenticator.setDefault(new CustomAuthenticator());
	             
	            URL url = new URL(this.streamAddress);
	             
	            // read text returned by server
	            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	             
	            String line;
	            while ((line = in.readLine()) != null) {
	                System.out.println(line);
	            }
	            in.close();
	             
	        }
	        catch (MalformedURLException e) {
	            System.out.println("Malformed URL: " + e.getMessage());
	        }
	        catch (IOException e) {
	            System.out.println("I/O Error: " + e.getMessage());
	        }*/
			

		} else {
			this.IP = ip;
			this.streamAddress="http://"+ip+"/cgi-bin/hi3510/snap.cgi?&-getstream&-chn=2";
		}
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
						vCapture.grab();
						vCapture.retrieve(mat);
						if (!mat.empty())
							img = Utils.mat2Image(mat);

					}
				} else {
					System.out.println("VideoCapture is null, remember to call openStream() before. Cam streamAddress: "
							+ streamAddress);
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
		return "Camera [streamAddress=" + streamAddress + ", zone=" + zone + ", pos=" + pos + ", ptz=" + ptz + "]";
	}

	public void followTag(Position tagPos) {
		if (this.ptz) {
			try {
				double pan=Utils.getPanAngle(tagPos, this.pos, this.differenceAngle);
				double tilt=Utils.getTiltAngle(tagPos, this.pos);
				System.out.println("pan= "+pan+" tilt="+tilt);
		
				URL url = new URL("http://"+IP+"/axis-cgi/com/ptz.cgi?pan="+pan+"&tilt="+tilt);
				String encoding = Base64.getEncoder().encodeToString(("root:admin").getBytes("UTF-8"));

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic " + encoding);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	public String getIP() {
		return IP;
	}
	public double getDiff() {
		return this.differenceAngle;
	}

}


