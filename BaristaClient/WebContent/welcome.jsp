<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
    <c:if test="${ not empty message }">
        ${ message } 
    </c:if>
    <form action='Controller' method="post" name="refresh">
        <input type="submit" value="Refresh">
        <input type="hidden" name="action" value="refresh">        
    </form>
        <c:if test="${not empty openOrders }">
	        <table>
	            <c:forEach var="order"  items="${openOrders }">
		            <tr>
                        <td>
                            Coffee Order ${order.id} 
                        </td>
		                <td>
		                    <form action='Controller' method="post" name="prepare">
		                        <input type="submit" value="Prepare">
		                        <input type="hidden" name="action" value="prepare">
		                        <input type="hidden" name="orderID" value="${order.id }">
		                    </form>                    
		                </td>
		                  <td>
		                    <form action="Controller" method="post" name="check_payment">
		                        <input type="submit" value="Check Payment">
		                        <input type="hidden" name="action" value="check_payment">
		                        <input type="hidden" name="orderID" value="${order.id }">
		                    </form>                    
		                </td>
		                  <td>
		                    <form action="Controller" method="post" name="release">
		                        <input type="submit" value="Release">
		                        <input type="hidden" name="action" value="release">
		                        <input type="hidden" name="orderID" value="${order.id }">
		                    </form>                    
		                </td>
		            </tr>
	            </c:forEach>        
	        </table>
        </c:if>
    
    


</body>
</html>