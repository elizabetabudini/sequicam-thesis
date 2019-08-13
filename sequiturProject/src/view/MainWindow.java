package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import controller.Controller;
import model.Camera;
import model.Device;
import model.Position;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.awt.Component;

public class MainWindow {

	private static Controller c = new Controller();

	public static void main(String[] args) throws IOException {
		final JPanel pCenter = new JPanel();
		final JPanel pNorth = new JPanel();
		pNorth.add(new JLabel("Current Data"));

		String col[] = { "Alias", "IP", "UID", "X", "Y", "Z", "Zone", "Telecamera attiva" };
		DefaultTableModel tableModel = new DefaultTableModel(col, 0);
		tableModel.addRow(col);
		JTable table = new JTable(tableModel);
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(1).setMinWidth(110);
		table.getColumnModel().getColumn(2).setMinWidth(110);
		table.getColumnModel().getColumn(7).setMinWidth(120);
		pCenter.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 20));
		pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.Y_AXIS));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		pCenter.add(table);
		JLabel label = new JLabel("Get map and camera stream:");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		pCenter.add(label);
		JLabel lblimg = new JLabel();
		JLabel lblflw = new JLabel();
		lblimg.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		lblflw.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		lblimg.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblflw.setAlignmentX(Component.CENTER_ALIGNMENT);
		ActionListener task2 = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					lblflw.setText("Following target: " + c.getLastUIDMapped());
					lblimg.setIcon(new ImageIcon(c.getMap(c.getLastUIDMapped())));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		};
		Timer timer2 = new Timer(50, task2); // show map with target
		timer2.setRepeats(true);

		Camera cmLab = new Camera(Controller.LAB_ADDR);
		cmLab.openStream();
		Camera cmUfficio = new Camera(Controller.UFFICIO_ADDR);
		cmUfficio.openStream();
		Camera cmCorridoio = new Camera(Controller.CORRIDOIO_ADDR);
		cmCorridoio.openStream();

		/*class CameraLabThread extends Thread {
			public CameraLabThread() {
				super();
			}

			public void run() {
				cmLab.openStream();
			}

		}
		class CameraUfficioThread extends Thread {
			public CameraUfficioThread() {
				super();
			}

			public void run() {
				cmUfficio.openStream();

			}

		}
		class CameraCorridoioThread extends Thread {
			public CameraCorridoioThread() {
				super();
			}

			public void run() {
				cmCorridoio.openStream();
			}

		}*/

		class CameraThread2 extends Thread {
			public CameraThread2(String UID) {
				super(UID);
			}

			private volatile boolean flagChangeCam = true;
			private volatile boolean flag = true;
			JFrame framecam = new JFrame();
			JPanel pCenterCam = new JPanel();
			JPanel pSouth = new JPanel();
			JButton close = new JButton("Close Frame");

			public void stopRunning() {
				flag = false;
				framecam.dispose();
			}

			public void checkCam() {
				flagChangeCam = false;
			}

			public void run() {
				close.addActionListener(e -> {
					stopRunning();
				});

				pSouth.add(close);
				framecam.setSize(600, 400);
				framecam.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				framecam.getContentPane().setLayout(new BorderLayout());
				framecam.getContentPane().add(pCenterCam, BorderLayout.CENTER);
				framecam.getContentPane().add(pSouth, BorderLayout.SOUTH);
				JLabel lblcam = new JLabel();
				lblcam.setAlignmentX(Component.CENTER_ALIGNMENT);
				pCenterCam.add(lblcam);
				framecam.setVisible(true);
				while (flag) {
					String rtspAddress = c.getLastAddress();
					framecam.setTitle("Camera stream for target: " + getName() + " on cam: " + rtspAddress);
					flagChangeCam = true;
					if (rtspAddress != "") {
						Camera cam = null;
						String zone = c.getLastZone();
						if (zone == "lab") {
							cam = cmLab;
						}
						if (zone == "corridoio") {
							cam = cmCorridoio;
						}
						if (zone == "ufficio") {
							cam = cmUfficio;
						}
						while (flagChangeCam) {
							if (rtspAddress != c.getLastAddress()) {
								this.checkCam();
								break;
							}
							BufferedImage im = cam.getFrame();
							if (im != null) {
								lblcam.setIcon(new ImageIcon(im));
							}
							
						}
					} else {
						if (rtspAddress != c.getLastAddress()) {
							this.checkCam();
						} else {
							lblcam.setIcon(null);
							lblcam.setText("not visible");
						}
						

					}
				}
				
				lblcam.setIcon(null);
				lblcam.setText("thread run method finished");
			}

		}

		List<Device> list = c.getDevices();
		if (!list.isEmpty()) {
			lblflw.setText("Following target: " + list.get(0).getUid());
			lblimg.setIcon(new ImageIcon(c.getMap(list.get(0).getUid())));
			timer2.start();
		}

		/*
		 * Add buttons to follow each device
		 */
		for (Device device : list) {
			JButton btn = new JButton(device.getUid());
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			pCenter.add(btn);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						JButton bt = (JButton) e.getSource();
						lblflw.setText("Following target: " + bt.getText());
						lblimg.setIcon(new ImageIcon(c.getMap(bt.getText())));

						/*
						 * CameraCorridoioThread thC = new CameraCorridoioThread(); CameraUfficioThread
						 * thU = new CameraUfficioThread(); CameraLabThread thL = new CameraLabThread();
						 * thC.start(); thL.start(); thU.start();
						 */

						timer2.start();
						new CameraThread2(bt.getText()).start();

						/*
						 * if (th.getState() != Thread.State.RUNNABLE) { //not running
						 * th.setName(bt.getText()); th.start(); } else { final CameraThread2 th2=new
						 * CameraThread2(bt.getText()); th2.setName(bt.getText()); th2.start(); }
						 */

					} catch (IOException e1) {
						// TODO Auto-generated catch block
					}

				}
			});

		}
		pCenter.add(lblflw);
		pCenter.add(lblimg);

		/*
		 ******* task to refresh current data table
		 */
		ActionListener task = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					// clean table
					if (tableModel.getRowCount() > 0) {
						for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
							tableModel.removeRow(i);
						}
					}
					tableModel.addRow(col);
					for (Device device : list) {
						String cam = "Not visible";
						Position p = c.getPosition(device.getUid());
						if (p.getZones().contains("lab")) {
							cam = "Camera PTZ-Lab";
						}
						if (p.getZones().contains("ufficio")) {
							cam = "Camera 1-Ufficio";
						}
						if (p.getZones().contains("corridoio")) {
							cam = "Camera 2-Corridoio";
						}
						try {
							c.setLastAddress(c.getLastUIDMapped());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// lista di zone nel mondo
						Object[] objs = { device.getAlias(), device.getIp_address(), device.getUid(), p.getX(),
								p.getY(), p.getZ(), p.getZones(), cam };
						tableModel.addRow(objs);
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
			}
		};
		Timer timer = new Timer(200, task); // refresh data on screen
		timer.setRepeats(true);
		timer.start();

		final JFrame frame = new JFrame("Sequitur project");
		frame.setSize(900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(pCenter, BorderLayout.CENTER);
		frame.getContentPane().add(pNorth, BorderLayout.NORTH);
		frame.setVisible(true);
	}

}
