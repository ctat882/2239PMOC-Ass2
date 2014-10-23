package cs9322.ass2.jdbc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class CoffeeOrder {
	private String id;
	private String type;
	private String cost;
	private String addition;
	private String status;
	// Contains the URI(s) for the next step(s) in the process.
	private ArrayList<String> links = new ArrayList<String>();
	
	
	
	public CoffeeOrder(){}
	
	public CoffeeOrder(String id, String type, String cost, String addition, String status){
		this.id = id;
		this.type = type;
		this.cost = cost;
		this.addition = addition;
		this.status = status;		
	}
	
	public CoffeeOrder(String type, String cost, String addition, String status){
		this.type = type;
		this.cost = cost;
		this.addition = addition;
		this.status = status;
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getAddition() {
		return addition;
	}
	public void setAddition(String addition) {
		this.addition = addition;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<String> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
	
}
