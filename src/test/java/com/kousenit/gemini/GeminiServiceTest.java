package com.kousenit.gemini;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.gemini.GeminiRecords.*;
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
        ModelList models = service.getModels();
        assertNotNull(models);
        models.models().stream()
                .map(Model::name)
                .filter(name -> name.contains("gemini"))
                .forEach(System.out::println);
    }

    @Test
    void getCompletionWith15Pro() throws Exception {
        String hybhy = PDFTextExtractor.extractText(
                "src/main/resources/pdfs/help-your-boss-help-you_P1.0.pdf");

        String prompt = """
            Here is the text from the book "Help Your Boss Help You":
            
            %s
            
            Answer the following question based on information
            contained in the book:
            
            %s
            """.formatted(hybhy, "What are the top five major points made in the book?");

        GeminiResponse response = service.getCompletionWithModel(
                GeminiService.GEMINI_1_5_PRO,
                new GeminiRequest(List.of(new Content(List.of(new TextPart(prompt))))));
        System.out.println(response);
        String text = response.candidates().getFirst().content().parts().getFirst().text();
        assertNotNull(text);
        System.out.println(text);
        System.out.println("Input Tokens : " + service.countTokens(prompt));
        System.out.println("Output Tokens: " + service.countTokens(text));
    }

    @Test
    void countTokens_fullRequest() {
        var request = new GeminiRequest(
                List.of(new Content(
                        List.of(new TextPart("What is the airspeed velocity of an unladen swallow?")))));
        GeminiCountResponse response = service.countTokens(GeminiService.GEMINI_PRO, request);
        assertNotNull(response);
        System.out.println(response.totalTokens());
        assertThat(response.totalTokens()).isEqualTo(12);
    }

    @Test
    void countTokens() {
        int totalTokens = service.countTokens("What is the airspeed velocity of an unladen swallow?");
        assertThat(totalTokens).isEqualTo(12);
    }

}