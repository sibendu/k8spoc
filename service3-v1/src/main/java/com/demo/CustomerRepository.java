package com.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String>{

	List<Customer> findByName(String name);

	List<Customer> findByZip(String zip);

	List<Customer> findByNameAndZip(String name, String zip);
	
	List<Customer> findByTagsIn(List<String> tags);
}
