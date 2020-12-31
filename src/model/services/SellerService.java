package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao dao = DaoFactory.createSellerDao();

	// busca todos os departamentos
	public List<Seller> findAll() {
		return dao.findAll();
	}
	
	// salva ou atualiza dados no BD
	public void saveOrUpdate(Seller seller) {
		if (seller.getId() == null) {
			dao.insert(seller);
		} else {
			dao.update(seller);
		}
	}
	
	// deleta um dados no BD
	public void remove(Seller seller) {
		dao.deleteById(seller.getId());
	}
}
