package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Position;

/**
 * Data conversion
 *
 * 
 */
public class Utils {
	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	public static Image mat2Image(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		} catch (Exception e) {
			System.err.println("Cannot convert the Mat obejct: " + e);
			return null;
		}
	}

	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property a {@link ObjectProperty}
	 * @param value    the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(() -> {
			property.set(value);
		});
	}

	/**
	 * Support for the {@link mat2image()} method
	 * 
	 * @param original the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 */
	private static BufferedImage matToBufferedImage(Mat original) {
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

	/**
	 * Method for retrieving the pan angle
	 * 
	 * @param tagPos          device position (x, y, z)
	 * @param cameraPos       camera position (x, y, z)
	 * @param differenceAngle angle in degrees between camera's reference system and
	 *                        world's reference system
	 * @return pan angle in degrees
	 */
	public static double getPanAngle(Position tagPos, Position cameraPos, double differenceAngle) {
		double angle = 0.0;
		double distanceX = Math.abs(cameraPos.getX() - tagPos.getX());
		double distanceY = Math.abs(cameraPos.getY() - tagPos.getY());

		if (tagPos.getX() < cameraPos.getX()) {
			angle = Math.toDegrees(Math.atan(distanceY / distanceX));
		} else if (tagPos.getX() > cameraPos.getX()) {
			if (tagPos.getY() == cameraPos.getY()) {
				angle = 180.0;
			} else {
				angle = Math.toDegrees(Math.atan(distanceX / distanceY)) + 90.0;
			}
		} else {
			angle = 90.0;
		}

		if (tagPos.getY() > cameraPos.getY()) {
			return angle - differenceAngle;
		} else {
			return -angle - differenceAngle;
		}

	}

	/**
	 * Method for retrieving the tilt angle
	 * 
	 * @param tagPos    
	 * 			device position (x, y, z)
	 * @param cameraPos 
	 * 			camera position (x, y, z)
	 * @return 
	 * 			tilt angle in degrees
	 */
	public static double getTiltAngle(Position tagPos, Position cameraPos) {
		double angle = 0.0;
		if (cameraPos.getX() == tagPos.getX()) {
			angle = 90.0;
		} else {
			double distanceX = Math.abs(cameraPos.getX() - tagPos.getX());
			double distanceZ = Math.abs(cameraPos.getZ() - tagPos.getZ());
			angle = Math.toDegrees(Math.atan(distanceZ / distanceX));

		}
		return -angle;

	}

}
