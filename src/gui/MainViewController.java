package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDeparmentAction() {
		// Carrega a view Department
		loadView("/gui/DepartmentList.fxml");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		// Carrega a view About
		loadView("/gui/About.fxml");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	/*
	 * Abre uma nova view dentro MainView
	 * 'synchronized' permite que processamento das trades não seja interrompida 
	 */
	public synchronized void loadView(String absoluteName) {
		try {
			// armazena a url a view passada no parâmetro
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			// guarda a view do endereço acima
			VBox newVBox = loader.load();
			
			// Obtém a cena principal
			Scene mainScene = Main.getMainScene(); 
			// guarda referencia para o  vbox da MainView
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			// guardando a referência para o menu principal
			Node mainMenu = mainVBox.getChildren().get(0); // primeiro filho do VBox da MainView
			mainVBox.getChildren().clear(); // limpa todos os filhos o vbox da MainView
			mainVBox.getChildren().add(mainMenu); // add o menu de volta na MainView
			mainVBox.getChildren().addAll(newVBox.getChildren()); // add os filhos do novo VBox
			
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar a página", e.getMessage(), AlertType.ERROR);
		}
	}

}
