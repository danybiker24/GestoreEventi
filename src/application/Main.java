package application;

import it.Daniele.gestore.calendario.controller.Controller;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;


public class Main extends Application {	
	@Override
	public void start(Stage primaryStage) {
		try {			
			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
			primaryStage.setTitle("Gestore Eventi");
			primaryStage.setWidth(primaryScreenBounds.getWidth()*0.5);
			primaryStage.setHeight(primaryScreenBounds.getHeight()*0.75);
			primaryStage.setResizable(false);	
			
			MySelectView selPane = new MySelectView(primaryStage);
			Scene selScene = new Scene(selPane);
			selScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(selScene);
			primaryStage.show();
			
		} catch(Exception e) {
			Controller.myAlert(AlertType.ERROR, "Unknown error\n" + e, ButtonType.CLOSE);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}