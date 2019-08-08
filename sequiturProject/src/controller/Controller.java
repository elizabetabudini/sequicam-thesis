package controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.Device;
import model.Position;
import model.Zone;

public class Controller {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.load("C:\\Users\\Utente\\Documents\\Workspace\\tesiProject\\sequiturProject\\lib\\opencv_videoio_ffmpeg411_64.dll");
		System.load("C:\\Users\\Utente\\Documents\\Workspace\\tesiProject\\sequiturProject\\lib\\opencv_java411.dll");
	}
	private String lastUIDMapped = null;

	public void getStreamFromUID(String UID) throws IOException {
		String rtspAddress="";
		if(this.getPosition(UID).getZones().contains("lab")) {
			rtspAddress= "rtsp://192.168.200.105:554/1/h264major"; //PTZ
		}
		if(this.getPosition(UID).getZones().contains("ufficio")) {
			rtspAddress="rtsp://192.168.200.103:554/12";
		}
		if(this.getPosition(UID).getZones().contains("corridoio")) {
			rtspAddress="rtsp://192.168.200.101:554/12";
		}
		
		if(rtspAddress != "") {
			VideoCapture vCapture = new VideoCapture();
			Mat mat = new Mat();
			final JFrame framecam = new JFrame("Camera Stream for target: "+UID);
			JButton close = new JButton("Close");
			final JPanel pCenterCam = new JPanel();
			final JPanel pSouth = new JPanel();
			pSouth.add(close);
			close.addActionListener(e->{
				framecam.setVisible(false);
			});
			
			framecam.setSize(600, 400);
			framecam.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			framecam.getContentPane().setLayout(new BorderLayout());
			framecam.getContentPane().add(pCenterCam, BorderLayout.CENTER);
			framecam.getContentPane().add(pSouth, BorderLayout.SOUTH);
			
			JLabel lblcam= new JLabel();
			lblcam.setAlignmentX(Component.CENTER_ALIGNMENT);
			pCenterCam.add(lblcam);

	        framecam.setVisible(true);
			
		    if (vCapture.open(rtspAddress)) {
		        //System.out.println("Camera opened from " + rtspAddress);
		    } else {
		        System.out.println("No camera found at " + rtspAddress);
		    }

	      if (!vCapture.isOpened()) {
		          System.out.println("media failed to open");
		      } else {    
		    	  
		          //while (vCapture.grab()) {
		    	  if (vCapture.grab()) {
		              vCapture.retrieve(mat);
	                  lblcam.setIcon(new ImageIcon(Mat2BufferedImage(mat)));
		          }
		          vCapture.release();
		      }
		}
		
   }
	   
	
	public BufferedImage Mat2BufferedImage(Mat m) {

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public List<Device> getDevices() throws IOException {
		Gson gson = new Gson();
		URL url = new URL("http://192.168.28.1:8688/api/dev");
		InputStreamReader reader = new InputStreamReader(url.openStream());

		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		jsonObject.get("device"); // returns a JsonElement for that name
		ArrayList<Device> devlist = new ArrayList<Device>();

		JsonArray jsonArray = (JsonArray) jsonObject.get("device");
		if (jsonArray != null) {
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				jsonArray.get(i).getAsJsonObject();
				Device dev = gson.fromJson(jsonArray.get(i).getAsJsonObject().toString(), Device.class);
				devlist.add(dev); // add dev object for each device detected
			}
		}
		return devlist;

	}

	public Position getPosition(String UID) throws IOException {
		Gson gson = new Gson();
		URL url = new URL("http://192.168.28.1:8688/api/dev/uid/" + UID + "?query=pos");
		InputStreamReader reader = new InputStreamReader(url.openStream());

		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		JsonArray jsonArray = (JsonArray) jsonObject.get("device");
		if (jsonArray != null) {
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				jsonArray.get(i).getAsJsonObject().get("pos");
				Position pos = gson.fromJson(jsonArray.get(i).getAsJsonObject().get("pos").toString(), Position.class);
				return pos;
			}
		}
		return null;

	}

	public BufferedImage getMap(String UID) throws IOException {
		lastUIDMapped = UID;
		URL url = new URL("http://192.168.28.1:8688/api/dev/img/uid/" + UID);
		BufferedImage resizedImg = new BufferedImage(350, 400, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(ImageIO.read(url), 0, 0, 350, 400, null);
		g2.dispose();
		return resizedImg;
	}

	public Zone getZoneDetails(String zoneName) throws IOException {
		//Gson gson = new Gson();
		//URL url = new URL("http://192.168.28.1:8688/api/dev/img/uid/" + zoneName);
		/*
		 * InputStreamReader reader = new InputStreamReader(url.openStream());
		 * 
		 * JsonObject jsonObject = gson.fromJson(reader, JsonObject.class); JsonArray
		 * jsonArray = (JsonArray) jsonObject.get("device"); if (jsonArray != null) {
		 * int len = jsonArray.size(); for (int i=0; i<len; i++){
		 * jsonArray.get(i).getAsJsonObject().get("pos"); Position pos =
		 * gson.fromJson(jsonArray.get(i).getAsJsonObject().get("pos").toString(),
		 * Position.class); return pos; } }
		 */
		return null;
	}

	public String getLastUIDMapped() {
		return lastUIDMapped;
	}

}
