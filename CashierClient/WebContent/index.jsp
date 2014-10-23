<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">	
	<link rel="stylesheet" href="index.css">	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3/jquery.min.js"></script>	
	<script src="index.js"></script>
	
	<title>Cashier/Customer Application</title>
</head>


<body>

	<h1>Cashier/Customer Application</h1>
	
	<div id="createOrderDiv">
		<input class="orderButton" id="createOrderButton" type="button" value="New Order" onClick="showOrderOptions();"/>
	</div>
	
	
		<div id="createOrderTable" style="display:none;">
			<form action='Controller' method='POST'>			
				<table>
					<tr>
						<td>Coffee Type:</td>
						<td>
							<select name="orderType" id="coffeeType">
								<option value="espresso">Espresso</option>
								<option value="macchiato">Macchiato</option>	
								<option value="cappuccino">Cappuccino</option>			
							</select>
						</td>			
					</tr>
					<tr>
						<td>Addition Type:</td>
						<td>
							<select name="orderAddition" id="additionType">
								<option value="skimMilk">Skim Milk</option>
								<option value="extraShot">Extra Shot</option>	
							</select>
						</td>			
					</tr>
					<tr>
						<td><input class="button" name="submitOrderBtn" type="submit" value="Submit Order" onClick="changeGetOrderID(this.id)"/></td>
					</tr>
				</table>
			</form>	
		</div>
	
	
	<div id="ordersTable" style="text-align:center;">
		<form action='Controller' method='POST'>
		<h2>Open Orders</h2>
			<table style="text-align:center;">
				<c:forEach var="item" items="${openOrders}">	
						
				     <tr>				     		 
						<td><input class="orderButton" name="button${item.id}" id="order:${item.id}" type="submit" value="- Coffee Order ${item.id}" onClick="changeGetOrderID(this.id)"/></td>
					</tr>	
					
					<%-- If Coffee Order is clicked, show details about the order --%>				
					<c:if test="${item.showCoffeeOrderInfo}">						
							<tr>
								<td>Order Status: ${item.orderStatus}</td>								
							</tr>
							<tr>
								<td>Coffee Type: ${item.coffeeType}</td>
								
							</tr>
							<tr>
								<td>Addition Type: ${item.additionType}</td>								
							</tr>
							<tr>
								<td>Cost: $${item.cost}</td>								
							</tr>						
					</c:if>
					<%----------------------------------------------------------------%>
					
					<c:if test="${item.showPayment}">
						  <tr>				     		 
							<td><input class="paymentButton" name="button${item.id}" id="payment:${item.id}" type="submit" value="-- Payment ${item.id}" onClick="changeGetOrderID(this.id)"/></td>
							<%-- If Payment Order is clicked, show details about the order --%>	
							<c:if test="${item.showPaymentInfo}">
								<tr>
									<td>Payment Status: Paid (${item.paymentStatus})</td>																	
								</tr>
								<%-- If Payment by card, show card details --%>	
								<c:if test="${item.paymentStatus == 'Card'}">
									<tr>
										<td>Name: ${item.name}</td>																	
									</tr>
									<tr>
										<td>Card No: ${item.cardNo}</td>																	
									</tr>
									<tr>
										<td>Expires: ${item.expires}</td>																	
									</tr>
								</c:if>
							</c:if>													
						 </tr>
					 </c:if>
					 
				     <tr style="text-align:center;">				     		 
						<td><input class="button" name="button${item.id}" id="cancel:${item.id}" type="submit" value="Cancel" onClick="changeGetOrderID(this.id)"/></td>
					</tr>
					<%-- Display response from "Cancel" Operation --%>
					<%-- If there is a response, an error occurred. Display the error. --%>
					
					<c:choose>
						<c:when  test="${not empty item.cancelResp}">
							<tr>
								<td>${item.cancelResp}</td>
							</tr>
						</c:when>					
					</c:choose>
					<%-- Display Option Response --%>
					<tr style="text-align:center;">				     		 
						<td><input class="button" name="button${item.id}" id="option:${item.id}" type="submit" value="Option" onClick="changeGetOrderID(this.id)"/></td>				
					</tr>	
						<c:choose>
						<c:when  test="${not empty item.optionResp}">
							<tr>
								<td>${item.optionResp}</td>
							</tr>
						</c:when>					
					</c:choose>
				   				    		    
				    <%------------------------------%>
				    <tr style="text-align:center;">
				    	<td><input class="button" type="button" value="Update Order" onClick="showUpdateOptions(${item.id})"/></td>
				    </tr>
				    <%-- Show result of update request --%>
				    <c:choose>
						<c:when  test="${not empty item.updateOrderResp}">
							<tr>
								<td>${item.updateOrderResp}</td>
							</tr>
						</c:when>					
					</c:choose>
					<%------------------------------------%>
					<%-- Update Options --%>
				    <tr style="text-align:center; display:none;" class="updateOptions${item.id}" >			     		 
						<td>			
								<select name="updateType${item.id}">
			  						<option value="cappuccino">Cappuccino</option>
			  						<option value="espresso">Espresso</option>
			  						<option value="macchiato">Macchiato</option>
								</select>
						</td>
					</tr>					
					<tr style="text-align:center; display:none;" class="updateOptions${item.id}">
						<td>
								<select name="updateAddition${item.id}">
									<option value="">None</option>
			  						<option value="skimMilk">Skim Milk</option>
			  						<option value="extraShot">Extra Shot</option>
								</select>
						</td>
					</tr>
					
				   	<tr style="text-align:center; display:none;" class="updateOptions${item.id}">
				    	<td><input style="width: 75px; display: inline-block;" class="button" name="button${item.id}" id="update:${item.id}" type="submit" value="Update" onClick="changeGetOrderID(this.id)"/></td>
				    </tr>				  
					<%-- -------------- --%>
					
					<%-- Payment Options --%>				   	
				   	<tr style="text-align:center;">
				   		<td><input class="button" type="button" value="Pay Order" onClick="showPayOptions(${item.id})"/></td>
				   	</tr>		
				   			     		
				   	 <c:choose>
						<c:when  test="${not empty item.payOrderResp}">
							<tr>
								<td>${item.payOrderResp}</td>
							</tr>
						</c:when>					
					</c:choose>
				   	 
				    <tr style="text-align:center; display:none;" class="payOptions${item.id}" >				     		 
						<td>	
																				
								<select id="paymentType:${item.id}" name="paymentType${item.id}" onchange="showCardOptions(this.id)">
									<option value="cash">Cash</option>
				  					<option value="card">Card</option>
								</select>
							
						</td>
					</tr>
					
					<tr style="text-align:center;  display:none;" class="cardOptions${item.id}">
						<td>	
							Name: <input type="text" name="name${item.id}" class="textBox"><br>							
						<td>
					
					</tr>
					<tr style="text-align:center;  display:none;" class="cardOptions${item.id}">
						<td>
							Card No: <input type="text" name="cardNo${item.id}" class="textBox"><br>
						</td>
					</tr>
					<tr style="text-align:center;  display:none;" class="cardOptions${item.id}">
						<td>
							Expires: <input type="text" name="expires${item.id}" class="textBox"><br>							
						</td>
					</tr>
					
				  	<tr style="text-align:center; display:none;" class="payOptions${item.id}">
				    	<td><input style="width: 75px; display: inline-block;" class="button" name="button${item.id}" id="update:${item.id}" type="submit" value="Pay" onClick="changeGetOrderID(this.id)"/></td>
				    </tr>
				   
				    <%---------------------%>				  
				      			    			
				</c:forEach>				
			 </table>
		
			 
			 
			 <h2>Cancelled Orders</h2>
			<table>
				<c:forEach var="item" items="${cancelledOrders}">
					<tr>				     		 
						<td><input class="cancelledOrderButton" name="button${item.id}" id="order:${item.id}" type="submit" value="- Cancelled Order ${item.id}" onClick="changeGetOrderID(this.id)"/></td>						
					</tr>
					<%-- If a Cancelled Coffee Order is clicked, show details about the order --%>				
					<c:if test="${item.showCancelledOrderInfo}">						
							<tr>
								<td>Order Status: ${item.orderStatus}</td>								
							</tr>
							<tr>
								<td>Coffee Type: ${item.coffeeType}</td>
								
							</tr>
							<tr>
								<td>Addition Type: ${item.additionType}</td>								
							</tr>
							<tr>
								<td>Cost: $${item.cost}</td>								
							</tr>						
					</c:if>						
				</c:forEach>				
			 </table>	 
		
			
			
			<h2>Released Orders</h2>
			 <table>
			 	<c:forEach var="item" items="${releasedOrders}">
			 		<tr>				     		 
						<td><input class="releasedOrderButton" name="button${item.id}" id="order:${item.id}" type="submit" value="- Released Order ${item.id}" onClick="changeGetOrderID(this.id)"/></td>						
					</tr>
					<%-- If a Released Coffee Order is clicked, show details about the order --%>				
					<c:if test="${item.showReleasedOrderInfo}">						
							<tr>
								<td>Order Status: ${item.orderStatus}</td>								
							</tr>
							<tr>
								<td>Coffee Type: ${item.coffeeType}</td>
								
							</tr>
							<tr>
								<td>Addition Type: ${item.additionType}</td>								
							</tr>
							<tr>
								<td>Cost: $${item.cost}</td>								
							</tr>						
					</c:if>		
			 	</c:forEach>
			 </table>
			 
			 <%-- This field is sent with the form to indicate the Order ID of the button that was clicked --%>
			 <input type="hidden" name="orderID" id="orderID" value="">	
			 
		</form>	
	</div> 
		
	
	
	
</body>
</html>