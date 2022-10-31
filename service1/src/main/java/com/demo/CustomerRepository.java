package com.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String>{

	List<Customer> findByFirstName(String firstName);

	List<Customer> findByTag(String tag);

}
