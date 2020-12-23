package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	// Declaração da cena
	private static Scene mainScene;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			// Ajusta as dimensões do menu
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Cadastro de Produtos");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// torna mainScene acessível
	public static Scene getMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
