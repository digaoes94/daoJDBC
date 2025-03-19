package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentJDBC implements DepartmentDAO {
	private Connection conn = DB.getConn();
	
	public DepartmentJDBC (Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department dep) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("INSERT INTO department "
					+ "(Name) VALUES (?)"
					+ Statement.RETURN_GENERATED_KEYS
					);
			
			st.setString(1, dep.getName());
			
			int affectedRows = st.executeUpdate();
			
			if (affectedRows > 0) {
				rs = st.getGeneratedKeys();
				
				if (rs.next()) {
					int id = rs.getInt(1);
					dep.setId(id);
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
	public void update(Department dep) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("UPDATE department "
					+ "SET Name = ? WHERE Id = ?"
					+ Statement.RETURN_GENERATED_KEYS
					);
			
			st.setString(1, dep.getName());
			
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
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
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
	public Department findByID(Integer id) {
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
				return instantiateDepartment(rs);
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
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT department.* FROM department ORDER BY Name"
			);
			
			rs = st.executeQuery();
			List<Department> deps = new ArrayList<Department>();
			
			if (rs.next()) {
				while (rs.next()) {
					deps.add(instantiateDepartment(rs));
				}
				
				return deps;
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
	
	public static Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
	}
}
