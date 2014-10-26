package cs9322.ass2.logic;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import cs9322.ass2.data.CoffeeOrder;


public class Controller  extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public Controller() {
		super();
		// TODO Auto-generated constructor stub  
	}

	static ClientConfig config = new DefaultClientConfig();
	static Client client = Client.create(config);


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String orderID = request.getParameter("orderID");
		String btnValue = request.getParameter("button" + orderID);
		String submitOrderBtn = request.getParameter("submitOrderBtn");

		// If set to true, we show this info
		boolean showCoffeeOrderInfo = false;
		boolean showPaymentOrderInfo = false;
		boolean showCancelledOrderInfo = false;
		boolean showReleasedOrderInfo = false;
		
		// Store the response of "cancel", "update", "pay" and "option"
		String cancelResp = "";
		String updateOrderResp = "";
		String payOrderResp = "";
		String optionResp = "";

		// Submit New Order
		if (submitOrderBtn != null){
			String orderType = request.getParameter("orderType");
			String additionType = request.getParameter("orderAddition");

			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("coffeeType", orderType);
			formData.add("addition", additionType);
			// Submit the order
			WebResource service = client.resource(getBaseURI());
			ClientResponse clientResp = service.path("rest").path("coffee").header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);

			clientResp.close();			
			
		} else if (btnValue != null){

			// Show Coffee Order Details
			if (btnValue.equals("- Coffee Order " + orderID)){

				showCoffeeOrderInfo = true;

			} else if (btnValue.equals("-- Payment " + orderID)){
				// Show Payment Details
				showPaymentOrderInfo = true;
				
			} else if (btnValue.equals("- Cancelled Order " + orderID)){
				// Show Cancelled Order Details
				showCancelledOrderInfo = true;

			} else if (btnValue.equals("- Released Order " + orderID)){
				// Show Released Order Details
				showReleasedOrderInfo = true;	
				
			}else if (btnValue.equals("Cancel")){
				// Cancel an order

				WebResource service = client.resource(getBaseURI());
				ClientResponse clientResp = service.path("rest").path("coffee").path(orderID).header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).delete(ClientResponse.class);
				int status = clientResp.getStatus();

				// If  Cancel was NOT successful, store the response message.
				if (status != 204) cancelResp = clientResp.getEntity(String.class);
												
				clientResp.close();

				// Update an Order
			} else if (btnValue.equals("Update")){
				String orderType = request.getParameter("updateType" + orderID);
				String additionType = request.getParameter("updateAddition" + orderID);

				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("coffeeType", orderType);
				formData.add("addition", additionType);

				WebResource service = client.resource(getBaseURI());
				ClientResponse clientResp = service.path("rest").path("coffee").path(orderID).header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).put(ClientResponse.class, formData);

				int status = clientResp.getStatus();

				// If Update successful
				if (status == 200) updateOrderResp = "Update Successful";
				// If update Unsuccessful, get Error message
				else updateOrderResp = clientResp.getEntity(String.class);

				clientResp.close();

				// Pay for an order
			} else if (btnValue.equals("Pay")){

				String paymentType = request.getParameter("paymentType" + orderID);
				String name = request.getParameter("name" + orderID);
				String cardNo = request.getParameter("cardNo" + orderID);
				String expires = request.getParameter("expires" + orderID);
				
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("paymentType", paymentType);
				formData.add("name", name);				
				formData.add("cardNo", cardNo);
				formData.add("expires", expires);

				WebResource service = client.resource(getBaseURI());
				ClientResponse clientResp = service.path("rest").path("payment").path(orderID).header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).put(ClientResponse.class, formData);

				int status = clientResp.getStatus();

				// If Payment successful
				if (status == 200) payOrderResp = "Payment made successfully!";
				// If Payment was Unsuccessful, get Error message
				else payOrderResp = clientResp.getEntity(String.class);
				
				clientResp.close();

			   // Get all available options on an order
			} else if (btnValue.equals("Option")){

				WebResource service = client.resource(getBaseURI());
				ClientResponse clientResp = service.path("rest").path("coffee").path(orderID).header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).options(ClientResponse.class);

				optionResp = clientResp.getEntity(String.class);

				clientResp.close();
			}
		}

		// Get All Coffee Orders
		WebResource service = client.resource(getBaseURI());
		ClientResponse clientResp = service.path("rest").path("coffee").header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);

		// If orders found, store the XML file
		// Otherwise, store the returned XML file
		if (clientResp.getStatus() != 404) {

			String orderXML = clientResp.getEntity(String.class);

			clientResp.close();																									

			try {
				Document document = loadXMLFromString(orderXML);
				NodeList nodeList = document.getDocumentElement().getChildNodes();

				// Open Orders
				ArrayList<CoffeeOrder> openOrders = new ArrayList<CoffeeOrder>();
				// Cancelled Orders
				ArrayList<CoffeeOrder> cancelledOrders = new ArrayList<CoffeeOrder>();
				// Released Orders
				ArrayList<CoffeeOrder> releasedOrders = new ArrayList<CoffeeOrder>();

				// Cycle through all the orders
				for (int i = 0; i < nodeList.getLength(); i++) {		        	  
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {		        		 
						Element elem = (Element) node;

						CoffeeOrder order = new CoffeeOrder();

						// Set ID
						String id = elem.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue();
						order.setId(id);

						// Get ALL Order Details for particular order
						clientResp = service.path("rest").path("coffee").path(id).header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);

						orderXML = clientResp.getEntity(String.class);

						clientResp.close();

						// Store order details
						getOrderDetails(id, order, orderXML);

						// ***************

						// Get ALL Payment Details for particular order

						clientResp = service.path("rest").path("payment").path(id).header("user", "customer-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);

						orderXML = clientResp.getEntity(String.class);

						clientResp.close();
						
						// Store payment details
						
						getPaymentDetails(id, order, orderXML);	
						
						// ***********

						// If the ID of the order in the XML file corresponds to the ID of the button selected,
						// Indicate what information will be displayed by the JSP
						if (id.equals(orderID)){
							if (showCoffeeOrderInfo) order.setShowCoffeeOrderInfo(true);
							else if (showPaymentOrderInfo) order.setShowPaymentInfo(true);
							else if (showCancelledOrderInfo) order.setShowCancelledOrderInfo(true);
							else if (showReleasedOrderInfo) order.setShowReleasedOrderInfo(true);
							else if (!cancelResp.isEmpty()) order.setCancelResp(cancelResp);
							else if (!updateOrderResp.isEmpty()) order.setUpdateOrderResp(updateOrderResp);
							else if (!payOrderResp.isEmpty()) order.setPayOrderResp(payOrderResp);
							else if (!optionResp.isEmpty()) order.setOptionResp(optionResp);
						}

						// If the payment has been made, we must show the payment button
						if (!order.getPaymentStatus().equals("Pending")){
							order.setShowPayment(true);
						}
						// If order is still open : ["Started" | "Unstarted"] then add to "open" orders
						if (order.getOrderStatus().contentEquals("Started") || order.getOrderStatus().contentEquals("Unstarted")){
							openOrders.add(order);
						// If the order has been "released" add to released orders
						} else if (order.getOrderStatus().contentEquals("Released")){
							releasedOrders.add(order);
						// If order has been "cancelled", add to cancelled orders.
						} else{
							cancelledOrders.add(order);
						}
					}
				}

				request.setAttribute("openOrders", openOrders);
				request.setAttribute("cancelledOrders", cancelledOrders);
				request.setAttribute("releasedOrders", releasedOrders);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}



	// Get all Order Details from XML file
	private void getOrderDetails(String id, CoffeeOrder order, String orderXML){

		try {

			Document document = loadXMLFromString(orderXML);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			String orderStatus = "";
			String coffeeType = "";
			String additionType = "";
			String cost = "";


			// Cycle through nodes, and retrieve "coffeeType", "additionType" and "cost".
			for (int i = 0; i < nodeList.getLength(); i++) {		        	  
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element elem = (Element) node;

					if (elem.getNodeName().equals("status")){
						orderStatus = elem.getTextContent();
						// Capitalise first letter
						orderStatus = orderStatus.substring(0,1).toUpperCase() + orderStatus.substring(1);
					} else if (elem.getNodeName().equals("type")){
						coffeeType = elem.getTextContent();	
						// Capitalise first letter
						coffeeType = coffeeType.substring(0,1).toUpperCase() + coffeeType.substring(1);
					} else if (elem.getNodeName().equals("addition")){
						additionType = elem.getTextContent();
						// Capitalise first letter
						additionType = additionType.substring(0,1).toUpperCase() + additionType.substring(1);
					} else if (elem.getNodeName().equals("cost")){
						cost = elem.getTextContent();
						// Capitalise first letter
						cost = cost.substring(0,1).toUpperCase() + cost.substring(1);
					}
				}
			}

			order.setOrderStatus(orderStatus);
			order.setCoffeeType(coffeeType);
			order.setAdditionType(additionType);
			order.setCost(cost);

		} catch (Exception e){
			e.printStackTrace();			
		} 
	}

	// Get all Payment Details from XML file
	private void getPaymentDetails(String id, CoffeeOrder order, String orderXML){

		try {
			Document document = loadXMLFromString(orderXML);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			String paymentStatus = "";
			String name = "";
			String cardNo = "";
			String expires = "";


			// Cycle through nodes, and retrieve "coffeeType", "additionType" and "cost".
			for (int i = 0; i < nodeList.getLength(); i++) {		        	  
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {	
					Element elem = (Element) node;

					if (elem.getNodeName().equals("paymentType")){
						paymentStatus = elem.getTextContent();
						// Capitalise first letter
						paymentStatus = paymentStatus.substring(0,1).toUpperCase() + paymentStatus.substring(1);
					} else if (elem.getNodeName().equals("name")){
						name = elem.getTextContent();	
					} else if (elem.getNodeName().equals("cardNo")){
						cardNo = elem.getTextContent();
					} else if (elem.getNodeName().equals("expires")){
						expires = elem.getTextContent();						
					}
				}
			}
			
			order.setPaymentStatus(paymentStatus);
			order.setName(name);
			order.setCardNo(cardNo);
			order.setExpires(expires);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}



	public static Document loadXMLFromString(String xml) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	// Here, the Web application root ... 
	// (then the remainder of the path should contain 'rest/*')
	private static URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://ctat882.srvr:8080/RestServerOne").build();
	}

}
