function showOrderOptions(){
	$('#createOrderTable').toggle('slow');
	
	// If New Order is clicked, change text to Cancel Order and vice versa
	if ($('#createOrderButton').attr('value') === "New Order") {
		$('#createOrderButton').attr('value','Cancel');
	}
	
	else {
		$('#createOrderButton').attr('value', 'New Order');
	}
}
// When a button is clicked, change the value of this button so we can determine the corresponding Order ID of the button clicked.
function changeGetOrderID(id){	
	var numberID = id.split(":");
	numberID = numberID[1];
	document.getElementById('orderID').value = numberID;	
}

// Show options to update the coffee order, "Coffee Type" and "Addition"
function showUpdateOptions(id){	
	$(".updateOptions" + id).toggle('slow');
}

// Show payment options "Cash" or "Card"
function showPayOptions(id){	
	$(".payOptions" + id).toggle('slow');
}

// Show "Name", "Card Number" and "Expires" if the user wants to pay by Card
function showCardOptions(id) {
		var element = document.getElementById(id);	
		
		var selectedOption = element.options[element.selectedIndex].value;
		
		var numberID = id.split(":");
		numberID = numberID[1];			
		$(".cardOptions" + numberID).toggle('slow');	
}

