package controller;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class PlayVideo extends JFrame {
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static { 
		   System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		   System.load("C:\\Users\\Utente\\Documents\\Workspace\\sequiturProject\\lib\\opencv_videoio_ffmpeg411_64.dll");
	        System.load("C:\\Users\\Utente\\Documents\\Workspace\\sequiturProject\\lib\\opencv_java411.dll");
	   }
	   static VideoCapture vCapture;
	   static Mat mat;
	   static ImageIcon image;
	   

	   public static void main(String[] args) {
		   image=new ImageIcon();
		   JLabel lblimg= new JLabel();
	    	  JFrame frame= new JFrame();
	    	  frame.setSize(500, 500);
	    	  frame.add(lblimg);
	    	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	  frame.setVisible(true);
		   
		    String camera  = "rtsp://192.168.200.101:554/11";
		    VideoCapture vCapture = new VideoCapture();
		    if (vCapture.open(camera)) {
		        System.out.println("Camera opened from " + camera);
		    } else {
		        System.out.println("No camera found at " + camera);
		    }
	      mat = new Mat();

	      if (!vCapture.isOpened()) {
	          System.out.println("media failed to open");
	      } else {    
	    	  
	          while (vCapture.grab()) {
	              vCapture.retrieve(mat);
	              lblimg.setIcon(new ImageIcon(Mat2BufferedImage(mat)));
	          }
	          vCapture.release();
	      }
	   }
	   
	    
	    public static BufferedImage Mat2BufferedImage(Mat m) {

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
	    
	    
	}
