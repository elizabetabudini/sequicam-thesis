package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.ConcurrentLinkedQueue;

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
		double X = tagPos.getX() - cameraPos.getX();
		double Y = tagPos.getY() - cameraPos.getY();
		if (!(X == 0 && Y == 0)) {
			angle = Math.toDegrees(Math.atan2(Y, X));
		}
		angle = -angle; // perch� i 2 quadranti positivi dell'atan2 sono i negativi della videocamera
		angle += differenceAngle;
		if (angle > 180) {
			double s = angle - 180;
			angle = -180 + s;
//			angle-=360;
		} else if (angle < -180) {
			double s = -180 - angle;
			angle = 180 - s;
//			angle+=360;
		}
		return angle;

	}

	/**
	 * Method for retrieving the tilt angle
	 * 
	 * @param tagPos    device position (x, y, z)
	 * @param cameraPos camera position (x, y, z)
	 * @return tilt angle in degrees
	 */
	public static double getTiltAngle(Position tagPos, Position cameraPos) {
		double angle = 0.0;
		double d = Math
				.sqrt((Math.pow(cameraPos.getX() - tagPos.getX(), 2) + Math.pow(cameraPos.getY() - tagPos.getY(), 2)));
		if (d == 0) {
			angle = 90.0;
		} else {
			double distanceZ = Math.abs(cameraPos.getZ() - tagPos.getZ());
			angle = Math.toDegrees(Math.atan(distanceZ / d));

		}
		return -angle;

	}

	public static Position averagePos(ConcurrentLinkedQueue<Position> lastPositions) {
		double sumx = 0;
		double sumy = 0;
		double sumz = 0;
		double x = 0;
		double y = 0;
		double z = 0;
		for (Position position : lastPositions) {
			sumx += position.getX();
			sumy += position.getY();
			sumz += position.getZ();
		}
		x = sumx / lastPositions.size();
		y = sumy / lastPositions.size();
		z = sumz / lastPositions.size();
		return new Position(x, y, z);
	}

}
