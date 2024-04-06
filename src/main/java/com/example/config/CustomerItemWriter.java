package com.example.config;

import com.example.entity.Customer;
import com.example.reporsitory.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerItemWriter implements ItemWriter<Customer> {

	@Autowired
	private CustomerRepository repository;

	@Override
	public void write(Chunk<? extends Customer> list) throws Exception {
		log.info("Writer Thread = " + Thread.currentThread()
				.getName());
		repository.saveAll(list);
	}

}