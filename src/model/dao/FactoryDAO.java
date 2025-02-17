package model.dao;

import model.dao.impl.SellerJDBC;

public class FactoryDAO {
	public static SellerDAO createSellerDAO () {
		return new SellerJDBC();
	}
}