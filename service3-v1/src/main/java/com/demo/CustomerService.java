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
import org.springframework.web.bind.annotation.RequestParam;
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

	@Autowired
	CustomerRepository customerRepository;

	public AwsCredentialsProvider getAWSCredential() {
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return StaticCredentialsProvider.create(awsCredentials);
	}

	public String getShardIterator(KinesisClient kinesis) {
		String shardIterator;
		String lastShardId = null;

		DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder().streamName(streamName).build();

		List<Shard> shards = new ArrayList<>();
		DescribeStreamResponse streamRes;
		do {
			streamRes = kinesis.describeStream(describeStreamRequest);
			shards.addAll(streamRes.streamDescription().shards());

			if (shards.size() > 0) {
				lastShardId = shards.get(shards.size() - 1).shardId();
			}
		} while (streamRes.streamDescription().hasMoreShards());

		GetShardIteratorRequest itReq = GetShardIteratorRequest.builder().streamName(streamName)
				.shardIteratorType("TRIM_HORIZON").shardId(lastShardId).build();

		GetShardIteratorResponse shardIteratorResult = kinesis.getShardIterator(itReq);
		shardIterator = shardIteratorResult.shardIterator();
		System.out.println("Got shards for stream " + streamName);
		return shardIterator;
	}

	@GetMapping("/consume")
	public List<String> consume() {

		System.out.println("Consuming from Kinesis Stream started...");

		KinesisClient kinesis = KinesisClient.builder().region(Region.EU_WEST_1).credentialsProvider(getAWSCredential())
				.build();
		System.out.println(kinesis);

		String shardIterator = getShardIterator(kinesis);

		GetRecordsRequest recordsRequest = GetRecordsRequest.builder().shardIterator(shardIterator).limit(1000).build();

		GetRecordsResponse result = kinesis.getRecords(recordsRequest);

		List<Record> records = result.records();
		List<String> results = new ArrayList();
		for (Iterator iterator = records.iterator(); iterator.hasNext();) {
			Record record = (Record) iterator.next();
			String msg = new String(record.data().asByteArray());
			System.out.println(record.sequenceNumber() + "  ::  " + msg);
			results.add(msg);
		}

		String msg = "Received from stream " + streamName + " : " + records.size();
		System.out.println(msg);

		return results;
	}

	@GetMapping("/publish")
	public String publish() {

		System.out.println("Publishing to Kinesis Stream started...");

		String recordData = "Message published at " + new Date();

		KinesisClient kinesis = KinesisClient.builder().region(Region.EU_WEST_1).credentialsProvider(getAWSCredential())
				.build();
		System.out.println(kinesis);

		SdkBytes sdkBytes = SdkBytes.fromByteArray(recordData.getBytes());
		PutRecordRequest recordRequest = PutRecordRequest.builder().partitionKey(partitionKey).streamName(streamName)
				.data(sdkBytes).build();

		PutRecordResponse recordResponse = kinesis.putRecord(recordRequest);
		kinesis.close();

		String msg = "Published to " + streamName + ", Message is: " + recordData;
		System.out.println(msg);

		return msg;
	}

	@GetMapping("/seed")
	public String seed() {

		System.out.println("Data seeding started...");

		Customer c1 = new Customer("tad", "Tadeos", "89111");
		Customer c2 = new Customer("brown", "Brown", "89111");
		Customer c3 = new Customer("amitra", "Amlan", "89112");

		customerRepository.save(c1);
		customerRepository.save(c2);
		customerRepository.save(c3);

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

	// Find all records by name, zip
	@GetMapping("/find")
	public List<Customer> find(@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "zip", required = false) String zip) {
		
		List<Customer> customers = new ArrayList<>();

		if(name != null && zip != null) {
			customers = customerRepository.findByNameAndZip(name, zip);
		}else if(name != null && zip == null) {
			customers = customerRepository.findByName(name);
		}else if(name == null && zip != null) {
			customers = customerRepository.findByZip(zip);
		}else {
			customers = customerRepository.findAll();
		}			

		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			Customer c = (Customer) iterator.next();
			System.out.println("Customer record: " + c.getName() + " , " + c.getZip());
		}

		return customers;
	}

	// Find method for records with a particular tag
	@GetMapping("/find/{tag}")
	public List<Customer> findByTag(@PathVariable String tag) {

		List<String> tags = new ArrayList<String>();
		tags.add(tag);

		List<Customer> customers = customerRepository.findByTagsIn(tags);

		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			Customer c = (Customer) iterator.next();
			System.out.println("Customer record: " + c.getName() + " , " + c.getZip());
		}

		return customers;
	}
}
