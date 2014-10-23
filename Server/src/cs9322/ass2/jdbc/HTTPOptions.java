package cs9322.ass2.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement
public class HTTPOptions {
	public HTTPOptions (){}

    List<String> list=new ArrayList<String>();

    public void add(String s) { list.add(s); }

    @XmlValue    
    public List<String> getData() { return list; }
}
