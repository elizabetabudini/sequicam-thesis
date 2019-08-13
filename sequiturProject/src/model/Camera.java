package model;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import controller.Controller;

public class Camera {
	private String address;
	private VideoCapture vCapture;

	public Camera(String address) {
		this.address = address;
		vCapture = new VideoCapture();
	}
	
	public String getAddress() {
		return address;
	}
	
	public void openStream() {
		if (vCapture.open(this.address)) {
			System.out.println("Camera opened from " + this.address);
		} else {
			System.out.println("No camera found at " + this.address);
		}
		return;
	}
	public boolean isOpened() {
		if(vCapture != null) {
			return vCapture.isOpened();
		}else { 
			return false;
		}
	}
	
	public BufferedImage getFrame() {
		Mat mat = new Mat();
		BufferedImage img=null;
		if(vCapture != null) {
			if (!vCapture.isOpened()) {
				System.out.println("Media failed to open, cam address: "+ this.address);
			} else {
				if (vCapture.grab()) {
					vCapture.retrieve(mat);
					img = Controller.Mat2BufferedImage(mat);
				}
				
			}
		} else {
			System.out.println("VideoCapture is null, remember to call openStream() before. Cam address: "+this.address);
		}
		return img;
		
	}
	
	public void release() {
		if(vCapture != null) {
				vCapture.release();
		}
	}

	

}
