package com.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CustomerRepositoryV2 extends MongoRepository<CustomerV2, String>{

	List<CustomerV2> findByFirstName(String firstName);

	List<CustomerV2> findByFirstNameAndTag(String firstName, String tag);
}
