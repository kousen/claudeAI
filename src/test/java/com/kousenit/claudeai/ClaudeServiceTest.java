package com.kousenit.claudeai;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static com.kousenit.claudeai.ClaudeMessageRequest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ClaudeServiceTest {
    @Autowired
    private ClaudeService claudeService;

    @Test
    void independentQuestions() {
        var response = claudeService.getClaudeMessageResponse(
                new ClaudeMessageRequest(
                        ClaudeService.CLAUDE_3_HAIKU,
                        "",
                        1024,
                        0.3,
                        List.of(
                                new Message("user", """
                                        I'm the person who ran the mutant lab that
                                        turned Wade Wilson into DeadPool in the movie.
                                        What is my supervillain name?""")
                        )));
        System.out.println(response);
        response = claudeService.getClaudeMessageResponse(
                new ClaudeMessageRequest(
                        ClaudeService.CLAUDE_3_HAIKU,
                        "",
                        1024,
                        0.3,
                        List.of(
                                new Message("user", """
                                        What did DeadPool call me in the movie?""")
                        )));
        System.out.println(response);
    }

    @Test
    void conversation() {
        var response = claudeService.getClaudeMessageResponse(
                ClaudeService.CLAUDE_3_HAIKU, """
                        I'm the person who ran the mutant lab that
                        turned Wade Wilson into DeadPool in the movie.
                        """,
                "What is my supervillain name?",
                "Your supervillain name is Ajax.",
                "What's my name?");
        System.out.println(response.content().getFirst().text());
    }


    @Test
    void howManyRoads() {
        var response = claudeService.getClaudeResponse("""
                How many roads must a man walk down
                before you can call him a man?
                """, ClaudeService.CLAUDE_2);
        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void pirateCoverLetter() {
        var response = claudeService.getClaudeResponse("""
                Please write a cover letter for a Java developer
                applying for an AI position, written in pirate speak.
                """, ClaudeService.CLAUDE_2);
        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void pirateCoverLetter_opus() {
        String question = """
                Write a cover letter for a Java developer
                applying for an AI programming position,
                written in pirate speak.
                """;
        var response = claudeService.getClaudeMessageResponse(question, ClaudeService.CLAUDE_3_OPUS);
        System.out.println(response);
        assertThat(response.content()
                .getFirst()
                .text()).contains("Ahoy");
    }

    @Test
    void getClaudeResponseToHHG2tG_prompt_claude2() {
        var response = claudeService.getClaudeResponse("""                               
                        According to Douglas Adams, what is the Ultimate Answer
                        to the Ultimate Question of Life, the Universe, and Everything?
                        """,
                ClaudeService.CLAUDE_2
        );
        System.out.println(response);
        assertThat(response).contains("42");
    }

    @Test
    void getClaudeResponseToHHG2tG_prompt_claude_instant() {
        var response = claudeService.getClaudeResponse("""                               
                        According to Douglas Adams, what is the Ultimate Answer
                        to the Ultimate Question of Life, the Universe, and Everything?
                        """,
                ClaudeService.CLAUDE_INSTANT_1
        );
        System.out.println(response);
        assertThat(response).contains("42");
    }

    @Test
    void calculatorTest_haiku() {
        String question = """
                Show each step of the calculation.
                What is the square root of the sum of the numbers of letters
                in the words "hello" and "world"?
                """;
        var response = claudeService.getClaudeMessageResponse(
                question, ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response);
        assertThat(response.content()
                .getFirst()
                .text()).contains("3.16");
    }

    @Test
    void haikuTest_haiku() {
        String question = """
                Write a haiku about Java development
                with AI tools.
                Remember that in a haiku, the first line
                should have 5 syllables, the second line 7 syllables,
                and the third line 5 syllables.
                """;
        var response = claudeService.getClaudeMessageResponse(
                question, ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response.model());
        System.out.println(response.usage());
        String poem = response.content()
                .getFirst()
                .text();
        System.out.println(poem);
        assertThat(poem).isNotBlank();
    }

    @Test
    void calculatorTest_sonnet() {
        String question = """
                Show each step of the calculation.
                What is the square root of the sum of the numbers of letters
                in the words "hello" and "world"?
                """;
        var response = claudeService.getClaudeMessageResponse(
                question, ClaudeService.CLAUDE_3_SONNET);
        System.out.println(response);
        assertThat(response.content()
                .getFirst()
                .text()).contains("3.16");
    }

    @Test
    void calculatorTest_opus() {
        String question = """
                Show each step of the calculation.
                What is the square root of the sum of the numbers of letters
                in the words "hello" and "world"?
                """;
        var response = claudeService.getClaudeMessageResponse(question, ClaudeService.CLAUDE_3_OPUS);
        System.out.println(response);
        assertThat(response.content()
                .getFirst()
                .text()).contains("3.16");
    }

    @Test
    void extractPerson_claude2() {
        int yearsFromNow = 2305 - LocalDate.now()
                .getYear();
        var person = claudeService.extractPerson("""
                        Captain Picard was born %d years from now on the 13th of juillet
                        in La Barre, France, Earth. His given name, Jean-Luc, is of French
                        origin and translates to "John Luke".
                        """.formatted(yearsFromNow),
                ClaudeService.CLAUDE_2);
        System.out.println(person);
        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("Jean-Luc"),
                () -> assertThat(person.lastName()).isEqualTo("Picard"),
                () -> assertThat(person.dob()
                        .getYear()).isCloseTo(2305, Offset.offset(5))
        );
    }

    @Test
    void extractPerson_claude_instant() {
        int yearsFromNow = 2305 - LocalDate.now()
                .getYear();
        var person = claudeService.extractPerson("""
                        Captain Picard was born %d years from now on the 13th of juillet,
                        in La Barre, France, Earth. His given name, Jean-Luc, is of French
                        origin and translates to "John Luke".
                        """.formatted(yearsFromNow),
                ClaudeService.CLAUDE_INSTANT_1);
        System.out.println(person);
        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("Jean-Luc"),
                () -> assertThat(person.lastName()).isEqualTo("Picard"),
                () -> assertThat(person.dob()
                        .getYear()).isCloseTo(2305, Offset.offset(5))
        );
    }

}