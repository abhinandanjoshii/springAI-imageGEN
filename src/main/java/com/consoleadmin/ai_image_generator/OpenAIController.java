package com.consoleadmin.ai_image_generator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAIController {

    private ChatClient chatClient;
    private final ChatMemory chatMemory; // for storing older chats


    public OpenAIController(ChatClient.Builder chatClientBuilder) {
        this.chatMemory = MessageWindowChatMemory
                .builder()
                .build();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build())
                .build();
    }

    @GetMapping("/api/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message) {
        // entire packet
        ChatResponse chatResponse = chatClient
                .prompt(message)
                .call()
                .chatResponse();

        if (chatResponse == null) {
            return ResponseEntity.notFound().build();
        }

        String model = chatResponse.getMetadata().getModel();

        String response = chatResponse
                .getResult()
                .getOutput()
                .getText();

        return ResponseEntity.ok(response + "\n" + model);
    }

}
