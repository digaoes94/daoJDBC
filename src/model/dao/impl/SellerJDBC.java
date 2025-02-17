package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
				Seller sell = new Seller();
				sell.setId(rs.getInt("Id"));
				sell.setName(rs.getString("Name"));
				sell.setEmail(rs.getString("Email"));
				sell.setBirth(Date.valueOf(rs.getDate("BirthDate").toString()).toLocalDate());
				sell.setSalary(rs.getDouble("BaseSalary"));
				sell.setDepartment(new Department(rs.getInt("DepartmentId"), rs.getString("DepName")));
				
				return sell;
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
		
		return null;
	}
}