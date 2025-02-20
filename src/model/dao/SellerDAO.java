package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDAO {
	void insert(Seller seller);
	void update(Seller seller);
	void deleteByID(Integer id);
	Seller findByID(Integer id);
	List<Seller> findByDepartment(Department dep);
	List<Seller> findAll();
}