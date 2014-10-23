package cs9322.ass2.resources;

import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import cs9322.ass2.exceptions.AlreadyExistsException;
import cs9322.ass2.exceptions.EmptyException;
import cs9322.ass2.jdbc.HTTPOptions;
import cs9322.ass2.jdbc.Payment;
import cs9322.ass2.jdbc.PaymentDAO;
import cs9322.ass2.jdbc.PaymentDAOImpl;

public class PaymentResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String orderID;

	

	public PaymentResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.orderID = id;
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getPayment(@HeaderParam("user") String user){
		
		PaymentDAO paymentDAO = new PaymentDAOImpl();
		Response.Status stat = Response.Status.OK;
		String error = "";
		Payment payment = null;
		
		// If the user is not a Customer or a Barista, they may NOT GET an order.
		try {
			if (!paymentDAO.userIsCustomer(user) && !paymentDAO.userIsBarista(user)){
				stat = Response.Status.UNAUTHORIZED;
				error = "You are not authorised to perform this action!";
			}
			else {
				payment = new Payment();
				payment = paymentDAO.getPayment(orderID);				
			}
		} catch (SQLException e){
			e.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		} catch (EmptyException e){
			e.printStackTrace();
			stat = Response.Status.NOT_FOUND;
			error = "Payment does not Exist!";
		}

		paymentDAO.closeConn();
		if (stat.compareTo(Response.Status.OK)== 0)
			return Response.status(stat).entity(payment).build();
		else if (error.isEmpty())
			return Response.status(stat).build();
		else
			return Response.status(stat).entity(error).build();

	}

	@PUT
	@Produces(MediaType.APPLICATION_XML)
	public Response makePayment(
			@FormParam("paymentType") String paymentType,
			@FormParam("name") String name,
			@FormParam("cardNo") String cardNo,
			@FormParam("expires") String expires,
			@HeaderParam("user") String user
			){

		PaymentDAO paymentDAO = new PaymentDAOImpl();
		Response.Status stat = Response.Status.OK;
		String error = "";
		Payment payment = null;
		
		// Convert empty strings to null
		if (paymentType != null && paymentType.equals("")) paymentType = null;
		if (name != null && name.equals("")) name = null;
		if (cardNo != null && cardNo.equals("")) cardNo = null;
		if (expires != null && expires.equals("")) expires = null;
		// Only a Customer may make a payment
		try {
			if (!paymentDAO.userIsCustomer(user)){
				stat = Response.Status.UNAUTHORIZED;
				error = "Only a customer may make payment!";
			}
			// Payment Type cannot be null or empty string
			else if (paymentType == null){
				stat = Response.Status.BAD_REQUEST ;
				error = "Payment Type CANNOT be null!";
			}
			// Either name, cardNo and expires are ALL NULL
			// 					OR
			// name, cardNo and expires are ALL NON-NULL values
			else if ( !(name != null && cardNo != null && expires != null) && !(name == null && cardNo == null && expires == null) ){
				stat = Response.Status.BAD_REQUEST;
				error = "If payment by card, \"name\", \"cardNo\" and \"expires\" must ALL be Non-Null";
			}
			// Payment Type must either be "cash" or "card"
			else if (paymentType != null && (!paymentType.equals("cash") && !paymentType.equals("card")) ){
				stat = Response.Status.BAD_REQUEST;
				error = "Payment Type must either be \"cash\" or \"card\"";
			}

			// If Payment type is "cash",  then "name", "cardNo" and "expires" must be null
			else if (paymentType.equals("cash") && !(name == null && cardNo == null && expires == null)){
				stat = Response.Status.BAD_REQUEST;
				error = "If Payment Type is \"cash\", then \"name\", \"cardNo\" and \"expires\" must be null";
			}

			// If Payment type is "card", then "name", "cardNo" and "expires" must all be Non-Null
			else if (paymentType.equals("card") && (name == null || cardNo == null || expires == null)){
				stat = Response.Status.BAD_REQUEST;
				error = "If Payment Type is \"card\", then \"name\", \"cardNo\" and \"expires\" must be Non-Null";
			}
			
			else {
				payment = new Payment();
				payment = paymentDAO.makePayment(orderID, paymentType, name, cardNo, expires);
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		} catch (AlreadyExistsException e){
			e.printStackTrace();
			stat = Response.Status.CONFLICT ;
			error = "Payment for this order already made!";
		}
		
		paymentDAO.closeConn();
		if (stat.compareTo(Response.Status.OK)== 0)
			return Response.status(stat).entity(payment).build();
		else if (error.isEmpty())
			return Response.status(stat).build();
		else
			return Response.status(stat).entity(error).build();
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_XML)
	public Response getOptions(@HeaderParam("user") String user){
		
		PaymentDAO paymentDAO = new PaymentDAOImpl();
		HTTPOptions options = null;
		Response.Status stat = Response.Status.OK;
		String error = "";

		// If the user is not a Customer or a Barista, they may NOT get Options on an order.
		try {
			if (!paymentDAO.userIsCustomer(user) && !paymentDAO.userIsBarista(user)){
				stat = Response.Status.UNAUTHORIZED;
				error = "You are not authorised to perform this action!";
			}
			else {
				options = new HTTPOptions();
				options = paymentDAO.getOptions(orderID);
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		}
				

		paymentDAO.closeConn();

		if (stat.compareTo(Response.Status.OK)== 0)
			return Response.status(stat).entity(options).build();
		else if (error.isEmpty())
			return Response.status(stat).build();
		else
			return Response.status(stat).entity(error).build();		
	}

}




