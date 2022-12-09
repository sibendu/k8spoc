package com.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class CustomerService {
	
	@Value("${service_url}")
	private String serviceUrl;
	
	 @Autowired
	 RestTemplate restTemplate;

	 
	@GetMapping("/customer")
	public List<Customer> getCustomers() {

		System.out.println("In CustomerService.getCustomers()");

		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
	    
	    System.out.println("Calling Service B @ "+serviceUrl);
	    
	    ArrayList<Customer> results = restTemplate.getForObject(serviceUrl, ArrayList.class);
	    
		return results;
	}

}
