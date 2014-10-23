package cs9322.ass2.jdbc;



import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Payment {
	
	
	private String paymentID;
	private String amount;
	private String paymentType;
	// If credit card used, the following will be filled.
	private String name;
	private String cardNo;
	private String expires;
	
	
	public Payment(){}

	public Payment(String paymentID, String amount, String paymentType){
		this.paymentID = paymentID;
		this.amount = amount;
		this.paymentType = paymentType;
	}
	
	// If credit card is used...
	public Payment(String paymentID, String amount, String paymentType, String name, String cardNo, String expires){
		this.paymentID = paymentID;
		this.amount = amount;
		this.paymentType = paymentType;
		this.name = name;
		this.cardNo = cardNo;
		this.expires = expires;
	}
	
	
	public String getPaymentID() {
		return paymentID;
	}
	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
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

}
