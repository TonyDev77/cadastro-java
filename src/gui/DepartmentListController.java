package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service; // Classe de serviços
	private ObservableList<Department> obsList; // lista p/ carregar os departamentos

	@FXML
	private TableView<Department> tableViewDepartment; // tabela
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // coluna id
	@FXML
	private TableColumn<Department, String> tableColumnName; // coluna nome
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT; // coluna botão edit
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE; // coluna botão delete
	@FXML
	private Button btNew;

	// comportamento do botão "Novo" (ActionEvent permite identificar o
	// palco/cena/botão que sofre a ação)
	@FXML
	public void onBtNewAction(ActionEvent event) {

		Stage parentStage = Utils.currentStage(event);
		Department depEntity = new Department();
		createDialogForm(depEntity, "/gui/DepartmentForm.fxml", parentStage);
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
		if (service == null) {
			throw new IllegalStateException("Serviço estava null");
		}
		List<Department> list = service.findAll(); // carrega lista de departamentos
		obsList = FXCollections.observableArrayList(list); // armazena em obsList
		tableViewDepartment.setItems(obsList); // carrega na tabela da view

		initEditButtons(); // Cria um botão EDIT para cada campo do form
		initRemoveButtons(); // Cria um botão REMOVE para cada campo do form
	}

	// Cria janela do formulário recebendo o endereço view e o pai dessa view)
	private void createDialogForm(Department depEntity, String url, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(url)); // carrega recurso desejado
			Pane pane = loader.load(); // carrega a view passada no recurso

			// insere objeto nos campos da janela do formulário
			DepartmentFormController controller = loader.getController(); // pega o controller da tela
			controller.setDepartment(depEntity); // cria o objeto
			controller.setDepartmentService(new DepartmentService()); // nova instância de serviço (CRUD c/ BD)

			controller.subscribeDataChangeListener(this); // se inscreve p/ receber alerta de novos eventos

			controller.updateFormData(); // insere nos campos

			Stage dialogStage = new Stage(); // P/ carregar um stage na frente do outro, cria-se um novo stage
			dialogStage.setTitle("Insira os dados do departamento");
			dialogStage.setScene(new Scene(pane)); // Carrega o elemento raiz da cena
			dialogStage.setResizable(false); // não pode redimensionar a janela
			dialogStage.initOwner(parentStage); // define quem será o pai dessa janela
			dialogStage.initModality(Modality.WINDOW_MODAL);// Torna ativa somente esta janela
			dialogStage.showAndWait(); // Mostra a janela

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao carregar recurso", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {

		updateTableView();
	}

	// Cria e configur um novo botão EDIT no formulário
	private void initEditButtons() {

		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		// cria o botão p/ cada linha
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Editar"); // define o nome do botão

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				// quando clicado abre o formulário de edição
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	// cria e configura um novo botão DELETE no formulário
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		// cria o botão p/ cada linha
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Deletar"); // define o nome do botão

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				// chama a função que confirmará e excluirá o dado
				button.setOnAction(event -> removeEntity(obj));

			}
		});
	}

	// Função para efetivar operação de esclusão
	private void removeEntity(Department dep) {

		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Está certo disto?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço esrava null");
			}

			try {
				service.remove(dep);
				updateTableView();

			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao excluir", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
