package com.consoleadmin.ai_image_generator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
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
    public String getAnswer(@PathVariable String message) {
        String response = chatClient
                .prompt(message)
                .call()
                .content(); // just content, other for metadata

        return "Server Running with Chat Client : " + response;
    }

}
