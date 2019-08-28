package application;

import java.util.Optional;

//import controller.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;

public class App extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLMainWindow.fxml"));
			// store the root element so that the controllers can use it
			GridPane rootElement = (GridPane) loader.load();
			
			// create and style a scene
			Scene scene = new Scene(rootElement);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Sequitur Project");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();

			// set the proper behavior on closing the application
			//MainWindowController controller = loader.getController();
			
			primaryStage.setOnCloseRequest((WindowEvent event) ->{
	            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	            alert.setTitle("Close application");
	            alert.setHeaderText("Close application");
	            alert.setContentText("Are you sure you want to exit the application?");

	            Optional<ButtonType> result = alert.showAndWait();
	            if (result.get() == ButtonType.OK){
	                primaryStage.close();
	                System.exit(0);
	            } else {
	                event.consume();
	                alert.close();
	            }
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
