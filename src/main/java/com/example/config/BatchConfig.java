package com.example.config;

import com.example.entity.Customer;
import com.example.listener.StepSkipListener;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class BatchConfig {

	@Bean
	public FlatFileItemReader<Customer> reader() {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csvReader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;

	}

	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

	@Bean
	public CustomerItemWriter writer() {
		return new CustomerItemWriter();
	}

	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("csv-step", jobRepository).<Customer, Customer>chunk(10, transactionManager)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.faultTolerant()
				.listener(skipListener())
				.skipPolicy(skipPolicy())
				.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new JobBuilder("importCustomers", jobRepository).flow(step1(jobRepository, transactionManager))
				.end()
				.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10);
		return asyncTaskExecutor;
	}

	@Bean
	public SkipPolicy skipPolicy() {
		return new ExceptionSkipPolicy();
	}

	@Bean
	public SkipListener skipListener() {
		return new StepSkipListener();
	}

}