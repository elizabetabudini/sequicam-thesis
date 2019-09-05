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
	private TableColumn<Device, String> tagAliasColumn;

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
	private static final String NO_TAGS = "No tags in this world";

	private ObservableList<World> worldList;

	@FXML
	private ChoiceBox<World> worldID;

	private World actualWorld;

	// a timer for acquiring the video stream
	private Timer timer1 = new Timer();
	private Timer timer2 = new Timer();;
	private ServerController server;
	private String cam = "";
	//private Image no_image;
	private Position pos;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		//Passing FileInputStream object as a parameter 
		//FileInputStream inputstream = null;
		/*try {
			inputstream = new FileInputStream("res/no-picture.jpg");
			no_image = new Image(inputstream); 
		} catch (FileNotFoundException e) {
			System.out.println("Impossible to load image: "+ inputstream);
		} */
		
		server = new ServerController();

		// debug only
		server.setCameraList(new ArrayList<>());
		server.addDefaultCameras(); // debug
		openCameras();
		setListeners();
		populateWorld();
		if (!worldID.getItems().isEmpty()) { // se ci sono mondi disponibili
			worldID.getSelectionModel().selectFirst();
			populateView();
		}
	}

	public void openCameras() {
		if (!server.getCameraList().isEmpty()) {
			for (Camera c : server.getCameraList()) {
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
			if (!tagTableView.getItems().isEmpty()) {
				Position p = null;
				try {
					p = server.getPosition(server.getLastUIDMapped());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					server.setLastAddress(server.getLastUIDMapped());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (p != null) {
					if (!p.getZones().isEmpty()) { // if the tag is inside the zones
						for (Camera c : server.getCameraList()) {
							if (p.getZones().contains(c.getZone().getName())) { // which camera has the actual zone
																				// covered
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
			} else {
				Platform.runLater(() -> {
					cameralbl.setText(NO_TAGS);
					currentFrame.setImage(null);
				});
			}
		}
	}

	// TimerTask updateData = new TimerTask() {
	public class updateDataTask extends TimerTask {

		@Override
		public void run() {
			if (server != null) {
				if (!tagTableView.getItems().isEmpty()) {
					Device dev = tagTableView.getSelectionModel().getSelectedItem();
					Image img = null;
					pos = null;
					try {
						img = server.getMap(server.getLastUIDMapped());
						if (dev != null) {
							pos = server.getPosition(dev.getUid());
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
							mapPanel.setText("Map for tag: " + server.getLastUIDMapped());
						});
					}

					if (img != null) {
						final Image imgToShow = img;
						Platform.runLater(() -> {
							currentMap.setImage(imgToShow);
						});
					}
				} else {
					Platform.runLater(() -> {
						xLabel.setText("");
						yLabel.setText("");
						zLabel.setText("");
						zoneLabel.setText("");
						mapPanel.setText("Map");
						currentMap.setImage(null);
						TagDetailsPanel.setText("No device details");
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
			if (!tagTableView.getItems().isEmpty()) {
				while (flag) {
					String rtspAddress = server.getLastAddress();
					flagChangeCam = true;
					if (rtspAddress != "") {
						currentFrame.setVisible(true);
						Camera cam = null;
						String zone = server.getLastZone();
						for (Camera camera : server.getCameraList()) {
							if (camera.getZone().getName() == zone) {
								cam = camera;
							}
						}
						while (flagChangeCam) {
							if (cameralbl.getText() == OUT_OF_ZONES || cameralbl.getText() == NOT_VISIBLE) {
								updateImageView(currentFrame, null);
							} else {
								if (rtspAddress != server.getLastAddress()) {
									break;
								}
								Image im = null;
								if (cam == null) {
									updateImageView(currentFrame, null);
								} else {
									if (cam.isPtz()) {
										if(pos!= null) {
											cam.followTag(pos);
											
										}
										
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
			} else {
				updateImageView(currentFrame, null);
				Platform.runLater(() -> {
					cameraLabel.setText(NO_TAGS);
				});
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
					if(server!=null) {
						server.getMap(newVal.getUid());
						server.setLastAddress(server.getLastUIDMapped());
					}
					
				} catch (IOException e) {
					System.out.println("Couldn't set lastUID mapped");
				}
				runTasks();

			}
		});

		worldID.getSelectionModel().selectedItemProperty().addListener((obVal, oldVal, newVal) -> {
			if (newVal != null) {
				this.actualWorld = newVal;
				populateView();
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

		controller.initialize(server);

		Scene scene = new Scene(root, 700, 400);
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle("Settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		openCameras();
		cameraList = FXCollections.observableArrayList(server.getCameraList());
		camerasTableView.setItems(cameraList);
	}

	private void populateWorld() {
		try {
			worldList = FXCollections.observableArrayList(server.getWorlds());
			this.worldID.setItems(worldList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateView() {
		try {
			tagList = FXCollections.observableArrayList(server.getTags(actualWorld.getId()));
			cameraList = FXCollections.observableArrayList(server.getCameraList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tagTableView.setItems(tagList);
		this.tagUIDColumn.setCellValueFactory(device -> new SimpleStringProperty(device.getValue().getUid()));
		this.tagAliasColumn.setCellValueFactory(device -> new SimpleStringProperty(device.getValue().getAlias()));

		camerasTableView.setItems(cameraList);
		this.coveredZoneColumn
				.setCellValueFactory(camera -> new SimpleStringProperty(camera.getValue().getZone().getName()));
		this.PTZcolumn
				.setCellValueFactory(camera -> new SimpleStringProperty(String.valueOf(camera.getValue().isPtz())));
		
		if (!tagTableView.getItems().isEmpty()) {
			tagTableView.getSelectionModel().selectFirst();
		}

	}

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

}
