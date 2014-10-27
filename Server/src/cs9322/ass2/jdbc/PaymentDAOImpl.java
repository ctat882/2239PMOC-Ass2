package cs9322.ass2.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import cs9322.ass2.exceptions.AlreadyExistsException;
import cs9322.ass2.exceptions.EmptyException;

public class PaymentDAOImpl implements PaymentDAO {

	Context ctx;
	DataSource ds;
	Connection conn;

	public PaymentDAOImpl() throws ClassNotFoundException{

		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://ctat882.srvr:3306/coffeeDB", "ctat882", "password");
			
		} catch(SQLException e){
			System.out.println("Exception:" + e);	
						

		}
	}

	// Get Single Payment Info
	@Override
	public Payment getPayment(String paymentID) throws EmptyException, SQLException {
		
		
		// TODO Auto-generated method stub
		Payment payment = new Payment();

		try{
			String sql = "SELECT * FROM Payment WHERE PaymentID = " + "'" + paymentID + "'"; 
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);	

			// Get Payment Details (Amount, Payment Type)
			if (generatedKeys.next()) {
				String amount = generatedKeys.getString(2);
				String paymentType = generatedKeys.getString(3);

				payment = new Payment(paymentID, amount, paymentType);

				// If the payment has been made by card, get the card details too.								
				if (paymentType.equals("card")){				
					try {
						sql = "SELECT * FROM CardDetails WHERE ID = " + "'" + paymentID + "'"; 
						Statement stat1 = conn.createStatement();
						ResultSet generatedKeys1 = stat1.executeQuery(sql);
						if (generatedKeys1.next()){
							String name = generatedKeys1.getString(2);
							String cardNo = generatedKeys1.getString(3);
							String expires = generatedKeys1.getString(4);

							payment.setName(name);
							payment.setCardNo(cardNo);
							payment.setExpires(expires);

							stat1.close();
						}

					} catch (SQLException e){
						e.printStackTrace();
						throw new SQLException();
					}					
				}

			}

			stat.close();

		} catch (SQLException e){
			e.printStackTrace();
		} 

		return payment;
	}

	@Override
	public Payment makePayment(String orderID, String paymentType, String name, String cardNo, String expires) throws AlreadyExistsException, SQLException  {
				
		Payment payment = new Payment();

		// If payment has already been made, return error
		try{
			if (paymentMade(orderID)){
				conn.close();
				throw new AlreadyExistsException();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		// Update the payment by changing the status from "pending" to either "cash" or "card"
		try {
			String sql;
			sql = "UPDATE Payment SET PaymentType = " + "'" + paymentType + "'"  + 
					" WHERE PaymentID = " + "'" + orderID + "'";

			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
			stat.close();

			payment.setPaymentID(orderID);
			payment.setPaymentType(paymentType);

		} catch (SQLException e){
			e.printStackTrace();
		}

		try{
			// If the paymentType = "card", then add card details to "CardDetails" Table
			if (paymentType.equals("card")){

				String sql = "INSERT INTO CardDetails (ID,Name,CardNo,Expires)" +                                 
						" VALUES (" + "'" + orderID + "'" + "," + "'" + name + "'" + "," 
						+ "'" + cardNo + "'" + "," 
						+ "'" + expires  + "'" 
						+ ")";

				Statement stat = conn.createStatement();
				stat.executeUpdate(sql);
				stat.close();

				payment.setName(name);	
				payment.setCardNo(cardNo);
				payment.setExpires(expires);

			}

			// Get payment amount
			String sql = "SELECT Amount FROM Payment WHERE PaymentID = " + "'" + orderID + "'"; 
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			if (generatedKeys.next()) {
				payment.setAmount(generatedKeys.getString(1));
			}

			stat.close();

		} catch (SQLException e){
			e.printStackTrace();
			
		} 

		return payment;
	}

	// Check whether payment made
	@Override
	public boolean paymentMade(String paymentID) throws SQLException {

		String sql = "SELECT PaymentType FROM Payment WHERE PaymentID = " + "'" + paymentID + "'";

		try {			
			Statement stat = conn.createStatement();
			ResultSet generatedKeys = stat.executeQuery(sql);
			// If the payment is set to either "card" or "cash", then the payment has already been made
			if (generatedKeys.first()) {
				if (!generatedKeys.getString(1).equals("pending")){ 
					return true;	
				}
			}
			else throw new SQLException();

			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException();

		}	
	}

	@Override
	public HTTPOptions getOptions(String id) throws SQLException {
				
		String sql = "SELECT PaymentType FROM Payment WHERE PaymentID = " + "'" + id + "'";

		Statement stat = conn.createStatement();
		String status = "";
		ResultSet generatedKeys = stat.executeQuery(sql);
		if (generatedKeys.next()){
			status = generatedKeys.getString(1);
		}

		HTTPOptions options = new HTTPOptions();

		if (status.equals("pending")){
			options.add("PUT");
		} else if (status.equals("cash")){
			options.add("GET");
		} else if (status.equals("card")){
			options.add("GET");			
		} 

		stat.close();																		

		return options;
	}

	@Override
	public boolean userIsCustomer(String key) throws SQLException {
		
		String sql = "SELECT CustomerKey FROM Customer WHERE CustomerKey = " + "'" + key + "'";
		Statement stat = conn.createStatement();
		ResultSet generatedKeys = stat.executeQuery(sql);

		if (generatedKeys.next()){ return true; }

		return false;
	}

	@Override
	public boolean userIsBarista(String key) throws SQLException {
		
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
