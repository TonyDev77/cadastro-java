package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;

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
		// Carrega a view Department com parâmetros
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		// Carrega a view About passando o 2º parâmetro com nada dentro p/ a consumer
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	/*
	 * Abre uma nova view dentro MainView
	 * 'synchronized' permite que processamento das trades não seja interrompida
	 * Também é uma "Consumer", função "Generic" do tipo <T>
	 */
	public synchronized <T> void loadView(String urlView, Consumer<T> consumerExecute) {
		try {
			// armazena a url a view passada no parâmetro
			FXMLLoader loader = new FXMLLoader(getClass().getResource(urlView));
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
			
			// Ativa o consumer executando o "Consumer" passado no parâmetro
			T controller = loader.getController();
			consumerExecute.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar a página", e.getMessage(), AlertType.ERROR);
		}
	}
}
