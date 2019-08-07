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
		pNorth.add(new JLabel("Current Data"));

		String col[] = { "Alias", "IP", "UID", "X", "Y", "Z", "Zone", "Telecamera attiva" };
		DefaultTableModel tableModel = new DefaultTableModel(col, 0);
		tableModel.addRow(col);
		JTable table = new JTable(tableModel);
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(1).setMinWidth(110);
		table.getColumnModel().getColumn(2).setMinWidth(110);
		table.getColumnModel().getColumn(7).setMinWidth(100);
		pCenter.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 20));
		pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.Y_AXIS));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		pCenter.add(table);
		JLabel label = new JLabel("Follow target");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		//label.setAlignmentX(Component.CENTER_ALIGNMENT);
		pCenter.add(label);
		JLabel lblimg= new JLabel();
		JLabel lblflw= new JLabel();
		lblimg.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		lblflw.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		lblimg.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblflw.setAlignmentX(Component.CENTER_ALIGNMENT);
		List<Device> list = c.getDevices();
		
		ActionListener task2 = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	try {
				    lblflw.setText("Following target: "+c.getLastUIDMapped());
                    lblimg.setIcon(new ImageIcon(c.getMap(c.getLastUIDMapped())));
	            	
            	} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
            }
		};
		Timer timer2 = new Timer(50 ,task2); // Execute task each 100 miliseconds
		timer2.setRepeats(true);
		
/*
 * Add buttons to follow each device*/
		/*for (Device device : list) {
			JButton btn = new JButton(device.getUid());
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			pCenter.add(btn);
			btn.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	try {
		            	JButton bt = (JButton)e.getSource();
					    lblflw.setText("Following target: "+bt.getText());
	                    lblimg.setIcon(new ImageIcon(c.getMap(bt.getText())));
	                    timer2.start();
	            	} catch (IOException e1) {
						// TODO Auto-generated catch block
					}
	            	
	            }
	        });
			
		}*/

/*
		* Add buttons to get stream for each device*/
		for (Device device : list) {
			JButton btn = new JButton(device.getUid());
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			pCenter.add(btn);
			btn.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	JButton bt = (JButton)e.getSource();
					lblflw.setText("Get camera stream target: "+bt.getText());
					c.getStreamFromCamera("rtsp://192.168.200.101:554/11");
					//timer2.start();
	            }
	        });
			
		}
		
		pCenter.add(lblflw);
		pCenter.add(lblimg);
		
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
						Position p = c.getPosition(device.getUid());
						// lista di zone nel mondo
						Object[] objs = { device.getAlias(), device.getIp_address(), device.getUid(), p.getX(),
								p.getY(), p.getZ(), p.getZones() };
						tableModel.addRow(objs);
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}
            }
		};
		Timer timer = new Timer(500 ,task); // Execute task each 100 miliseconds
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
