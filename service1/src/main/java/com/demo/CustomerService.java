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
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerRepositoryV2 customerRepositoryV2;
	
	
	public AwsCredentialsProvider getAWSCredential() {
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return StaticCredentialsProvider.create(awsCredentials);
	}
	
	public String getShardIterator(KinesisClient kinesis) {
		String shardIterator;
        String lastShardId = null;

        DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
            .streamName(streamName)
            .build();

        List<Shard> shards = new ArrayList<>();
        DescribeStreamResponse streamRes;
        do {
            streamRes = kinesis.describeStream(describeStreamRequest);
            shards.addAll(streamRes.streamDescription().shards());

            if (shards.size() > 0) {
                lastShardId = shards.get(shards.size() - 1).shardId();
            }
        } while (streamRes.streamDescription().hasMoreShards());

        GetShardIteratorRequest itReq = GetShardIteratorRequest.builder()
            .streamName(streamName)
            .shardIteratorType("TRIM_HORIZON")
            .shardId(lastShardId)
            .build();

        GetShardIteratorResponse shardIteratorResult = kinesis.getShardIterator(itReq);
        shardIterator = shardIteratorResult.shardIterator();
        System.out.println("Got shards for stream "+streamName);
        return shardIterator;
	}
	
	@GetMapping("/consume")
	public List<String> consume() {

		System.out.println("Consuming from Kinesis Stream started...");
		
		KinesisClient kinesis = KinesisClient.builder().region(Region.EU_WEST_1)
				.credentialsProvider(getAWSCredential()).build();
		System.out.println(kinesis);
		
		String shardIterator = getShardIterator(kinesis);
		
		GetRecordsRequest recordsRequest = GetRecordsRequest.builder()
				.shardIterator(shardIterator)
	            .limit(1000)
	            .build();
		
		GetRecordsResponse result = kinesis.getRecords(recordsRequest);
		
		List<Record> records = result.records();
		List<String> results = new ArrayList();
        for (Iterator iterator = records.iterator(); iterator.hasNext();) {
			Record record = (Record) iterator.next();
			String msg = new String(record.data().asByteArray());
			System.out.println(record.sequenceNumber() + "  ::  "+ msg);
			results.add(msg);
		}
        
		String msg = "Received from stream "+streamName+" : "+records.size();
		System.out.println(msg);

		return results;
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

		Customer c1 = new Customer("johnd", "John", "Doe", "Jr.");
		customerRepository.save(c1);

		CustomerV2 c2 = new CustomerV2("amitra", "Amlan", "Mitra", new Integer(45));
		customerRepositoryV2.save(c2);

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

	//Find all records using V1 code (Customer object as it was during earlier version)
	@GetMapping("/findv1")
	public List<Customer> find() {
		// return groceryItemRepo.findAll();
		List<Customer>  customers = customerRepository.findAll();
		
		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			Customer c = (Customer) iterator.next();
			System.out.println("Customer record: "+c.getFirstName()+" , " + c.getLastName()+ " , "+c.getMiddleName());
		}
		
		return customers;
	}
	
	//Find all records using V2 code (Customer object as it is in most recent version)
	@GetMapping("/findv2")
	public List<CustomerV2> findV2() {
		// return groceryItemRepo.findAll();
		List<CustomerV2>  customers = customerRepositoryV2.findAll();
		
		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			CustomerV2 c = (CustomerV2) iterator.next();
			System.out.println("Customer record: "+c.getFirstName()+" , " + c.getLastName()+ " , "+c.getAge());
		}
		
		return customers;
	}
	
	//Find method for records with a particular tag
	@GetMapping("/findbytag/{tag}")
	public List<Customer> findByTag(@PathVariable String tag) {

		List<Customer> customers = customerRepository.findByTag(tag);
		
		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			Customer c = (Customer) iterator.next();
			System.out.println("Customer record: "+c.getFirstName()+" , " + c.getLastName()+ " , "+c.getMiddleName());
		}
		
		return customers;
	}

	//Find method by First Name and tag (using recent version of Customer object)
	@GetMapping("/findbyfirstname/{firstName}/{tag}")
	public List<CustomerV2> findByFirstNameAndTag(@PathVariable String firstName, @PathVariable String tag) {

		List<CustomerV2> customers = customerRepositoryV2.findByFirstNameAndTag(firstName, tag);
		
		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			CustomerV2 c = (CustomerV2) iterator.next();
			System.out.println("Customer record: "+c.getFirstName()+" , " + c.getLastName()+ " , "+c.getAge());
		}
		
		return customers;
	}
	
	//Find method by Middle Name and tag (using initial/older version of Customer object)
	@GetMapping("/findbymiddlename/{middleName}/{tag}")
	public List<Customer> findByMiddleNameAndTag(@PathVariable String middleName, @PathVariable String tag) {

		List<Customer> customers = customerRepository.findByMiddleNameAndTag(middleName, tag);
		
		for (Iterator iterator = customers.iterator(); iterator.hasNext();) {
			Customer c = (Customer) iterator.next();
			System.out.println("Customer record: "+c.getFirstName()+" , " + c.getLastName()+ " , "+c.getMiddleName());
		}
		
		return customers;
	}
	
	@GetMapping("/find/{id}")
	public Customer find(@PathVariable String id) {
		return customerRepository.findById(id).get();
	}

}
