package com.bootstrap.workshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WorkshopApplicationTests {

	@org.springframework.test.context.bean.override.mockito.MockitoBean(answers = org.mockito.Answers.RETURNS_DEEP_STUBS)
	private io.micrometer.tracing.Tracer tracer;

	@org.springframework.test.context.bean.override.mockito.MockitoBean(answers = org.mockito.Answers.RETURNS_DEEP_STUBS)
	private io.micrometer.core.instrument.MeterRegistry meterRegistry;

	@Test
	void contextLoads() {
	}

}
