package model.dao;

import db.DB;
import model.dao.impl.SellerJDBC;

public class FactoryDAO {
	public static SellerDAO createSellerDAO () {
		return new SellerJDBC(DB.getConn());
	}
}