package com.kousenit.gemini;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GeminiServiceTest {
    @Autowired
    private GeminiService service;

    @Test
    void getCompletion_HHGtTG_question() {
        String text = service.getCompletion("""
            What is the Ultimate Answer to
            the Ultimate Question of Life, the Universe,
            and Everything?
            """);
        assertNotNull(text);
        System.out.println(text);
        assertThat(text).contains("42");
    }

    @Test
    void getCompletion() {
        String text = service.getCompletion("""
            How many roads must a man walk down
            before you can call him a man?
            """);
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void pirateCoverLetter() {
        String text = service.getCompletion("""
            Please write a cover letter for a Java developer
            applying for an AI position, written in pirate speak.
            """);
        assertNotNull(text);
        System.out.println(text);
    }


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
                "A_cheerful_robot.png");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void getModels() {
        JsonStructure.ModelList models = service.getModels();
        assertNotNull(models);
        models.models().stream()
                .map(JsonStructure.Model::name)
                .filter(name -> name.contains("gemini"))
                .forEach(System.out::println);
    }

    @Test @Disabled("Ultimate model not yet available")
    void getCompletionWithUltimateModel() {
        String question = """
            What is the Ultimate Answer to
            the Ultimate Question of Life, the Universe,
            and Everything?
            """;
        JsonStructure.GeminiResponse response = service.getCompletionWithModel(
                GeminiService.GEMINI_ULTIMATE,
                new JsonStructure.GeminiRequest(
                        List.of(new JsonStructure.Content(List.of(new JsonStructure.TextPart(question))))));
        String text = response.candidates().getFirst().content().parts().getFirst().text();
        assertNotNull(text);
        System.out.println(text);
    }
}