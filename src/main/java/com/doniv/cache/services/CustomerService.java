package com.doniv.cache.services;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.doniv.cache.entities.Customer;
import com.doniv.cache.repositories.CustomerRepository;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class CustomerService {

	private final CustomerRepository customerRepository;
	
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public CustomerService(CustomerRepository customerRepository,RedisTemplate<String, Object> redisTemplate) {
		this.customerRepository = customerRepository;
		this.redisTemplate = redisTemplate;
	}

	public Customer addCustomer(Customer customer) {
		
		log.info("Adding Customer Data to the db {}, {}", customer.getName() , customer.getEmail());
		Customer cust =  customerRepository.save(customer);
		redisTemplate.opsForHash().put("customersCache", customer.getId(), cust);
		return cust;
	}
	
	public Customer updateCustomer(Customer customer) {
		
		log.info("Updating Customer Data to the db {} -> {}, {}", customer.getId(), customer.getName() , customer.getEmail());
		Customer cust =  customerRepository.save(customer);
		redisTemplate.opsForHash().put("customersCache", customer.getId(), cust);
		return cust;
	}

	public List<Customer> getCustomers() {
		final Map<String, Customer> entries = redisTemplate.<String,Customer>opsForHash().entries("customersCache");
	    final List<Customer> values = entries.values().stream().collect(toList());
	    
	    if (values.size()>0) {
	      return values;
	    }
	   
	    log.info("Cache Miss. Fetching customers from the db");
		try {
			Thread.sleep(5000); //Sleep for 5 seconds to slow down..
		} catch (InterruptedException e) {
			log.error("An error occured --> {}", e.getMessage());
		} 
		List<Customer> customers =  (List<Customer>)customerRepository.findAll();
		Map<Object, Object> customerData = customers.stream().collect(Collectors.toMap(cust-> cust.getId(), cust-> cust));
		redisTemplate.opsForHash().putAll("customersCache",customerData);
		return customers;
	}
	
	public Optional<Customer> getCustomer(long id) {
		log.info("Fetching customer {} from the cache", id);
		Customer customer = (Customer) redisTemplate.opsForHash().get("customersCache", id);
		if(customer != null)
			return Optional.of(customer);
		
		log.info("Cache Miss. Fetching customer {} from the db", id);
		return customerRepository.findById(id);
	}
		
	public void evictAll() {
		log.info("Evicting all customers cache");
		redisTemplate.opsForHash().delete("customersCache");
	}
	
	public void evictSpecificCache(long id) {
		log.info("Evicting customer {} ", id);
		redisTemplate.opsForHash().delete("customersCache", id);

	}
	
	
}
