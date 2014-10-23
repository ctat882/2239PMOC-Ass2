package cs9322.ass2.data;

public class CoffeeOrder {

	// General info
	private String id;
	private String orderLink;
	private String paymentLink;

	// Coffee Order Details
	private String orderStatus;
	private String coffeeType;
	private String additionType;
	private String cost;

	// Payment Details
	private String paymentStatus;
	private String name;
	private String cardNo;
	private String expires;

	// Indicate what will be displayed in JSP file
	private boolean showPayment = false;
	// Only one of these will be set
	private boolean showCoffeeOrderInfo = false;
	private boolean showPaymentInfo = false;
	private boolean showCancelledOrderInfo = false;
	private boolean showReleasedOrderInfo = false;


	// Response messages for "cancel", "update", "pay" and "option"
	private String cancelResp;
	private String updateOrderResp;
	private String payOrderResp;
	private String optionResp;



	public String getCoffeeType() {
		return coffeeType;
	}


	public void setCoffeeType(String coffeeType) {
		this.coffeeType = coffeeType;
	}


	public String getAdditionType() {
		return additionType;
	}


	public void setAdditionType(String additionType) {
		this.additionType = additionType;
	}


	public String getCost() {
		return cost;
	}


	public void setCost(String cost) {
		this.cost = cost;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}


	public String getExpires() {
		return expires;
	}


	public void setExpires(String expires) {
		this.expires = expires;
	}


	public CoffeeOrder() {}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getOrderStatus() {
		return orderStatus;
	}


	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}


	public String getPaymentStatus() {
		return paymentStatus;
	}


	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}


	public String getOrderLink() {
		return orderLink;
	}


	public void setOrderLink(String orderLink) {
		this.orderLink = orderLink;
	}


	public String getPaymentLink() {
		return paymentLink;
	}


	public void setPaymentLink(String paymentLink) {
		this.paymentLink = paymentLink;
	}


	public boolean isShowPayment() {
		return showPayment;
	}


	public void setShowPayment(boolean showPayment) {
		this.showPayment = showPayment;
	}

	public boolean isShowCoffeeOrderInfo() {
		return showCoffeeOrderInfo;
	}


	public void setShowCoffeeOrderInfo(boolean showCoffeeOrderInfo) {
		this.showCoffeeOrderInfo = showCoffeeOrderInfo;
	}


	public boolean isShowPaymentInfo() {
		return showPaymentInfo;
	}


	public void setShowPaymentInfo(boolean showPaymentInfo) {
		this.showPaymentInfo = showPaymentInfo;
	}


	public boolean isShowCancelledOrderInfo() {
		return showCancelledOrderInfo;
	}


	public void setShowCancelledOrderInfo(boolean showCancelledOrderInfo) {
		this.showCancelledOrderInfo = showCancelledOrderInfo;
	}


	public String getCancelResp() {
		return cancelResp;
	}


	public void setCancelResp(String cancelResp) {
		this.cancelResp = cancelResp;
	}


	public String getUpdateOrderResp() {
		return updateOrderResp;
	}


	public void setUpdateOrderResp(String updateOrderResp) {
		this.updateOrderResp = updateOrderResp;
	}


	public String getPayOrderResp() {
		return payOrderResp;
	}


	public void setPayOrderResp(String payOrderResp) {
		this.payOrderResp = payOrderResp;
	}


	public boolean isShowReleasedOrderInfo() {
		return showReleasedOrderInfo;
	}


	public void setShowReleasedOrderInfo(boolean showReleasedOrderInfo) {
		this.showReleasedOrderInfo = showReleasedOrderInfo;
	}


	public String getOptionResp() {
		return optionResp;
	}


	public void setOptionResp(String optionResp) {
		this.optionResp = optionResp;
	}

	
}
