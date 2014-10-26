package cs9322.ass2.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import cs9322.ass2.exceptions.EmptyException;
import cs9322.ass2.exceptions.OrderStartedException;
import cs9322.ass2.exceptions.PaymentAlreadyMadeException;
import cs9322.ass2.exceptions.PaymentNotMadeException;

public class CoffeeOrderDAOImpl implements CoffeeOrderDAO{

	Context ctx;
	DataSource ds;
	Connection conn;

	public CoffeeOrderDAOImpl() throws ClassNotFoundException{

		try {
			//ctx = new InitialContext();
			//ds = (DataSource)ctx.lookup("java:comp/env/jdbc/coffeeDB");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://ctat882.srvr:3306/coffeeDB", "ctat882", "password");
			
			//
			System.out.println("AAAAAAAAAAAAAAAAA" + conn.isClosed());

		} catch(SQLException e){
			System.out.println("Exception:" + e);							
		//} catch (NamingException ne) {

		}
			
	}

	@Override
	public String createCoffeeOrder(CoffeeOrder order) throws SQLException {
		// TODO Auto-generated method stub
		
		System.out.println("AAAAAAAAAAAAAAAAA" + conn.isClosed());


		String type = order.getType();
		String cost = order.getCost();
		String addition = order.getAddition();
		String status = order.getStatus();
		String lastID = "";

		String sql = "INSERT INTO CoffeeOrder (CoffeeType,Cost,Addition,Status)" +      
				" VALUES (" + "'" + type + "'" + "," 
				+ "'" + cost + "'" + "," 
				+ "'" + addition  + "'" + "," 
				+ "'" + status + "'" + ")";

		try{
			Statement stat = conn.createStatement();

			// Insert coffee order into database
			stat.executeUpdate(sql);

			// Get ID of last inserted order
			ResultSet generatedKeys = stat.executeQuery("SELECT last_insert_id()");			
			if (generatedKeys.next()) {				
				lastID = generatedKeys.getString(1);
			}		

			stat.close();	
			
			return lastID;

		} catch (SQLException  e){
			e.printStackTrace();
			throw new SQLException();
			
		} 
	}


