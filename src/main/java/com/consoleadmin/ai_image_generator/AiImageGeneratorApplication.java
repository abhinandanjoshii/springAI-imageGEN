package com.consoleadmin.ai_image_generator;

import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { PgVectorStoreAutoConfiguration.class })
public class AiImageGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiImageGeneratorApplication.class, args);
	}

}
