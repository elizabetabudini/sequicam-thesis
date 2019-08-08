package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		final JPanel pCenterCam = new JPanel();
		final JPanel pSouth = new JPanel();
		pNorth.add(new JLabel("Current Data"));
		final JFrame framecam = new JFrame("Camera Stream");
		JButton close = new JButton("Close");
		pSouth.add(close);
		close.addActionListener(e->{
			framecam.setVisible(false);
		});
		
		
		framecam.setSize(600, 400);
		framecam.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		framecam.getContentPane().setLayout(new BorderLayout());
		framecam.getContentPane().add(pCenterCam, BorderLayout.CENTER);
		framecam.getContentPane().add(pSouth, BorderLayout.SOUTH);
		

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
		//label.setAlignmentX(Component.CENTER_ALIGNMENT);
		pCenter.add(label);
		JLabel lblimg= new JLabel();
		JLabel lblflw= new JLabel();
		JLabel lblcam= new JLabel();
		lblimg.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		lblflw.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		lblimg.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblflw.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblcam.setAlignmentX(Component.CENTER_ALIGNMENT);
		List<Device> list = c.getDevices();
		if(!list.isEmpty()) {
			lblflw.setText("Following target: "+list.get(0).getUid());
	        lblimg.setIcon(new ImageIcon(c.getMap(list.get(0).getUid())));
		}
		ActionListener task2 = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	try {
				    lblflw.setText("Following target: "+c.getLastUIDMapped());
                    lblimg.setIcon(new ImageIcon(c.getMap(c.getLastUIDMapped())));
                   // lblcam.setIcon(new ImageIcon(c.getStreamFromCamera("rtsp://192.168.200.101:554/12")));
	            	
            	} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
            }
		};
		Timer timer2 = new Timer(500,task2); //show map with target
		timer2.setRepeats(true);
		
		ActionListener task3 = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	//lblcam.setIcon(new ImageIcon(c.getStreamFromCamera("rtsp://192.168.200.101:554/12")));
            }
		};
		Timer timer3 = new Timer(10,task3); //show camera
		timer3.setRepeats(true);
		
/*
 * Add buttons to follow each device*/
		for (Device device : list) {
			JButton btn = new JButton(device.getUid());
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			pCenter.add(btn);
			btn.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	try {
		            	JButton bt = (JButton)e.getSource();
					    lblflw.setText("Following target: "+bt.getText());
	                    lblimg.setIcon(new ImageIcon(c.getMap(bt.getText())));
	                    // lblcam.setIcon(new ImageIcon(c.getStreamFromCamera("rtsp://192.168.200.101:554/12")));
	                    //framecam.setVisible(true);
	                    
		                c.getStreamFromUID(btn.getText());

	                    timer2.start();
	                    //timer3.start();
	            	} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
	            	
	            }
	        });
			
		}

/*
		* Add buttons to get stream for each device*/
		/*for (Device device : list) {
			JButton btn = new JButton(device.getUid());
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			pCenter.add(btn);
			btn.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	JButton bt = (JButton)e.getSource();
					lblflw.setText("Get camera stream target: "+bt.getText());
					c.getStreamFromCamera("rtsp://192.168.200.101:554/12");
					//timer2.start();
	            }
	        });
			
		}*/
		
		pCenter.add(lblflw);
		pCenter.add(lblimg);
		pCenterCam.add(lblcam);
		
/*
******* task to refresh current data table*/		
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
						String cam="Not visible";
						Position p = c.getPosition(device.getUid());
						if(p.getZones().contains("lab")) {
							cam="Camera PTZ-Lab";
							//c.getStreamFromUID(device.getUid());
						}
						if(p.getZones().contains("ufficio")) {
							cam="Camera 1-Ufficio";
							//c.getStreamFromUID(device.getUid());
						}
						if(p.getZones().contains("corridoio")) {
							cam="Camera 2-Corridoio";
							//c.getStreamFromUID(device.getUid());
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
		Timer timer = new Timer(500 ,task); //refresh data on screen
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
