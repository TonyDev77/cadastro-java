package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable{

	private Department deptEntity;
	
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
	
	public void setDeptEntity(Department deptEntity) {
		this.deptEntity = deptEntity;
	}

	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
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
		
		if (deptEntity == null) {
			throw new IllegalStateException("Entidade null");
		}
		textId.setText(String.valueOf(deptEntity.getId())); // converte id para string (campos só trabalham c/ string)
		textName.setText(deptEntity.getName());
	}

}
