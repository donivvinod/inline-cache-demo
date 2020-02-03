package com.doniv.cache.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.doniv.cache.entities.Customer;
import com.doniv.cache.services.CustomerService;

@RestController
public class CustomerController {

	private final CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping("/customers")
	public Customer addCustomer(@RequestBody Customer customer) {
		customerService.addCustomer(customer);
		return customer;
	}

	@PostMapping("/customers/{id}")
	public Customer updateCustomer(@RequestBody Customer customer) {
		customerService.addCustomer(customer);
		return customer;
	}

	
	@GetMapping("/customers")
	public List<Customer> getCustomers() {
		 
		return (List<Customer>) customerService.getCustomers();
	}
	
	@GetMapping("/customers/{id}")
	public Optional<Customer> getCustomers(@PathVariable(name = "id") long id) {
		 
		return customerService.getCustomer(id);
	}
	
	
	@GetMapping("/customers/evictCache/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void evictCustomersCache(@PathVariable(name = "id") long id) {
		customerService.evictSpecificCache(id);
	}



}
