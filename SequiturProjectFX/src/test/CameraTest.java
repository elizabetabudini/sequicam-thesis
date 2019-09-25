package test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.Test;

import controller.Controller;
import model.AXISCamera;
import model.World;

public class CameraTest {
	final static Controller con = new Controller("");

	@Test
	public void addDefaultCameras() throws NumberFormatException, IOException {
		con.addDefaultCameras();
		assertFalse(con.getCameraList().isEmpty());
	}

	@Test
	public void addCamera() throws IOException {
		int size = con.getCameraList().size();
		con.addCamera(new AXISCamera(null, null, null, 0, false));
		assertEquals(con.getCameraList().size(), size + 1);
	}

	@Test
	public void removeCamera() throws IOException {
		con.addDefaultCameras();
		int size = con.getCameraList().size();
		con.removeCamera(con.getCameraList().get(0));
		assertEquals(con.getCameraList().size(), size - 1);
	}

	@Test
	public void actualWorld() throws IOException {
		World wd = new World();
		wd.setId(12);
		con.setActualWorld(wd);
		assertEquals(con.getActualWorld().getId(), new Integer(12));
		assertFalse(con.getActualWorld().equals(null));
	}

}
