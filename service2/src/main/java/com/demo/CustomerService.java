package com.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableMongoRepositories
@RequestMapping("/api")
public class CustomerService {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@GetMapping("/seed")
	public String seed() {
				
		System.out.println("Data creation started...");

		Customer c2 = new Customer("bellac", "Bella", "Curtis");
		customerRepository.save(c2);

		String msg = "Populated seed data successfully!";
		System.out.println("Data creation complete...");
        
		return msg;
	}	
	
	@GetMapping("/clean")
	public String clean() {		
		
		customerRepository.deleteAll();
		String msg = "Cleaned seed data successfully!";
		System.out.println(msg);
        
		return msg;
	}	
	
	@GetMapping("/find")
	public List<Customer> find() {		
		//return groceryItemRepo.findAll();
		return customerRepository.findAll();
	}
	
	@GetMapping("/find/{id}")
	public Customer find(@PathVariable String id) {		
		return customerRepository.findById(id).get();
	}
	
	@GetMapping("/find/name/{firstName}")
	public List<Customer> findName(@PathVariable String firstName) {		
		return customerRepository.findByFirstName(firstName);
	}
		 
}
