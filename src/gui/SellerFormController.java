package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Seller;
import model.exceptions.ValidationExceptions;
import model.services.SellerService;

public class SellerFormController implements Initializable{

	private Seller department;
	private SellerService service;
	// lista de obj interessados em escutar novos eventos (mudanças) de outros objetos
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField textId;
	@FXML
	private TextField textName;
	@FXML
	private Label labelErrorName;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	
	// instancia novo vendedor
	public void setSeller(Seller department) {
		this.department = department;
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	// método que permite objetos se inscreverem na lista p/ receber evento (mudança)
	public void subscribeDataChangeListener(DataChangeListener listener) {
		
		dataChangeListeners.add(listener);
	}
	

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (department == null) {
			throw new IllegalStateException("Entidade está null");
		}
		if (service == null) {
			throw new IllegalStateException("Service está null");
		}
		
		try {
			department = getFormData(); // obtém objeto criado
			service.saveOrUpdate(department); // salva no BD
			notifyDataChangeListeners(); // notifica os ouvidores/listeners
			Utils.currentStage(event).close(); // Fecha a janela
			    
		}  catch (ValidationExceptions e) {
			setErrorMessage(e.getErrors()); // captura os erros na coleção do Map
			
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// notifica os ouvidores/listeners
	private void notifyDataChangeListeners() {
		
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	// pega os dados do formulário e retorna um objeto
	private Seller getFormData() {
		Seller dep = new Seller();
		
		ValidationExceptions exception = new ValidationExceptions("Validation Error");
		
		dep.setId(Utils.tryParseToInt(textId.getText()));
		
		// valida o os campos do formulário
		if (textName.getText() == null || textName.getText().trim().equals("")) {
			exception.addErrors("name", "Campo 'Nome' não pode estar vazio");
		}
		
		dep.setName(textName.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return dep;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		
		Utils.currentStage(event).close(); // Fecha a janela
	}

	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		initializeNode();
		
	}
	
	// Validação para os campos textFields
	private void initializeNode() {
		Constraints.setTextFieldInteger(textId);
		Constraints.setTextFieldMaxLength(textName, 30 );
	}
	
	// Popula os campos (id, name) com a entity Seller
	public void updateFormData() {
		
		if (department == null) {
			throw new IllegalStateException("Entidade null");
		}
		textId.setText(String.valueOf(department.getId())); // converte id para string (campos só trabalham c/ string)
		textName.setText(department.getName());
	}
	
	// Retorna os erros gerados na tela do usuário
	private void setErrorMessage(Map<String, String> errors) {
		
		Set<String> fields = errors.keySet(); // percorre o Map pegando apenas a chave
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name")); // pega a chave do erro em Map e imprime na tela
		}
		
	}

}
