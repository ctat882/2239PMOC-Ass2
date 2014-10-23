package cs9322.ass2.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import cs9322.ass2.exceptions.EmptyException;
import cs9322.ass2.exceptions.OrderStartedException;
import cs9322.ass2.exceptions.PaymentAlreadyMadeException;
import cs9322.ass2.exceptions.PaymentNotMadeException;

public interface CoffeeOrderDAO {
		
	// Create a Coffee Order	
	// Returns the ID of the order
	public String createCoffeeOrder(CoffeeOrder order) throws SQLException; 
	
	// Get a SINGLE Coffee Order	
	public CoffeeOrder getCoffeeOrder(String id) throws EmptyException, SQLException;
	
	// Get ALL Coffee Orders	
	public Map<String,CoffeeOrder> getCoffeeOrders() throws SQLException;
	
	// Update a Coffee Order (excluding status)
	// Return updated representation of the resource
	public CoffeeOrder updateOrder(String id, String type, String addition) throws EmptyException, SQLException, OrderStartedException;
	
	// Update Coffee Order Status
	public CoffeeOrder updateOrderStatus (String orderID, String status) throws EmptyException, SQLException, OrderStartedException, PaymentNotMadeException;
	
	// Cancel a Coffee Order
	public void deleteOrder(String id) throws EmptyException, SQLException, OrderStartedException;
	
	// Create Payment 
	public void createPayment(String orderID, String amount, String paymentType) throws SQLException;
	
	// Add Next Links for a particular order
	public void addNextURI(String orderID, ArrayList<String> links) throws SQLException;
	
	// Check whether Coffee Order Exists
	public boolean orderExists(String orderID) throws SQLException;
	
	// Check whether order started
	public boolean orderStarted (String orderID) throws SQLException;
	
	// Check whether payment made
	public boolean paymentMade (String orderID) throws SQLException;
	
	// Get Coffee Cost
	public double getCoffeeCost (String coffeeName) throws SQLException;
	
	// Get Addition Cost
	public double getAdditionCost (String additionName) throws SQLException;
	
	public boolean coffeeTypeExists (String coffeeType) throws SQLException;
	
	public boolean additionTypeExists (String additionType) throws SQLException;
	
	public HTTPOptions getOptions (String id) throws SQLException; 

	public boolean userIsCustomer (String key) throws SQLException;
	
	public boolean userIsBarista (String key) throws SQLException;
	
	public void closeConn();

}
