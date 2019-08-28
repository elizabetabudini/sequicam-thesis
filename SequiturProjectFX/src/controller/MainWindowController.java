package controller;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import org.opencv.core.Core;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Camera;
import model.Device;
import model.Position;
import model.World;
import utils.Utils;
import javafx.scene.control.Label;

import javafx.scene.control.TableView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import javafx.scene.control.TableColumn;

public class MainWindowController implements Initializable {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.load("C:\\Users\\Utente\\Documents\\Workspace\\SequiturProjectFX\\lib\\opencv_videoio_ffmpeg411_64.dll");
		System.load("C:\\Users\\Utente\\Documents\\Workspace\\SequiturProjectFX\\lib\\opencv_java411.dll");
	}
	@FXML
	private TitledPane tagListPanel;
	@FXML
	private TableView<Device> tagTableView;
	@FXML
	private TableColumn<Device, String> tagUIDColumn;
	@FXML
	private TableColumn<Device, String> tagIPColumn;
	
	@FXML
	private TitledPane camerasPanel;
	@FXML
	private TableView<Camera> camerasTableView;
	@FXML
	private TableColumn<Camera, String> coveredZoneColumn;
	@FXML
	private TableColumn<Camera, String> PTZcolumn;
	@FXML
	private TitledPane TagDetailsPanel;
	@FXML
	private Label xLabel;
	@FXML
	private Label yLabel;
	@FXML
	private Label zLabel;
	@FXML
	private Label zoneLabel;
	@FXML
	private Label cameraLabel;
	@FXML
	private TitledPane CameraPanel;
	@FXML
	private ChoiceBox<String> worldID;
	private ObservableList<Device> tagList;
	private ObservableList<Camera> cameraList;
	@FXML
	private ImageView currentFrame;
	@FXML
	private TitledPane mapPanel;
	@FXML
	private Button closeButton;
	@FXML
	private ImageView currentMap;
	@FXML
	private Label zonelbl;
	@FXML
	private Label cameralbl;
	@FXML
	private Button settings;
	private static final String NOT_VISIBLE = "Tag not visible, there's no camera in this zone";
	private static final String OUT_OF_ZONES = "Out of zones";
	
	private World actualWorld;

	// a timer for acquiring the video stream
	private Timer timer1 = new Timer();
	private Timer timer2 = new Timer();;
	private Controller c;
	String cam = "";

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		c = new Controller();
		TagDetailsPanel.setVisible(false);
		CameraPanel.setVisible(false);
		mapPanel.setVisible(false);
		
		// debug only
		c.setCameraList(new ArrayList<>());
		c.addDefaultCameras(); // debug
		try {
			actualWorld=c.getWorlds().get(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		openCameras();
		populateView();
		setListeners();
		if(!tagTableView.getItems().isEmpty()) {
			tagTableView.getSelectionModel().selectFirst();
		}
		
	}

	public void openCameras() {
		if (!c.getCameraList().isEmpty()) {
			for (Camera c : c.getCameraList()) {
				if (!c.isOpened()) {
					c.openStream();
					c.getFrame();
				}
			}
		}
	}

	/*
	 ******* task to refresh current data table
	 */
	public class updateCameraAddressTask extends TimerTask {
		@Override
		public void run() {
			Position p = null;
			try {
				p = c.getPosition(c.getLastUIDMapped());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				c.setLastAddress(c.getLastUIDMapped());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (p != null) {
				if (!p.getZones().isEmpty()) { //if the tag is inside the zones
					for (Camera c : c.getCameraList()) {
						if (p.getZones().contains(c.getZone().getName())) { // which camera has the actual zone covered
							cam = c.getZone().getName();
							final String camera = cam;
							Platform.runLater(() -> {
								cameralbl.setText(camera);
								zonelbl.setText(zoneLabel.getText());
							});
							return;
						}
					}
				} else {
					Platform.runLater(() -> {
						cameralbl.setText(OUT_OF_ZONES);
						zonelbl.setText(zoneLabel.getText());
					});
					return;
				}
				Platform.runLater(() -> {
					cameralbl.setText(NOT_VISIBLE);
					zonelbl.setText(zoneLabel.getText());
				});

			}
		}
	}

	// TimerTask updateData = new TimerTask() {
	public class updateDataTask extends TimerTask {

		@Override
		public void run() {
			if (c != null) {
				Device dev = tagTableView.getSelectionModel().getSelectedItem();
				Image img = null;
				Position pos = null;
				try {
					img = c.getMap(c.getLastUIDMapped());
					if(dev != null) {
						pos = c.getPosition(dev.getUid());
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (pos != null) {
					final String posX = String.valueOf(pos.getX());
					final String posY = String.valueOf(pos.getY());
					final String posZ = String.valueOf(pos.getZ());
					final String zones = String.valueOf(pos.getZones());
					Platform.runLater(() -> {
						xLabel.setText(posX);
						yLabel.setText(posY);
						zLabel.setText(posZ);
						zoneLabel.setText(zones);
						mapPanel.setText("Map for tag: " + c.getLastUIDMapped());
					});
				}

				if (img != null) {
					final Image imgToShow = img;
					Platform.runLater(() -> {
						currentMap.setImage(imgToShow);
					});
				}

			}
		}
	};

	class CameraTask extends TimerTask {

		private volatile boolean flagChangeCam = true;
		private volatile boolean flag = true;

		public void stopRunning() {
			flag = false;
			currentFrame.setImage(null);
		}

		public void checkCam() {
			flagChangeCam = false;
		}

		@Override
		public void run() {
			/*
			 * close.addActionListener(e -> { stopRunning(); });
			 */

			while (flag) {
				String rtspAddress = c.getLastAddress();
				flagChangeCam = true;
				if (rtspAddress != "") {
					currentFrame.setVisible(true);
					Camera cam = null;
					String zone = c.getLastZone();
					for (Camera camera : c.getCameraList()) {
						if (camera.getZone().getName() == zone) {
							cam = camera;
						}
					}
					while (flagChangeCam) {
						if (cameralbl.getText() == OUT_OF_ZONES || cameralbl.getText() == NOT_VISIBLE) {
							updateImageView(currentFrame, null);
						} else {
							if (rtspAddress != c.getLastAddress()) {
								break;
							}
							Image im = null;
							if (cam == null) {
								updateImageView(currentFrame, null);
							} else {
								if (cam.isPtz()) {
									cam.move((int) Math.random() * 8 + 1);
								}
								im = cam.getActualFrame();
							}

							if (im != null) {
								updateImageView(currentFrame, im);
							}
						}
						
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				this.checkCam();
			}

		}

	}

	private void setListeners() {
		tagTableView.getSelectionModel().selectedItemProperty().addListener((obVal, oldVal, newVal) -> {
			if (newVal != null) {
				TagDetailsPanel.setVisible(true);
				CameraPanel.setVisible(true);
				mapPanel.setVisible(true);
				TagDetailsPanel.setText("Device details: " + newVal.getUid());
				mapPanel.setText("Map for tag: " + newVal.getUid());
				try {
					c.getMap(newVal.getUid());
					c.setLastAddress(c.getLastUIDMapped());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				runTasks();

			}
		});
	}

	private void runTasks() {
		timer1.schedule(new updateDataTask(), 0, 500);
		timer1.schedule(new updateCameraAddressTask(), 0, 100);
		timer2.schedule(new CameraTask(), 0);
	}

	@FXML
	private void openSettings(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLSettings.fxml"));
		Parent root = (Parent) loader.load();
		SettingsController controller = loader.<SettingsController>getController();

		controller.initialize(c);

		Scene scene = new Scene(root, 700, 400);
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle("Settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		openCameras();
		cameraList = FXCollections.observableArrayList(c.getCameraList());
		camerasTableView.setItems(cameraList);
	}

	private void populateView() {
		try {
			tagList = FXCollections.observableArrayList(c.getTags(actualWorld.getId()));
			cameraList = FXCollections.observableArrayList(c.getCameraList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tagTableView.setItems(tagList);
		this.tagUIDColumn.setCellValueFactory(device -> new SimpleStringProperty(device.getValue().getUid()));
		this.tagIPColumn.setCellValueFactory(device -> new SimpleStringProperty(device.getValue().getIp_address()));

		camerasTableView.setItems(cameraList);
		this.coveredZoneColumn.setCellValueFactory(camera -> new SimpleStringProperty(camera.getValue().getZone().getName()));
		this.PTZcolumn.setCellValueFactory(camera -> new SimpleStringProperty(String.valueOf(camera.getValue().isPtz())));

	}

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

}
