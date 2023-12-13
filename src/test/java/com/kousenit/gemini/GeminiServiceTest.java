package com.kousenit.gemini;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GeminiServiceTest {
    @Autowired
    private GeminiService service;

    @Test
    void writeAStory() {
        String text = service.getCompletion("Write a story about a magic backpack.");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void describeAnImage() throws Exception {
        String text = service.getCompletionWithImage(
                "Describe this image",
                "ClaudeAI_in_Spring.png");
        assertNotNull(text);
        System.out.println(text);
    }

}