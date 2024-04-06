package com.example.config;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class ExceptionSkipPolicy implements SkipPolicy {

	@Override
	public boolean shouldSkip(Throwable throwable, long i) throws SkipLimitExceededException {
		return throwable instanceof NumberFormatException;
	}
}