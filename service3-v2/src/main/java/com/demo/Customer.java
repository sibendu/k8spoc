package com.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("customer")
public class Customer {

	@Id
    private String id;
    private String name;
    private String zip;
    private String phone;
    
    private List<String> tags;

	public Customer(String id, String name, String zip, String phone) {
		super();
		this.id = id;
		this.name = name;
		this.zip = zip;
		this.phone=phone;
		
		List<String> tags = new ArrayList<String>();
		tags.add("v2");
		
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tag) {
		this.tags = tags;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
        
}
