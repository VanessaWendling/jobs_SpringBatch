package com.udemy.SpringBatch.OddEven;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class OddEvenBatchConfig {
	@Bean
	public Job writeOddOrEvenJob(JobRepository jobRepository, Step writeOddOrEvenStep) {
		return new JobBuilder("writeOddOrEvenJob", jobRepository)
				.start(writeOddOrEvenStep)
				.incrementer(new RunIdIncrementer())
				.build();
	}

	@Bean
	public Step writeOddOrEvenStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("writeOddOrEvenStep", jobRepository)
				.<Integer, String>chunk(1, transactionManager)
				.reader(countToTenReader())
				.processor(OddOrEvenProcessor())
				.writer(showWriter())
				.build();
	}

	public IteratorItemReader<Integer> countToTenReader() {
		List<Integer> numebersFromOneToTen = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		return new IteratorItemReader<Integer>(numebersFromOneToTen.iterator());
	}
	
	public FunctionItemProcessor<Integer, String> OddOrEvenProcessor(){
		return new FunctionItemProcessor<Integer, String>
		(item -> item % 2 == 0 ? String.format("Item %s is even", item): String.format("Item %s is odd", item));
	}

	public ItemWriter<String> showWriter(){
		return itens -> itens.forEach(System.out::println);
	}
}
