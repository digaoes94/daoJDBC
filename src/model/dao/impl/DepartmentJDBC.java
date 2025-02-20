package model.dao.impl;

import java.sql.Connection;
import java.util.List;

import db.DB;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentJDBC implements DepartmentDAO {
	private Connection conn = DB.getConn();
	
	@Override
	public void insert(Department dep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Department dep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByID(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Department findByID(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Department> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
