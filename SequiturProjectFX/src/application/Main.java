package application;

import com.sun.javafx.application.LauncherImpl;

public class Main {

	public static void main(String[] args) {
		 LauncherImpl.launchApplication(App.class, SplashScreenLoader.class, args);
	}

}
