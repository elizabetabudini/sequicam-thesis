package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AXISCamera;
import model.Camera;
import model.Position;
import model.SV3CCamera;
import model.Zone;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.Label;

import javafx.scene.control.ChoiceBox;

public class FormularioCameraController implements Initializable {
	@FXML
	private Label title;
	@FXML
	private TextField x;
	@FXML
	private TextField y;
	@FXML
	private TextField z;
	@FXML
	private TextField address;
	@FXML
	private ChoiceBox<Zone> zone;
	@FXML
	private Button cancel;
	@FXML
	private Button save;
	private Camera cam;
	private Controller controller;
	private ObservableList<Zone> zoneList;
	@FXML
	private CheckBox PTZcheckbox;
	@FXML
	private Label hintX;
	@FXML
	private Label hintY;
	@FXML
	private Label hintZ;
	@FXML
	private TextField diff;

	private ChangeListener<String> checkEnableConfirm = (obVal, oldVal, newVal) -> {
		boolean disable = true;
		if (address.getText().equals("") || x.getText().equals("") || y.getText().equals("") || z.getText().equals("")
				|| diff.getText().equals("")) {
			disable = true;
		} else {
			disable = false;
		}

		if (x.getText().matches("[-+]?[0-9]*\\.?[0-9]+")) {
			this.hintX.setVisible(false);
		} else {
			this.hintX.setVisible(true);
			disable = true;
		}
		if (y.getText().matches("[-+]?[0-9]*\\.?[0-9]+")) {
			this.hintY.setVisible(false);
		} else {
			this.hintY.setVisible(true);
			disable = true;
		}
		if (z.getText().matches("[-+]?[0-9]*\\.?[0-9]+")) {
			this.hintZ.setVisible(false);
		} else {
			this.hintZ.setVisible(true);
			disable = true;
		}
		if (diff.getText().matches("[-+]?[0-9]*\\.?[0-9]+")) {
			this.hintZ.setVisible(false);
		} else {
			this.hintZ.setVisible(true);
			disable = true;
		}
		this.save.setDisable(disable);
	};

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

	// Event Listener on Button[#save].onAction
	@FXML
	public void onSave(ActionEvent event) {
		Camera cam = null;
		try {
			if (this.PTZcheckbox.isSelected()) {
				cam = new AXISCamera(this.address.getText(), this.zone.getSelectionModel().getSelectedItem(),
						new Position(Double.valueOf(this.x.getText()), Double.valueOf(this.y.getText()),
								Double.valueOf(this.z.getText())),
						Double.valueOf(this.diff.getText()), this.PTZcheckbox.isSelected());
			} else {
				cam = new SV3CCamera(this.address.getText(), this.zone.getSelectionModel().getSelectedItem(),
						new Position(Double.valueOf(this.x.getText()), Double.valueOf(this.y.getText()),
								Double.valueOf(this.z.getText())),
						Double.valueOf(this.diff.getText()), this.PTZcheckbox.isSelected());
			}

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Close window");
		alert.setHeaderText("Save");
		alert.setContentText("Save and close settings?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			controller.getCameraList().remove(this.cam);
			controller.getCameraList().add(cam);
			close();
		} else {
			// ... user chose CANCEL or closed the dialog
			event.consume();
			alert.close();
		}

	}

	private void close() {
		Stage currentStage = (Stage) cancel.getScene().getWindow();
		currentStage.close();
	}

	public void initialize(Camera cam, int editIndex, Controller controller, String zoneset) {

		this.controller = controller;
		zoneList = FXCollections.observableArrayList(controller.getZones(zoneset));
		this.zone.setItems(zoneList);

		if (cam != null && editIndex >= 0) {
			this.cam = cam;
			this.address.setText(cam.getIP());
			this.title.setText("Modify camera");
			this.x.setText(String.valueOf(cam.getPos().getX()));
			this.y.setText(String.valueOf(cam.getPos().getY()));
			this.z.setText(String.valueOf(cam.getPos().getZ()));
			this.diff.setText(String.valueOf(cam.getDiff()));
			this.PTZcheckbox.selectedProperty().set(cam.isPtz());
			this.zone.getSelectionModel().select(cam.getZone());
			this.zone.getItems().forEach(e -> {
				if (e.getName().equals(this.cam.getZone().getName()) && e.getId() == this.cam.getZone().getId()) {
					this.zone.getSelectionModel().select(e);
				}
			});
		} else {
			this.title.setText("Add new camera");
			this.zone.getSelectionModel().selectFirst();
			this.x.setText("0.0");
			this.y.setText("0.0");
			this.z.setText("0.0");
			this.diff.setText("0.0");
		}
		Platform.runLater(() -> {
			this.address.requestFocus();
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		address.textProperty().addListener(checkEnableConfirm);
		x.textProperty().addListener(checkEnableConfirm);
		y.textProperty().addListener(checkEnableConfirm);
		z.textProperty().addListener(checkEnableConfirm);
		diff.textProperty().addListener(checkEnableConfirm);
		save.setDisable(true);

	}
}