	// Get all Coffee Orders
	// Return ID and URI(s)
	@Override
	public Map<String, CoffeeOrder> getCoffeeOrders() throws SQLException{
		
		System.out.println("BBBBBBBBBBBBBBBB" + conn.isClosed());


		Map<String, CoffeeOrder> result = new HashMap<String, CoffeeOrder>();

		// TODO Auto-generated method stub														
		try {
			String sql = "SELECT * FROM CoffeeOrder";
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);	

			while (generatedKeys.next()) {

				CoffeeOrder order = new CoffeeOrder();

				String orderID = generatedKeys.getString(1);
				order.setId(orderID);

				// Get URI Links																					
				sql = "SELECT * FROM OrderNextLinks WHERE orderID = " + "'" + orderID + "'";
				Statement stat1 = conn.createStatement();
				ResultSet generatedKeys1 = stat1.executeQuery(sql);

				ArrayList<String> nextLinks = new ArrayList<String>();

				// Add all the links to the array 
				while (generatedKeys1.next()) {	
					nextLinks.add(generatedKeys1.getString(3));
				}
				// Add the links to the CoffeeOrder object
				order.setLinks(nextLinks);

				result.put(orderID, order);

				stat1.close();
			}

			stat.close();
						
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException();
			
		} 
	}


	// Get Single Coffee Order
	@Override
	public CoffeeOrder getCoffeeOrder(String orderID) throws EmptyException, SQLException {
		// TODO Auto-generated method stub

		CoffeeOrder order = new CoffeeOrder();
		
		System.out.println("CCCCCCC" + conn.isClosed());

		
		// If Coffee Order doesn't exist, throw "EmptyException"												
		if (!orderExists(orderID)){	
			
			throw new EmptyException();
		}

		try {
			String sql = "SELECT * FROM CoffeeOrder WHERE ID = " + "'" + orderID + "'"; 
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);	

			if (generatedKeys.next()) {	
				String type = generatedKeys.getString(2);
				String cost = generatedKeys.getString(3);
				String addition = generatedKeys.getString(4);
				String status = generatedKeys.getString(5);
				order = new CoffeeOrder(orderID,type,cost,addition,status);

			}

			stat.close();

			// Get URI Links
			ArrayList<String> nextLinks = new ArrayList<String>();
			sql = "SELECT * FROM OrderNextLinks WHERE OrderID = " + "'" + orderID + "'";
			Statement stat1 = conn.createStatement();
			ResultSet generatedKeys1 = stat1.executeQuery(sql);

			// Only add payment links to the order
			while (generatedKeys1.next()) {
				if (generatedKeys1.getString(3).contains("payment")){				
					nextLinks.add(generatedKeys1.getString(3));
				}
			}

			order.setLinks(nextLinks);		

			stat1.close();

			return order;

		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();
			
		}

	}


	// Update Coffee Order
	// Can change "CoffeeType" and "Addition"
	// Return updated representation of the resource
	@Override
	public CoffeeOrder updateOrder(String orderID, String type, String addition) throws EmptyException, SQLException, OrderStartedException {
		// TODO Auto-generated method stub
		
		
		System.out.println("EEEEEEEEEEEEEE" + conn.isClosed());


		CoffeeOrder order = new CoffeeOrder();

		// If Coffee Order doesn't exist, throw "EmptyException"												
		if (!orderExists(orderID)){ throw new EmptyException();}
		
																										//CHECKKKK!!!
		// If Coffee Order already started, order cannot be updated
		if (orderStarted(orderID)) {throw new OrderStartedException();}
		

		try {				
			String sql = "UPDATE CoffeeOrder SET CoffeeType = " + "'" + type + "'" +
					", Addition = " + "'" + addition + "'" + 
					" WHERE ID = " + "'" + orderID + "'";

			// Insert coffee order into database
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			stat.close();


			// Get URI Links 																						
			ArrayList<String> nextLinks = new ArrayList<String>();
			sql = "SELECT * FROM OrderNextLinks WHERE OrderID = " + "'" + orderID + "'";
			stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);

			// Only add payment links to the order
			while (generatedKeys.next()) {
				if (generatedKeys.getString(3).contains("payment")){				
					nextLinks.add(generatedKeys.getString(3));
				}
			}

			order = getCoffeeOrder(orderID);
			
			//order.setLinks(nextLinks);								
			//order.setType(type);
			//order.setAddition(addition);
			//order.setCost(cost);
			//order.setStatus("unstarted");

			stat.close();			

			return order;

		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();

		} 


	}


	@Override
	public void deleteOrder(String id) throws EmptyException, SQLException, OrderStartedException {								
		// TODO Auto-generated method stub
		
		System.out.println("FFFFFFFFFFFFFFFF" + conn.isClosed());


		// If Coffee Order doesn't exist, throw "EmptyException"											
		if (!orderExists(id)){ throw new EmptyException();}
		
		// If the order has already started, it cannot be deleted
		if (orderStarted(id)){throw new OrderStartedException();}
																														

		try{																				
			String sql = "UPDATE CoffeeOrder SET Status = " + "'" + "cancelled" + "'" +					 
					     " WHERE ID = " + "'" + id + "'";
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			stat.close();			

		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();

		}
	}


	@Override
	// When an order is created, the preliminary PAYMENT details of an order are entered.
	public void createPayment(String orderID, String amount, String paymentType) throws SQLException {
		// TODO Auto-generated method stub
		
		System.out.println("GGGGGGGGGGGGG" + conn.isClosed());

		
		try{
			String sql = "INSERT INTO Payment (PaymentID,Amount,PaymentType)" + 
					" VALUES (" + "'" + orderID + "'" + "," 
					+ "'" + amount + "'" + "," 
					+ "'" + paymentType  + "'" 
					+ ")";
			Statement stat = conn.createStatement();				
			// Set up Payment in database
			stat.executeUpdate(sql);
			stat.close();

		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();
		}
	}


	@Override
	public void addNextURI(String orderID, ArrayList<String> links) throws SQLException {
		// TODO Auto-generated method stub
		
		System.out.println("HHHHHHHHHHHHHHHH" + conn.isClosed());

		
		try{
			for (int x = 0; x < links.size(); ++x){

				String sql = "INSERT INTO OrderNextLinks(OrderID,Link)" +
						" VALUES (" + "'" + orderID + "'" + "," 
						+ "'" + links.get(x) + "'" +
						")";

				Statement stat = conn.createStatement();					
				stat.executeUpdate(sql);
				stat.close();
			}
			
		} catch (SQLException e){				
			e.printStackTrace();
			throw new SQLException();

		}

	}

	// Update the status of an order
	@Override
	public CoffeeOrder updateOrderStatus(String orderID, String status) throws EmptyException, SQLException, OrderStartedException, PaymentNotMadeException {
		
		System.out.println("IIIIIIIIIIIIIII" + conn.isClosed());

		
		// TODO Auto-generated method stub		

		// If Coffee Order doesn't exist, throw "EmptyException"												
		if (!orderExists(orderID)){ throw new EmptyException();}
		
		// If Payment has not been made, order cannot be updated to "released" status
		if (status.equals("released") && !paymentMade(orderID)){
			throw new PaymentNotMadeException();
		}
		
		// if (status.contentEquals("cancelled") && orderStarted(orderID))
		//	throw new OrderStartedException();
		
		CoffeeOrder order = new CoffeeOrder();
		

		// Update order's status
		try {				
			String sql = "UPDATE CoffeeOrder SET Status = " + "'" + status + "'" +
						 " WHERE ID = " + "'" + orderID + "'";
			
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			stat.close();
			
			// Get the new details of the order
			order = getCoffeeOrder(orderID);
					
			return order;
			
		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();
			
		} 
		
		
	}
	

	@Override
	public boolean orderExists(String orderID) throws SQLException {
		// TODO Auto-generated method stub
		
		System.out.println("JJJJJJJJJJJJJJJJJJJJJ" + conn.isClosed());


		String sql = "SELECT ID FROM CoffeeOrder WHERE ID = " + "'" + orderID + "'";

		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.next()){ return true;	}
			
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException();
			
		}		
	}

	// If payment already made, return true
	@Override
	public boolean paymentMade(String orderID) throws SQLException {
		
		System.out.println("KKKKKKKKKKKKKKKKKK" + conn.isClosed());

		
		
		// TODO Auto-generated method stub
		String sql = "SELECT PaymentType FROM Payment WHERE PaymentID = " + "'" + orderID + "'";

		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.getString(1).equals("pending")){ return false; }
			
		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();
			
		}
		
		return true;
	}

	@Override
	public boolean orderStarted(String orderID) throws SQLException {
		
		System.out.println("LLLLLLLLLLLLLLLLL" + conn.isClosed());

		
		
		// TODO Auto-generated method stub
		String sql = "SELECT Status FROM CoffeeOrder WHERE ID = " + "'" + orderID + "'";
		
		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.next()){
				if (generatedKeys.getString(1).equals("unstarted")){ return false; }
			}
			
		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();			
		}

		return true;
	}

	@Override
	public double getCoffeeCost(String coffeeName) throws SQLException {
		
		System.out.println("MMMMMMMMMMMMMMMM" + conn.isClosed());

		
		// TODO Auto-generated method stub
		
		double cost = 0;
		
		String sql = "SELECT Cost FROM CoffeeCost WHERE Coffee = " + "'" + coffeeName + "'";

		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if(generatedKeys.next()) {
//			cost = (double)generatedKeys.getFloat(1);
				cost = generatedKeys.getDouble(1);
				System.out.println("cost = " + cost);
			} 
			
		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();		
		}
		
		return cost;
	}

	@Override
	public double getAdditionCost(String additionName) throws SQLException {
		// TODO Auto-generated method stub
		
		System.out.println("NNNNNNNNNNNNNNNN" + conn.isClosed());

		
		double cost = 0;
		
		String sql = "SELECT Cost FROM AdditionCost WHERE Addition = " + "'" + additionName + "'";

		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.next()){
				cost = generatedKeys.getDouble(1);
			}
			
		} catch (SQLException e){
			e.printStackTrace();
			throw new SQLException();		
		}
		
		return cost;
	}

	@Override
	public boolean coffeeTypeExists(String coffeeType) throws SQLException {
		// TODO Auto-generated method stub
		
		System.out.println("OOOOOOOOOOOOOO" + conn.isClosed());

		
		String sql = "SELECT Coffee FROM CoffeeCost WHERE Coffee = " + "'" + coffeeType + "'";
		
		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.next()){ return true;	}
			
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException();			
		}
	}

	@Override
	public boolean additionTypeExists(String additionType) throws SQLException {
		
		System.out.println("PPPPPPPPPPPPPPPPPPP" + conn.isClosed());

		
		// TODO Auto-generated method stub
	String sql = "SELECT Addition FROM AdditionCost WHERE Addition = " + "'" + additionType + "'";
		
		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.next()){ return true;	}
			
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException();			
		}
	}

	@Override
	public HTTPOptions getOptions(String id) throws SQLException {
		
		System.out.println("QQQQQQQQQQQQ" + conn.isClosed());

		
		String sql = "SELECT Status FROM CoffeeOrder WHERE ID = " + "'" + id + "'";
		
		Statement stat = conn.createStatement();
		String status = "";
		ResultSet generatedKeys = stat.executeQuery(sql);
		if (generatedKeys.next()){
			status = generatedKeys.getString(1);
		}
		
		HTTPOptions options = new HTTPOptions();
		
		if (status.equals("unstarted")){
			options.add("GET:PUT");
		} else if (status.equals("started")){
			options.add("GET");
		} else if (status.equals("cancelled")){
			options.add("GET");			
		} else if (status.equals("released")){
			options.add("GET");			
		}


		
		return options;
	}

	@Override
	public boolean userIsCustomer(String key) throws SQLException {
		
		//System.out.println("RRRRRRRRRRRRRR" + conn.isClosed());
		
		
		String sql = "SELECT CustomerKey FROM Customer WHERE CustomerKey = " + "'" + key + "'";
		Statement stat = conn.createStatement();
		ResultSet generatedKeys = stat.executeQuery(sql);
		
		if (generatedKeys.next()){ return true; }
		
		return false;
	}

	@Override
	public boolean userIsBarista(String key) throws SQLException {
		
		System.out.println("SSSSSSSSSSSSSSSSSS" + conn.isClosed());

		
		String sql = "SELECT BaristaKey FROM Barista WHERE BaristaKey = " + "'" + key + "'";
		Statement stat = conn.createStatement();
		ResultSet generatedKeys = stat.executeQuery(sql);
		
		if (generatedKeys.next()){ return true; }
		
		return false;
	}

	@Override
	public void closeConn() {
		// TODO Auto-generated method stub
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




}


