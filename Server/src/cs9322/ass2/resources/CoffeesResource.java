package cs9322.ass2.resources;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import cs9322.ass2.jdbc.CoffeeOrder;
import cs9322.ass2.jdbc.CoffeeOrderDAO;
import cs9322.ass2.jdbc.CoffeeOrderDAOImpl;

@Path("/coffee")
public class CoffeesResource {
	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	

	// Create new order
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response newOrder(
			@FormParam("coffeeType") String coffeeType,			
			@FormParam("addition") String addition,
			@HeaderParam("user") String user,
			@Context HttpServletResponse servletResponse			
			) throws IOException {

		Response.Status stat = Response.Status.CREATED;
		String error = "";
		double totalCost = 0;
		String cost = "";		
		String status = "unstarted";	 // Status automatically set to "unstarted"
		CoffeeOrder order = null;
		String orderID = "";
		
		if (coffeeType != null && coffeeType.equals("")){
			coffeeType = null;
		}
		if (addition != null && addition.equals("")){
			addition = null;
		}
		
		
		CoffeeOrderDAO coffeeDAO = new CoffeeOrderDAOImpl();

		// If the user is not a customer, they may not create a new order.
		try {
			// Coffee Type must be specified
			if (coffeeType == null){
				stat = Response.Status.BAD_REQUEST;
				error = "Must specifiy Coffee Type";
			}
			else if (!coffeeDAO.userIsCustomer(user)){
				stat = Response.Status.UNAUTHORIZED;
				error = "You are not authorised to perform this action!";
			}
			// Check whether Coffee Type exists in database
			else if (!coffeeDAO.coffeeTypeExists(coffeeType)){
				stat = Response.Status.BAD_REQUEST;
				error = "Coffee Type does not Exist!";
			}
			// Check whether Addition Type exists in database
			else if (addition != null && !coffeeDAO.additionTypeExists(addition)){
				stat = Response.Status.BAD_REQUEST;
				error = "Addition Type does not Exist!";
			}
			// Get cost of Coffee and Addition
			else {
				totalCost = coffeeDAO.getCoffeeCost(coffeeType);
				if (addition != null) {
					totalCost += coffeeDAO.getAdditionCost(addition);
				}
				cost = String.valueOf(totalCost);
				order = new CoffeeOrder(coffeeType, cost, addition, status);
				// Add order to database
				// Returns ID of order
				orderID = coffeeDAO.createCoffeeOrder(order);
				// Set-up payment in database
				coffeeDAO.createPayment(orderID, cost, "pending");
				order = new CoffeeOrder();
				// Add cost to CoffeeOrder object
				order.setCost(cost);
				// *****   Add next links *******
				// Add Order URI to CoffeeOrder
				String orderURI = "http://localhost:9080/cs9322.ass2/rest/coffee/" + orderID;
				// Add Payment URI to CoffeeOrder
				String paymentURI = "http://localhost:9080/cs9322.ass2/rest/payment/" + orderID;				

				ArrayList<String> links = new ArrayList<String>();
				links.add(orderURI);
				links.add(paymentURI);

				order.setLinks(links);
				// Add Next Links for a particular order to Database	
				coffeeDAO.addNextURI(orderID , links);
				order.setId(orderID);
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		}
		
		coffeeDAO.closeConn();
		if (stat.compareTo(Response.Status.CREATED) == 0)
			return Response.status(stat).entity(order).build();
		else if (error.isEmpty())
			return Response.status(stat).build();
		else
			return Response.status(stat).entity(error).build();
	}

	// Get all orders
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllOrders(@HeaderParam("user") String user){

		CoffeeOrderDAO coffeeDAO = new CoffeeOrderDAOImpl();
		Response.Status stat = Response.Status.OK;
		String error = "";
		GenericEntity<ArrayList<CoffeeOrder> > entity = null;
		// If the user is not a Barista or a Customer, they may NOT get all orders.
		try {
			if (!coffeeDAO.userIsBarista(user) && !coffeeDAO.userIsCustomer(user)){
				stat = Response.Status.UNAUTHORIZED;
				error = "You are not authorised to perform this action!";
			}
			else {
				ArrayList<CoffeeOrder> orders = new ArrayList<CoffeeOrder>();
				entity = new GenericEntity<ArrayList<CoffeeOrder> >(orders) {};
				orders.addAll(coffeeDAO.getCoffeeOrders().values());
				// If there are no orders, return exception
				if (orders.size() == 0){
					stat = Response.Status.NOT_FOUND;
					error = "There are no orders at present!";
				}
			}
		} catch (SQLException e2) {
			e2.printStackTrace();
			stat = Response.Status.INTERNAL_SERVER_ERROR;
		}	
		// Close DB Connection
		coffeeDAO.closeConn();
		
		if (stat.compareTo(Response.Status.OK) == 0)
			return  Response.status(stat).entity(entity).build();
		else if (error.isEmpty())
			return Response.status(stat).build();
		else
			return Response.status(stat).entity(error).build();
	}

	// Single order
	@Path("{order}")
	public CoffeeResource getOrder(
			@PathParam("order") String id) {

		return new CoffeeResource(uriInfo, request, id);
	}

}
