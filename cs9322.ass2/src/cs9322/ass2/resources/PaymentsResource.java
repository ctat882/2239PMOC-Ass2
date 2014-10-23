package cs9322.ass2.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/payment")
public class PaymentsResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;
	
	// Single order
	@Path("{order}")
	public PaymentResource getOrder(
		@PathParam("order") String id) {
			
		return new PaymentResource(uriInfo, request, id);
	}

}
