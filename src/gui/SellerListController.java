package gui;

import java.net.URL;
import java.sql.Date;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service; // Classe de serviços
	private ObservableList<Seller> obsList; // lista p/ carregar os departamentos

	@FXML
	private TableView<Seller> tableViewSeller; // tabela
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // coluna id
	@FXML
	private TableColumn<Seller, String> tableColumnName; // coluna nome
	@FXML
	private TableColumn<Seller, String> tableColumnEmail; // coluna email
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate; // coluna nascimento (java.sql.Date) 
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary; // coluna salário
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT; // coluna botão edit
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE; // coluna botão delete
	@FXML
	private Button btNew;

	// comportamento do botão "Novo" (ActionEvent permite identificar o
	// palco/cena/botão que sofre a ação)
	@FXML
	public void onBtNewAction(ActionEvent event) {

		Stage parentStage = Utils.currentStage(event);
		Seller depEntity = new Seller();
		createDialogForm(depEntity, "/gui/SellerForm.fxml", parentStage);
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
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

		// Macete p/ fazer a tabela acompanhar o dimensionamento da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	// permite acoplamento fraco - (inversão de controle - solid)
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	// Acessa, carrega e guarda os departamentos em obsList
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Serviço estava null");
		}
		List<Seller> list = service.findAll(); // carrega lista de departamentos
		obsList = FXCollections.observableArrayList(list); // armazena em obsList
		tableViewSeller.setItems(obsList); // carrega na tabela da view

		initEditButtons(); // Cria um botão EDIT para cada campo do form
		initRemoveButtons(); // Cria um botão REMOVE para cada campo do form
	}

	// Cria janela do formulário recebendo o endereço view e o pai dessa view)
	private void createDialogForm(Seller depEntity, String url, Stage parentStage) {

//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(url)); // carrega recurso desejado
//			Pane pane = loader.load(); // carrega a view passada no recurso
//
//			// insere objeto nos campos da janela do formulário
//			SellerFormController controller = loader.getController(); // pega o controller da tela
//			controller.setSeller(depEntity); // cria o objeto
//			controller.setSellerService(new SellerService()); // nova instância de serviço (CRUD c/ BD)
//
//			controller.subscribeDataChangeListener(this); // se inscreve p/ receber alerta de novos eventos
//
//			controller.updateFormData(); // insere nos campos
//
//			Stage dialogStage = new Stage(); // P/ carregar um stage na frente do outro, cria-se um novo stage
//			dialogStage.setTitle("Insira os dados do departamento");
//			dialogStage.setScene(new Scene(pane)); // Carrega o elemento raiz da cena
//			dialogStage.setResizable(false); // não pode redimensionar a janela
//			dialogStage.initOwner(parentStage); // define quem será o pai dessa janela
//			dialogStage.initModality(Modality.WINDOW_MODAL);// Torna ativa somente esta janela
//			dialogStage.showAndWait(); // Mostra a janela
//
//		} catch (IOException e) {
//			Alerts.showAlert("IOException", "Erro ao carregar recurso", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {

		updateTableView();
	}

	// Cria e configur um novo botão EDIT no formulário
	private void initEditButtons() {

		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		// cria o botão p/ cada linha
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Edit"); // define o nome do botão

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);

				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				// quando clicado abre o formulário de edição
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	// cria e configura um novo botão DELETE no formulário
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		// cria o botão p/ cada linha
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Remove"); // define o nome do botão

			@Override
			protected void updateItem(Seller obj, boolean empty) {
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
	private void removeEntity(Seller dep) {

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
