package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javafx.scene.image.Image;
import utils.Utils;

public class SV3CCamera extends Camera {
	private String streamAddress;
	private VideoCapture vCapture;
	private Zone zone;
	private Position pos;
	private boolean ptz;
	private String IP;
	private double differenceAngle;

	private Image img = null;

	public SV3CCamera(String ip, Zone zone, Position pos, double differenceAngle, boolean ptz) {
		super(ip, zone, pos, differenceAngle, ptz);
		this.streamAddress = "http://" + ip + "/cgi-bin/hi3510/snap.cgi?&-getstream&-chn=2";
		vCapture = new VideoCapture();
	}

	public String getAddress() {
		return streamAddress;
	}

	public void setAddress(String address) {
		this.streamAddress = address;
	}

	@Override
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

	public String toString() {
		return "AXISCamera [streamAddress=" + streamAddress + ", zone=" + zone + ", pos=" + pos + ", ptz=" + ptz + "]";
	}

	@Override
	public void followTag(Position tagPos) {
		if (this.ptz) {
			try {
				double pan = Utils.getPanAngle(tagPos, this.pos, this.differenceAngle);
				double pan_t = BigDecimal.valueOf(pan).setScale(2, RoundingMode.HALF_UP).doubleValue();
				double tilt = Utils.getTiltAngle(tagPos, this.pos);
				double tilt_t = BigDecimal.valueOf(tilt).setScale(2, RoundingMode.HALF_UP).doubleValue();
				URL urlpantilt = new URL("http://" + IP + "/axis-cgi/com/ptz.cgi?pan=" + pan_t + "&tilt=" + tilt_t);
				System.out.println(urlpantilt);
				String encoding = Base64.getEncoder().encodeToString(("root:admin").getBytes("UTF-8"));
				HttpURLConnection connection = (HttpURLConnection) urlpantilt.openConnection();

				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setRequestProperty("Authorization", "Basic " + encoding);
				connection.getInputStream();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean isOpened() {
		{
			if (vCapture != null) {
				return vCapture.isOpened();
			} else {
				return false;
			}
		}
	}

}
