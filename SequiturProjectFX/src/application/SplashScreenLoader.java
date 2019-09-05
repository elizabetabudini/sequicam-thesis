package application;

import java.io.IOException;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SplashScreenLoader extends Preloader {

    private Stage splashScreen;

    @Override
    public void start(Stage stage) throws Exception {
        splashScreen = stage;
        splashScreen.setScene(createScene());
        splashScreen.show();
    }

    public Scene createScene() throws IOException {
        StackPane rootElement = new StackPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Splash.fxml"));
		// store the root element so that the controllers can use it
		rootElement = (StackPane) loader.load();
		Scene scene = new Scene(rootElement);
        return scene;
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        if (notification instanceof StateChangeNotification) {
            splashScreen.hide();
        }
    }

}