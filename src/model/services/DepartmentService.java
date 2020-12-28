package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	// busca todos os departamentos
	public List<Department> findAll() {
		
		return dao.findAll();
	}
	
	// salva ou atualiza dados no BD
	public void saveOrUpdate(Department dep) {
		if (dep.getId() == null) {
			dao.insert(dep);
		} else {
			dao.update(dep);
		}
	}
	
	// deleta um dados no BD
	public void remove(Department dep) {
		dao.deleteById(dep.getId());
	}
}
