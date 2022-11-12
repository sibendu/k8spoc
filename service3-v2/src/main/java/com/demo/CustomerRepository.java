package com.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String>{

	List<Customer> findByName(String name);

	List<Customer> findByZip(String zip);
	
	List<Customer> findByPhone(String phone);

	List<Customer> findByNameAndZip(String name, String zip);
	
	List<Customer> findByNameAndPhone(String name, String phone);
	
	List<Customer> findByZipAndPhone(String zip, String phone);
	
	List<Customer> findByNameAndZipAndPhone(String name, String zip, String phone);
	
	List<Customer> findByTagsIn(List<String> tags);
	
	List<Customer> findByZipAndTagsIn(String zip, List<String> tags);
}
