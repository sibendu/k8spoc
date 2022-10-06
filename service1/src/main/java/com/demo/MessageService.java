package com.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class MessageService {
	
	@Value("${aws.access.key}")
	private String accessKey;

	@Value("${aws.secret.key}")
	private String secretKey;
	
	@GetMapping("/publish")
	public String publish() {
		
		System.out.println("Publishing to Kinesis Stream started...");
		System.out.println(accessKey+":"+secretKey);
		
		String msg = "Published!";
		System.out.println("Publishing to Kinesis Stream done...");
		
		return msg;
	}
	
	@GetMapping("/consume")
	public String consume() {
		
		System.out.println("Consuming from Kinesis Stream started...");
		
		String msg = "Consumed!";
		System.out.println("Consuming from Kinesis Stream done...");
		System.out.println(accessKey+":"+secretKey);
		
		return msg;
	}
}
