package controller;

import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.opencv.core.Core;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Camera;
import model.Device;
import model.Position;
import model.World;
import utils.ConfigurationProperties;
import utils.Utils;
import controller.Controller;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import javafx.scene.control.TableColumn;

public class MainWindowController {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File lib = new File("../SequiturProjectFX/lib/" + System.mapLibraryName("opencv_videoio_ffmpeg411_64"));
		System.load(lib.getAbsolutePath());
		File lib2 = new File("../SequiturProjectFX/lib/" + System.mapLibraryName("opencv_java411"));
		System.load(lib2.getAbsolutePath());
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
	private Button refresh;
	@FXML
	private ImageView currentMap;
	@FXML
	private Label zonelbl;
	@FXML
	private Label cameralbl;
	@FXML
	private Label connecting;
	@FXML
	private Button settings;
	@FXML
	private ProgressBar progress;
	private static final String NOT_VISIBLE = "Tag not visible, there's no camera in this zone";
	private static final String OUT_OF_ZONES = "Out of zones";
	private static final String NO_TAGS = "No tags in this world";
	private static final String NO_VALID = "Tag position out of Sequitur localization area";

	private ObservableList<World> worldList;

	@FXML
	private ChoiceBox<World> worldID;

	private World actualWorld;
	// private ArrayList<Position> lastPositions;
	private ConcurrentLinkedQueue<Position> lastPositions;

	// a timer for acquiring the video stream
	private Timer timer1 = new Timer();
	// private Timer timer2 = new Timer();
	private Controller server;
	private String cam = "";
	private Image no_image;
	private Position pos;

	public void initialize(String serveraddr) {
		
		no_image = new Image(this.getClass().getClassLoader().getResourceAsStream("no_image.png"));
		lastPositions = new ConcurrentLinkedQueue<>();
		server = new Controller(serveraddr);
		server.setCameraList(new ArrayList<Camera>());
		try {
			server.addDefaultCameras();
		} catch (NumberFormatException | IOException e1) {
			System.out.println("Impossible to load cameras from file");
		}
		Task<Boolean> task = new Task<Boolean>() {
			@Override
			public Boolean call() throws IOException {
				server.openCameras();
				return true;
				
			}
		};

		task.setOnRunning((e) -> {
			this.progress.setVisible(true);
			this.connecting.setVisible(true);
			System.out.println("loading cameras...");
		});
		task.setOnSucceeded((e) -> {
			connecting.setVisible(false);
			this.progress.setVisible(false);
		});
		task.setOnFailed((e) -> {
			connecting.setVisible(false);
			this.progress.setVisible(false);
		});
		new Thread(task).start();
		setListeners();
		populateWorld();
		if (!worldID.getItems().isEmpty()) { // se ci sono mondi disponibili
			worldID.getSelectionModel().selectFirst();
			populateView();
		} else {
			System.out.println("Non ci sono mondi in questo Environment");
		}
	}

	/*
	 ******* task to refresh current data table
	 */
	public class updateCameraData extends TimerTask {
		@Override
		public void run() {
			if (!tagTableView.getItems().isEmpty()) {
				Position p = null;
				p = server.getPosition(server.getLastUIDMapped());
				try {
					server.setLastAddress(server.getLastUIDMapped());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (p != null) {
					List<String> zonelist = p.getZones();
					if (!p.getValidity()) {
						Platform.runLater(() -> {
							cameralbl.setText(NO_VALID);
							currentFrame.setImage(no_image);
						});
						return;
					} else if (!zonelist.isEmpty()) { // if the tag is inside the zones
						for (Camera c : server.getCameraList()) {
							if (zonelist.contains(c.getZone().getName()) && c.isOpened()) { // which camera has the
																							// actual zone
								// covered
								cam = c.getZone().getName();
								final String camera = cam;
								Platform.runLater(() -> {
									cameralbl.setText(camera);
									zonelbl.setText(zoneLabel.getText());
								});

								if (c.isPtz() && c.isOpened()) {

									c.followTag(p);
									// c.followTag(Utils.averagePos(lastPositions));
								}
								Image im = c.getActualFrame();

								if (im != null) {
									updateImageView(currentFrame, im);
								} else {
									updateImageView(currentFrame, no_image);
								}
								return;
							}
						}
					} else {
						Platform.runLater(() -> {
							cameralbl.setText(OUT_OF_ZONES);
							zonelbl.setText(zoneLabel.getText());
							currentFrame.setImage(no_image);
						});
						return;
					}
					Platform.runLater(() -> {
						cameralbl.setText(NOT_VISIBLE);
						zonelbl.setText(zoneLabel.getText());
						currentFrame.setImage(no_image);
					});

				}
			} else {
				Platform.runLater(() -> {
					cameralbl.setText(NO_TAGS);
					currentFrame.setImage(no_image);
				});
			}
		}
	}

