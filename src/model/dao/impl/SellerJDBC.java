package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class SellerJDBC implements SellerDAO {
	private Connection conn = DB.getConn();
	
	public SellerJDBC (Connection conn) {
		this.conn = conn;
	}
	@Override
	public void insert(Seller seller) {
		
	}

	@Override
	public void update(Seller seller) {
		
	}

	@Override
	public void deleteByID(Integer id) {
		
	}

	@Override
	public Seller findByID(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department on seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?"
			);
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				return instantiateSeller(rs);
			}
			else {
				return null;
			}
		}
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
	
	@Override
	public List<Seller> findByDepartment(Department dep1) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? ORDER BY Name"
			);
			
			st.setInt(1, dep1.getId());
			rs = st.executeQuery();
			List<Seller> sellers = new ArrayList<Seller>();
			Map<Integer, Department> departments = new HashMap<>();
			
			if (rs.next()) {
				Department dep2 = departments.get(rs.getInt("DepartmentId"));
				
				if (dep2 == null) {
					dep2 = instantiateDepartment(rs);
					departments.put(dep2.getId(), dep2);
				}
				
				while (rs.next()) {
					if (rs.getInt("DepartmentId") == dep2.getId()) {
						sellers.add(instantiateSeller(rs, dep2));
					}
				}
				
				return sellers;
			}
			else {
				return null;
			}
		}
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name"
			);
			
			rs = st.executeQuery();
			List<Seller> sellers = new ArrayList<Seller>();
			Map<Integer, Department> departments = new HashMap<>();
			
			if (rs.next()) {
				while (rs.next()) {
					Department dep = departments.get(rs.getInt("DepartmentId"));
					
					if (dep == null) {
						dep = instantiateDepartment(rs);
						departments.put(dep.getId(), dep);
					}
					
					if (rs.getInt("DepartmentId") == dep.getId()) {
						sellers.add(instantiateSeller(rs, dep));
					}
				}
				
				return sellers;
			}
			else {
				return null;
			}
		}
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
	
	public Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
	}
	
	public Seller instantiateSeller(ResultSet rs) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), 
				Date.valueOf(rs.getDate("BirthDate").toString()).toLocalDate(),
				rs.getDouble("BaseSalary"), instantiateDepartment(rs));
	}
	
	public Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), 
				Date.valueOf(rs.getDate("BirthDate").toString()).toLocalDate(),
				rs.getDouble("BaseSalary"), dep);
	}
}