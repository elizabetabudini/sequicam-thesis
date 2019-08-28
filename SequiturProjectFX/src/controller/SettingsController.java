package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Camera;
import javafx.scene.control.TableColumn;

public class SettingsController implements Initializable {
	@FXML
	private Button cancel;
	@FXML
	private TableView<Camera> CameraTable;
	@FXML
	private TableColumn<Camera, String> AddressColumn;
	@FXML
	private TableColumn<Camera, String> ZoneColumn;
	@FXML
	private TableColumn<Camera, String> PositionColumn;
	@FXML
	private TableColumn<Camera, String> PTZColumn;
	@FXML
	private ListView<String> worldList;
	@FXML
	private Button add;
	@FXML
	private Button remove;
	@FXML
	private Button modify;
	@FXML
	private Button save;

	private ObservableList<Camera> cameraList;
	private Controller controller;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		setButtons();
	}

	// Event Listener on Button[#cancel].onAction
	@FXML
	public void onCancel(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Close window");
		alert.setHeaderText("Close window");
		alert.setContentText("Are you sure you want to close this window? All data won't be saved.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			close();
		} else {
			// ... user chose CANCEL or closed the dialog
			event.consume();
			alert.close();
		}
	}

	private void close() {
		Stage currentStage = (Stage) remove.getScene().getWindow();
		currentStage.close();
	}

	// Event Listener on Button[#add].onAction
	@FXML
	public void onAdd(ActionEvent event) throws IOException {
		launchModal(null, -1);
	}

	// Event Listener on Button[#remove].onAction
	@FXML
	public void onRemove(ActionEvent event) {
		int i = CameraTable.getSelectionModel().getSelectedIndex();
		Camera g = cameraList.get(i);
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete camera");
		alert.setHeaderText("Delete camera");
		alert.setContentText("Are you sure you want to delete this camera?\n\nCamera zone: " + g.getZone().getName());

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// ... user chose OK
			if (i >= 0)
				cameraList.remove(i);
			alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Camera deleted correctly");
			alert.setHeaderText("Camera deleted");
			alert.setContentText("Camera deleted correctly");
			alert.show();
		} else {
			// ... user chose CANCEL or closed the dialog
			event.consume();
			alert.close();
		}
	}

	// Event Listener on Button[#modify].onAction
	@FXML
	public void onModify(ActionEvent event) throws IOException {
		MultipleSelectionModel<Camera> selection = CameraTable.getSelectionModel();
		// System.out.println(selection.getSelectedItem()+","+
		// selection.getSelectedIndex());
		launchModal(selection.getSelectedItem(), selection.getSelectedIndex());

	}

	// Event Listener on Button[#save].onAction
	@FXML
	public void onSave(ActionEvent event) {

		controller.setCameraList(cameraList);
		close();

	}

	private void setButtons() {
		add.requestFocus();
		modify.setDisable(true);
		remove.setDisable(true);

		CameraTable.getSelectionModel().selectedItemProperty().addListener((obVal, oldVal, newVal) -> {
			if (newVal != null) {
				modify.setDisable(false);
				remove.setDisable(false);
			} else {
				modify.setDisable(true);
				remove.setDisable(true);
			}
		});
	}

	private void launchModal(Camera cam, int editIndex) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLFormularioCamera.fxml"));
		Parent root = (Parent) loader.load();
		FormularioCameraController controller = loader.<FormularioCameraController>getController();

		controller.initialize(cam, editIndex, this.controller);

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		if (editIndex >= 0) {
			stage.setTitle("Modify camera");
		}
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		initialize(this.controller);
	}

	public void initialize(Controller c) {
		this.controller = c;
		cameraList = FXCollections.observableArrayList(controller.getCameraList());
		CameraTable.setItems(cameraList);
		this.AddressColumn.setCellValueFactory(camera -> new SimpleStringProperty(camera.getValue().getAddress()));
		this.PositionColumn.setCellValueFactory(device -> new SimpleStringProperty(String
				.valueOf(device.getValue().getPos().getX() + "; " + String.valueOf(device.getValue().getPos().getY()
						+ "; " + String.valueOf(device.getValue().getPos().getZ())))));
		this.ZoneColumn.setCellValueFactory(camera -> new SimpleStringProperty(camera.getValue().getZone().getName()));
		this.PTZColumn
				.setCellValueFactory(camera -> new SimpleStringProperty(String.valueOf(camera.getValue().isPtz())));
	}
}
