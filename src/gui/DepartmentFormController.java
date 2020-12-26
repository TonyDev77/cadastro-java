package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
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
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{

	private Department department;
	private DepartmentService service;
	
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
	
	// instancia novo departamento
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
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
			Utils.currentStage(event).close(); // Fecha a janela
			
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// pega os dados do formulário e retorna um objeto
	private Department getFormData() {
		Department dep = new Department();
		
		dep.setId(Utils.tryParseToInt(textId.getText()));
		dep.setName(textName.getText());
		
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
	
	// Popula os campos (id, name) com a entity Department
	public void updateFormData() {
		
		if (department == null) {
			throw new IllegalStateException("Entidade null");
		}
		textId.setText(String.valueOf(department.getId())); // converte id para string (campos só trabalham c/ string)
		textName.setText(department.getName());
	}

}