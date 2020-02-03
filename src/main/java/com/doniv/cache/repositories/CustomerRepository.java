package com.doniv.cache.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.doniv.cache.entities.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
	
}
