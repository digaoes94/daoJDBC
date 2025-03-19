package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS
					);
			
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, Date.valueOf(seller.getBirth()));
			st.setDouble(4, seller.getSalary());
			st.setInt(5, seller.getDepartment().getId());
			
			int affectedRows = st.executeUpdate();
			
			if (affectedRows > 0) {
				rs = st.getGeneratedKeys();
				
				if (rs.next()) {
					int id = rs.getInt(1);
					seller.setId(id);
				}
			}
			else {
				throw new DBException("Unexpected error, update failed.");
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
	public void update(Seller seller) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?",
					Statement.RETURN_GENERATED_KEYS
					);
			
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, Date.valueOf(seller.getBirth()));
			st.setDouble(4, seller.getSalary());
			st.setInt(5, seller.getDepartment().getId());
			st.setInt(6, seller.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteByID(Integer id) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			st.setInt(1, id);
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DBException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
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
					dep2 = DepartmentJDBC.instantiateDepartment(rs);
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
					"SELECT seller.*, department.Name as DepName "
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
						dep = DepartmentJDBC.instantiateDepartment(rs);
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
	
	public static Seller instantiateSeller(ResultSet rs) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), 
				Date.valueOf(rs.getDate("BirthDate").toString()).toLocalDate(),
				rs.getDouble("BaseSalary"), DepartmentJDBC.instantiateDepartment(rs));
	}
	
	public Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), 
				Date.valueOf(rs.getDate("BirthDate").toString()).toLocalDate(),
				rs.getDouble("BaseSalary"), dep);
	}
}