	public class updateTagData extends TimerTask {

		@Override
		public void run() {
			if (server != null) {
				if (!tagTableView.getItems().isEmpty()) {
					Device dev = tagTableView.getSelectionModel().getSelectedItem();
					Image img = null;
					pos = null;
					img = server.getMap(server.getLastUIDMapped());
					if (dev != null) {
						pos = server.getPosition(dev.getUid());
					}
					if (pos != null) {
						if (lastPositions.size() > 10) {
							lastPositions.clear();
						}
						lastPositions.add(pos);
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

	private void setListeners() {
		tagTableView.getSelectionModel().selectedItemProperty().addListener((obVal, oldVal, newVal) -> {
			if (newVal != null) {
				TagDetailsPanel.setVisible(true);
				CameraPanel.setVisible(true);
				mapPanel.setVisible(true);
				TagDetailsPanel.setText("Device details: " + newVal.getUid());
				mapPanel.setText("Map for tag: " + newVal.getUid());
				try {
					if (server != null) {
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
				server.setActualWorld(this.actualWorld);
				populateView();
			}
		});
	}

	private void runTasks() {
		timer1.schedule(new updateTagData(), 0, 50);
		timer1.schedule(new updateCameraData(), 0, 50);
	}

	@FXML
	private void openSettings(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLSettings.fxml"));
		Parent root = (Parent) loader.load();
		SettingsController controller = loader.<SettingsController>getController();

		controller.initialize(server);

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle("Settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();

		Task<Boolean> task = new Task<Boolean>() {
			@Override
			public Boolean call() throws IOException {
				cameraList = FXCollections.observableArrayList(server.getCameraList());
				camerasTableView.setItems(cameraList);
				ConfigurationProperties.setProperties(cameraList);
				server.openCameras();
				return true;
			}
		};

		progress.progressProperty().bind(task.progressProperty());

		task.setOnRunning((e) -> {
			this.progress.setVisible(true);
			this.connecting.setVisible(true);
			System.out.println("Loading cameras...");
		});
		task.setOnSucceeded((e) -> {
			connecting.setVisible(false);
			this.progress.setVisible(false);
		});
		task.setOnFailed((e) -> {
			connecting.setVisible(false);
			this.progress.setVisible(false);
		});
		new Thread(task).start();
	}

	@FXML
	private void onRefresh(ActionEvent event) {
		populateWorld();
		if (!worldID.getItems().isEmpty()) { // se ci sono mondi disponibili
			worldID.getSelectionModel().selectFirst();
			populateView();
		} else {
			System.out.println("Non ci sono mondi in questo Environment");
		}

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
		this.tagAliasColumn
				.setCellValueFactory(device -> new SimpleStringProperty(device.getValue().getInfo().getAlias()));

		camerasTableView.setItems(cameraList);
		this.coveredZoneColumn
				.setCellValueFactory(camera -> new SimpleStringProperty(camera.getValue().getZone().getName()));
		this.PTZcolumn
				.setCellValueFactory(camera -> new SimpleStringProperty(String.valueOf(camera.getValue().isOpened())));

		if (!tagTableView.getItems().isEmpty()) {
			tagTableView.getSelectionModel().selectFirst();
		}

	}

	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

}
