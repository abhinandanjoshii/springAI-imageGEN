package com.consoleadmin.ai_image_generator;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class OpenAIController {

    private ChatClient chatClient;
    private final ChatMemory chatMemory;

    @Autowired
    private VectorStore vectorStore;

    @Qualifier("openAiEmbeddingModel")
    @Autowired
    private EmbeddingModel embeddingModel;


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

    @PostMapping("/api/ai/openai/embedding")
    public ResponseEntity<float[]> embeddings(@RequestParam String text) {
        float[] ans = embeddingModel.embed(text);
        return ResponseEntity.ok(ans);
    }

    @PostMapping("/api/ai/openai/similarityBetweenVectorDimensions")
    public double getSimilarity(@RequestParam String text1, @RequestParam String text2) {
        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += Math.pow(embedding1[i], 2);
            norm2 += Math.pow(embedding2[i], 2);
        }
        // Semantic Searchingg
        return dotProduct*100 / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // Semantic Search : Headphones should search for similar items say Wearables, Speaker, Bluetooth, Earbuds, Airpods etc.
    @PostMapping("/api/ai/openapi/semantic-search")
    public List<Document> getProducts(@RequestParam String text){
        return vectorStore.similaritySearch(text);
    }

}
