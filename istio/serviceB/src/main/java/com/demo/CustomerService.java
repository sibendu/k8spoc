package com.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomerService {
		
	@GetMapping("/customer")
	public List<Customer> getCustomers() {

		System.out.println("In CustomerService.getCustomers()");
		
		List<Customer> results = new ArrayList();
		results.add( new Customer("1", "Tad", "M", null));
		results.add( new Customer("2", "Mitra", "A", null));
		
		return results;
	}

}
