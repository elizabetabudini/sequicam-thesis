package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;

import javafx.scene.control.Label;

public class SplashController {
	@FXML
	private TextField serveraddr;
	@FXML
	private Label serverError;
	@FXML
	private Button okbtn;
	private MainWindowController controller;
	private Parent root;

	// Event Listener on Button[#okbtn].onAction
	@FXML
	public void onOK(ActionEvent event) {

		// here runs the JavaFX thread
		// Boolean as generic parameter since you want to return it
		Task<Boolean> task = new Task<Boolean>() {
			@Override
			public Boolean call(){
					URL url = null;
					try {
						url = new URL("http://" + serveraddr.getText() + "/api/sys/environment");
					} catch (MalformedURLException e) {
						return false;
					}
					System.out.println("Trying to connect to " + url);
					String encoding;
					try {
						encoding = Base64.getEncoder().encodeToString(("root:admin").getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return false;
					}
					HttpURLConnection connection;
					try {
						connection = (HttpURLConnection) url.openConnection();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
					connection.setConnectTimeout(2000);
					try {
						connection.setRequestMethod("GET");
					} catch (ProtocolException e) {
						e.printStackTrace();
						return false;
					}
					connection.setDoOutput(true);
					connection.setRequestProperty("Authorization", "Basic " + encoding);
					try {
						connection.getInputStream();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}

					FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLMainWindow.fxml"));
					try {
						root = (Parent) loader.load();
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					}
					controller = loader.<MainWindowController>getController();
					return true;

			}
		};

		task.setOnRunning((e) -> {
			serverError.setVisible(true);
			serverError.setText("Connecting...");
			okbtn.setDisable(true);
		});
		task.setOnSucceeded((e) -> {
			try {
				if(task.get()) {
					serverError.setText("Connection succesfull.");
					controller.initialize(serveraddr.getText());
					Scene scene = new Scene(root);
					Stage stage = new Stage();
					stage.setOnCloseRequest(Event -> {
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setTitle("Close window");
						alert.setHeaderText("Close window");
						alert.setContentText("Are you sure you want to exit the application?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							stage.close();
							System.exit(0);
						} else {
							// ... user chose CANCEL or closed the dialog
							event.consume();
							alert.close();
						}
					});

					Stage currentStage = (Stage) okbtn.getScene().getWindow();
					currentStage.close();

					stage.setResizable(false);
					stage.setScene(scene);
					stage.setTitle("Sequitur project Extension");
					stage.initModality(Modality.WINDOW_MODAL);
					stage.show();
				} else {
					serverError.setVisible(true);
					serverError.setText("Impossible to connect. Try again.");
					okbtn.setDisable(false);
				}
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

		});
		task.setOnFailed((e) -> {
			// eventual error handling by catching exceptions from task.get()
			System.out.println("Error: ");
			task.getException().printStackTrace(System.out);
			
			serverError.setVisible(true);
			serverError.setText("Impossible to connect. Try again.");
			okbtn.setDisable(false);
		});
		new Thread(task).start();

	}
}
