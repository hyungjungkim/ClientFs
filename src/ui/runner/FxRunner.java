package ui.runner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxRunner extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		//
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/loginfx.fxml"));
		Parent root = loader.load();

		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Project Ping-Pong");
		primaryStage.show();
	
	}
	
	public static void main(String[] args){
		//
		launch(args);
		}
}
