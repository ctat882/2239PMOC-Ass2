package cs9322.ass2.resources;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import cs9322.ass2.exceptions.EmptyException;
import cs9322.ass2.exceptions.OrderStartedException;
import cs9322.ass2.exceptions.PaymentAlreadyMadeException;
import cs9322.ass2.exceptions.PaymentNotMadeException;
import cs9322.ass2.jdbc.CoffeeOrder;
import cs9322.ass2.jdbc.CoffeeOrderDAO;
import cs9322.ass2.jdbc.CoffeeOrderDAOImpl;
import cs9322.ass2.jdbc.HTTPOptions;

public class CoffeeResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;

	

	public CoffeeResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Get a Single Order
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getOrder(@HeaderParam("user") String user) throws ClassNotFoundException{
		
		CoffeeOrderDAO coffeeDAO = new CoffeeOrderDAOImpl();
		Response.Status  status = Response.Status.OK;
		String error_msg = "";
		System.out.println("heeeereeeeeeeeeee!");
		CoffeeOrder order = null;
		
		// If the user is not a Customer or a Barista, they may NOT get a single order.
		try {
			if (!coffeeDAO.userIsCustomer(user) && !coffeeDAO.userIsBarista(user)){
				status = Response.Status.UNAUTHORIZED;
				error_msg = "You are not authorised to perform this action!";
			}
			order = new CoffeeOrder();
			order = coffeeDAO.getCoffeeOrder(id);
			
		} catch (SQLException e2) {
			status = Response.Status.INTERNAL_SERVER_ERROR;
			e2.printStackTrace();
		} catch (EmptyException e) {
			status = Response.Status.NOT_FOUND;
			error_msg = "Order does not Exist!";
		}
		// Close DB Connection
		coffeeDAO.closeConn();
		if (status.compareTo(Response.Status.INTERNAL_SERVER_ERROR) == 0)
			return Response.status(status).build();
		else if (status.compareTo(Response.Status.OK) == 0)
			return Response.status(status).entity(order).build();
		else
			return Response.status(status).entity(error_msg).build();	
	}

	// Update a Single Order
	@PUT
	@Produces(MediaType.APPLICATION_XML)
	public Response updateOrder(
			@FormParam("coffeeType") String type,			
			@FormParam("addition") String addition,			
			@FormParam("status") String status,
			@HeaderParam("user") String user
			) throws ClassNotFoundException{
		
		Response.Status stat = Response.Status.OK;
		String error = "";			

		if (type != null && type.equals("")) type = null;  
		if (addition != null && addition.equals("")) addition = null;
		if (status != null && status.equals(""))	status = null;

		CoffeeOrderDAO coffeeDAO = new CoffeeOrderDAOImpl();
		try {
			// Must specifiy Coffee Type / Addition OR Status
			if (status == null && (type == null && addition == null)){
				stat = Response.Status.BAD_REQUEST;
				error = "Must specifiy Coffee Type / Addition OR Status";
			}		
			// Status should be updated independently
			else if (status != null && ( type != null || addition != null) ){	
				stat = Response.Status.BAD_REQUEST;
				error = "Status should be updated independently";
			}	
			// Status can only be set to "started" or "released"
			else if (status != null && ( !status.equals("started") && !status.equals("released") )){
				stat = Response.Status.BAD_REQUEST;
				error = "Status must be set to \"started\" or \"released\"";
			}
			// Only a Barista can update the "Status" of an order
			else if (status != null && !coffeeDAO.userIsBarista(user)) {
				stat = Response.Status.UNAUTHORIZED;
				error = "\"Status\" may only be changed by a Barista!";
			}
			// Only a customer may update the "Coffee Type" and "Addition"
			else if ( ( type != null || addition != null) && !coffeeDAO.userIsCustomer(user)){
				coffeeDAO.closeConn();
				stat = Response.Status.UNAUTHORIZED;
				error =  "\"Coffee Type\" and \"Addition\" may only be changed by a Customer!";
			}
		} catch (SQLException e1) {
			stat = Response.Status.INTERNAL_SERVER_ERROR;
			e1.printStackTrace();			
		}

		CoffeeOrder order = new CoffeeOrder();
		
		try {
				// If status needs to be updated to "started" or "released"
				if (status != null) 	order = coffeeDAO.updateOrderStatus(id, status);
				// Coffee Type and Addition need to be updated
				else order = coffeeDAO.updateOrder(id, type, addition);
				
		} catch (EmptyException e) {
			e.printStackTrace();
			stat = Response.Status.NOT_FOUND;
			error = "Order does not Exist!";
		} catch (SQLException e) {
			e.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		} catch (OrderStartedException e){
			e.printStackTrace();
			stat = Response.Status.FORBIDDEN;
			error = "Order already started!";
		} catch (PaymentNotMadeException e){
			e.printStackTrace();
			coffeeDAO.closeConn();
			stat = Response.Status.FORBIDDEN;
			error = "Cannot update status to \"released\" as payment has not been made!";
		}
		// Close DB Connection
		coffeeDAO.closeConn();
		if (stat.compareTo(Response.Status.INTERNAL_SERVER_ERROR) == 0)
			return Response.status(stat).build();
		else if (stat.compareTo(Response.Status.OK) == 0)
			return Response.status(stat).entity(order).build();
		else
			return Response.status(stat).entity(error).build();	

	}

	// Delete a Single Order
	@DELETE
	public Response deleteOrder(@HeaderParam("user") String user) throws ClassNotFoundException{

		CoffeeOrderDAO coffeeDAO = new CoffeeOrderDAOImpl();
		Response.Status stat = Response.Status.NO_CONTENT;
		String error = "";
		
		// Only a Customer may delete an order
		try {
			if (!coffeeDAO.userIsCustomer(user)){
//				coffeeDAO.closeConn();
				stat = Response.Status.UNAUTHORIZED;
				error = "An order may only be deleted by a Customer";
			}
			else 
				coffeeDAO.deleteOrder(id);
		} catch (SQLException e1) {
			e1.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		} catch (EmptyException e){
			stat = Response.Status.NOT_FOUND;
			error = "Order does not Exist!";
		} catch (OrderStartedException e){
			stat = Response.Status.FORBIDDEN;
			error = "Order already started!";
		}
		// Close DB Connection
		coffeeDAO.closeConn();
		
		if (error.isEmpty())
			return Response.status(stat).build();
		else
			return Response.status(stat).entity(error).build();
	}

	@OPTIONS
	@Produces(MediaType.APPLICATION_XML)
	public Response getOptions(@HeaderParam("user") String user) throws ClassNotFoundException{
		
		CoffeeOrderDAO coffeeDAO = new CoffeeOrderDAOImpl();
		Response.Status stat = Response.Status.OK;
		String error = "";
		HTTPOptions options = new HTTPOptions();
		
		// If the user is not a Customer or a Barista, they may NOT get Options on an order.
		try {
			if (!coffeeDAO.userIsCustomer(user) && !coffeeDAO.userIsBarista(user)){
				stat = Response.Status.UNAUTHORIZED;
				error = "You are not authorised to perform this action!";
			}
			else {
				options = coffeeDAO.getOptions(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		}
		// Close DB Connection
		coffeeDAO.closeConn();
		if (stat.compareTo(Response.Status.OK) == 0)
			return Response.status(stat).entity(options).build();
		else if (stat.compareTo(Response.Status.INTERNAL_SERVER_ERROR) == 0)
			return Response.status(stat).build();
		else 
			return Response.status(stat).entity(error).build();		
	}
}
