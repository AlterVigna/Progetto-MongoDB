package application;
	
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



/**
 * Classe per l'avvio dell'applicazione.
 * @author Davide
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {					
			
			URL loc = Main.class.getClass().getResource("/views/AutenticazioneView.fxml");
			Parent root = FXMLLoader.load(loc);
		
			Scene scene = new Scene(root,850,600);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Let's Movie - Autenticazione");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
