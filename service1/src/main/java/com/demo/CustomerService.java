package com.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.Shard;

@RestController
@EnableMongoRepositories
@RequestMapping("/api")
public class CustomerService {
	
	@Value("${aws.kinesis.stream}")
	private String streamName;
	
	@Value("${aws.kinesis.partitionKey}")
	private String partitionKey;

	@Value("${aws.access.key}")
	private String accessKey;

	@Value("${aws.secret.key}")
	private String secretKey;
	
	String nextShardIterator;

	@Autowired
	CustomerRepository customerRepository;
	
	public AwsCredentialsProvider getAWSCredential() {
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return StaticCredentialsProvider.create(awsCredentials);
	}
	
	@GetMapping("/publish")
	public String publish() {

		System.out.println("Publishing to Kinesis Stream started...");

		String recordData = "Message published at "+new Date();

		KinesisClient kinesis = KinesisClient.builder().region(Region.EU_WEST_1)
				.credentialsProvider(getAWSCredential()).build();
		System.out.println(kinesis);

		SdkBytes sdkBytes = SdkBytes.fromByteArray(recordData.getBytes());
		PutRecordRequest recordRequest = PutRecordRequest.builder()
				.partitionKey(partitionKey)
				.streamName(streamName)
				.data(sdkBytes)
				.build();

		PutRecordResponse recordResponse = kinesis.putRecord(recordRequest);
		kinesis.close();

		String msg = "Published to "+streamName+ ", Message is: " +recordData;
		System.out.println(msg);

		return msg;
	}


	@GetMapping("/seed")
	public String seed() {

		System.out.println("Data seeding started...");

		Customer c1 = new Customer("johnd", "John", "Doe");
		customerRepository.save(c1);

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
		// return groceryItemRepo.findAll();
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
