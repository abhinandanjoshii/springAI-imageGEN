package com.consoleadmin.ai_image_generator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAIController {

    private ChatClient chatClient;

    public OpenAIController(OpenAiChatModel openAiChatModel) {
        this.chatClient = ChatClient.create(openAiChatModel);
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
