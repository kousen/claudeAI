package com.kousenit.gemini;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static com.kousenit.gemini.JsonStructure.GeminiRequest;
import static com.kousenit.gemini.JsonStructure.GeminiResponse;

@HttpExchange("/v1beta/models/")
public interface GeminiInterface {
    @PostExchange("{model}:generateContent")
    GeminiResponse getCompletion(
            @PathVariable String model,
            @RequestBody GeminiRequest request);
}
