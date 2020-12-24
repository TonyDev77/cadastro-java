package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService service; // Classe de serviços
	private ObservableList<Department> obsList; // lista p/ carregar os departamentos
	
	@FXML
	private TableView<Department> tableViewDepartment; // tabela
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // coluna id
	@FXML
	private TableColumn<Department, String> tableColumnName; // coluna nome 
	@FXML
	private Button btNew;
	
	// comportamento do botão "Novo"
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	// Inicia antes da tela ser montada
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // método que permite comportamento correto da tabela
	}

	// padrão do JavaFX que permite inicializar apropriadamente as colunas da tabela
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// Macete p/ fazer a tabela acompanhar o dimensionamento da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	// permite acoplamento fraco - (inversão de controle - solid)
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	// Acessa, carrega e guarda os departamentos em obsList
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("Serviço estava null");
		}
		List<Department> list = service.findAll(); // carrega lista de departamentos
		obsList = FXCollections.observableArrayList(list); // armazena em obsList
		tableViewDepartment.setItems(obsList); // carrega na tabela da view
	}

}
