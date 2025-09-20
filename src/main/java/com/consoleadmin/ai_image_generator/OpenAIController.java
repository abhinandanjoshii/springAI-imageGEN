package com.consoleadmin.ai_image_generator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OpenAIController {

    private ChatClient chatClient;
    private final ChatMemory chatMemory; // for storing older chats


    public OpenAIController(OpenAiChatModel chatModel) {
        this.chatMemory = MessageWindowChatMemory
                .builder()
                .build();
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("/api/ai/openai/{message}")
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

    @PostMapping("/api/ai/openai/recommend")
    public String recommend(@RequestParam String level , @RequestParam String domain){

        // Note : Restricting to 5 words to avoid Token Costing
        String template = """
                          I want just 5 word response, I am a {level} engineer, and want to explore this domain : {domain}
                          ,suggest me what I can learn
                          """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("domain", domain, "level", level));

       String response = chatClient
               .prompt(prompt)
               .call()
               .content();

       return response;

    }

}
