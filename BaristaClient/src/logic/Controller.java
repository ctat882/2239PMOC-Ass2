package logic;
import data.CoffeeOrder;

import java.io.IOException;


// Following imports from lab
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.util.ArrayList;
import java.io.StringReader;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MultivaluedMap;
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
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;


/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
    static ClientConfig config ;
    static Client client; 
    static private ArrayList<CoffeeOrder> currOrders;
    static private WebResource service;
    static private ClientResponse clientResp;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		this.config  = new DefaultClientConfig();
		this.client = Client.create(this.config);
		this.currOrders = new ArrayList<CoffeeOrder>();
		this.service = this.client.resource(getBaseURI());
//		getAllOpenOrders();
		System.setProperty("http.proxyHost", "http://ctat882.srvr");
		System.setProperty("http.proxyPort", "8080");
		
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = "";
		String orderID = "";
		String msg = "";
		String forwardPage;
		
		// Set auto-refresh
		response.setIntHeader("Refresh", 10);
		
		if(request.getParameterMap().containsKey("action")) {
				action = request.getParameter("action");
		}
		if(request.getParameterMap().containsKey("orderID")) {
				orderID = request.getParameter("orderID");
		}
		
		forwardPage = "welcome.jsp";
		
		if(action.isEmpty()) {
//			getAllOrders();
		}		
		else if (action.contentEquals("refresh")) {
			getAllOpenOrders();
		}
		else if (action.contentEquals("prepare") && !orderID.isEmpty()) {
			// Change order "status" to started
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("status", "started");
			this.clientResp = this.service.path("rest").path("coffee").path(orderID).header("user",
					"barista-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).put(ClientResponse.class,formData);
			if (this.clientResp.getStatus() == 200 ) {
				msg = "Order Started";
			}
			else {
				msg = this.clientResp.getEntity(String.class);
			}
			this.clientResp.close();
		
		}
		// Check Payment
		else if(action.contentEquals("check_payment") && !orderID.isEmpty()) {
			this.clientResp = this.service.path("rest").path("payment").path(orderID).header("user",
					"barista-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);
			//TODO check the status code here first before doing anything
			String payXML = this.clientResp.getEntity(String.class);
			try {
				Document doc = loadXMLFromString(payXML);
				NodeList nodeList = doc.getDocumentElement().getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {	
						Element elem = (Element) node;
						if (elem.getNodeName().contentEquals("paymentType")) {
							msg = elem.getTextContent();
							break;
						}
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.clientResp.close();
		}
		else if (action.contentEquals("release")) {
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("status", "released");
			this.clientResp = this.service.path("rest").path("coffee").path(orderID).header("user",
					"barista-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).put(ClientResponse.class,formData);
			if (this.clientResp.getStatus() == 200 ) {
				msg = "Order" + orderID + " Released";
			}
			else {
				msg = this.clientResp.getEntity(String.class);
			}
			this.clientResp.close();
		}
		else {
			
		}
		

		getAllOpenOrders();
		request.setAttribute("openOrders", currOrders);
		request.setAttribute("message", msg);
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/"+forwardPage);
		dispatcher.forward(request, response);
		
	}
	
	
	// Here, the Web application root ... 
		// (then the remainder of the path should contain 'rest/*')
	private static URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://ctat882.srvr:8080/RestServerOne").build();
	}
	
	//fill the currOrders array list with latest orders.
	private void getAllOpenOrders () {
//		System.out.println("getAllOrders");
		this.clientResp = this.service.path("rest").path("coffee").header("user", "barista-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);
		if (this.clientResp.getStatus() != 404) {
			String orderXML = this.clientResp.getEntity(String.class);
			this.clientResp.close();
			this.currOrders.clear();
			try {
				Document document = loadXMLFromString(orderXML);
				NodeList nodeList = document.getDocumentElement().getChildNodes();
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
						this.clientResp = service.path("rest").path("coffee").path(id).header("user", "barista-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);
						orderXML = clientResp.getEntity(String.class);

						this.clientResp.close();

						// Store order details
						getOrderDetails(id, order, orderXML);
						
						// Get ALL Payment Details for particular order

						this.clientResp = service.path("rest").path("payment").path(id).header("user", "barista-123").type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).get(ClientResponse.class);

						orderXML = clientResp.getEntity(String.class);

						this.clientResp.close();
						
						// Store payment details
						
						getPaymentDetails(id, order, orderXML);
						
						if( order.getOrderStatus().contentEquals("Started") || 
								order.getOrderStatus().contentEquals("Unstarted")) {
								this.currOrders.add(order);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	// Get all Payment Details from XML file
		private void getPaymentDetails(String id, CoffeeOrder order, String orderXML){

			try {
				Document document = loadXMLFromString(orderXML);
				NodeList nodeList = document.getDocumentElement().getChildNodes();

				String paymentStatus = "";


				// Cycle through nodes
				for (int i = 0; i < nodeList.getLength(); i++) {		        	  
					Node node = nodeList.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {	
						Element elem = (Element) node;

						if (elem.getNodeName().equals("paymentType")){
							paymentStatus = elem.getTextContent();
							// Capitalise first letter
							paymentStatus = paymentStatus.substring(0,1).toUpperCase() + paymentStatus.substring(1);
						} 
					}
				}				
				order.setPaymentStatus(paymentStatus);				
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
}
