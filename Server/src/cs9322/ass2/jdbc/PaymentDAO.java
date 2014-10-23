package cs9322.ass2.jdbc;

import java.sql.SQLException;

import cs9322.ass2.exceptions.AlreadyExistsException;
import cs9322.ass2.exceptions.EmptyException;

public interface PaymentDAO {
	
	public Payment getPayment(String orderID) throws EmptyException, SQLException;
	
	public Payment makePayment(String orderID, String paymentType, String name, String cardNo, String expires) throws AlreadyExistsException, SQLException;
	
	public boolean paymentMade(String paymentID) throws SQLException;

	public HTTPOptions getOptions (String id) throws SQLException; 
	
	public boolean userIsCustomer (String key) throws SQLException;
	
	public boolean userIsBarista (String key) throws SQLException;
	
	public void closeConn();

}
