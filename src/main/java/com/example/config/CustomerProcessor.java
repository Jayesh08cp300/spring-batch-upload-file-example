package com.example.config;

import com.example.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
	@Override
	public Customer process(Customer customer) {
		log.info("Processor Thread = " + Thread.currentThread()
				.getName());

		if (null != customer.getCountry() && "china".equalsIgnoreCase(customer.getCountry())) {
			return customer;
		}
		return null;
	}
}