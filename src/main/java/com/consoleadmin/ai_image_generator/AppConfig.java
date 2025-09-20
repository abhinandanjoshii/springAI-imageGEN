package com.consoleadmin.ai_image_generator;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class AppConfig {

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("localhost", 6379);
    }

    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled , @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled , embeddingModel)
                .indexName("product-index")
                .initializeSchema(true)
                .build();
    }

}
