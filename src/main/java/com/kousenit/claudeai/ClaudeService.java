package com.kousenit.claudeai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.kousenit.claudeai.ClaudeRecords.ClaudeMessageRequest.*;
import static com.kousenit.claudeai.ClaudeRecords.*;

@Service
public class ClaudeService {
    public static final Logger logger = LoggerFactory.getLogger(ClaudeService.class);
    public static final Double DEFAULT_TEMPERATURE = 0.3;
    public static final Integer MAX_TOKENS_TO_SAMPLE = 1024;

    public final static String CLAUDE_2 = "claude-2";
    public final static String CLAUDE_INSTANT_1 = "claude-instant-1";

    public final static String CLAUDE_3_HAIKU = "claude-3-haiku-20240307";
    public final static String CLAUDE_3_SONNET = "claude-3-sonnet-20240229";
    public final static String CLAUDE_3_OPUS = "claude-3-opus-20240229";

    private final ClaudeInterface claudeInterface;
    private final ObjectMapper mapper;

    public ClaudeService(ClaudeInterface claudeInterface, ObjectMapper mapper) {
        this.claudeInterface = claudeInterface;
        this.mapper = mapper;
    }

    public String getClaudeResponse(String prompt, String model) {
        return getClaudeResponse("", prompt, model, DEFAULT_TEMPERATURE);
    }

    public String getClaudeResponse(String system, String prompt,
                                    String model, double temperature) {
        ClaudeRequest request = new ClaudeRequest(
                model,
                formatWithSystemPrompt(system, prompt),
                MAX_TOKENS_TO_SAMPLE,
                temperature);
        ClaudeResponse response = claudeInterface.getCompletion(request);
        logger.debug(response.toString());
        return response.completion();
    }

    public String getClaudeMessageResponse(String prompt, String model) {
        ClaudeMessageRequest request = new ClaudeMessageRequest(
                model,
                "",
                MAX_TOKENS_TO_SAMPLE,
                DEFAULT_TEMPERATURE,
                List.of(new Message("user", new StringContent(prompt)))
        );
        return claudeInterface.getMessageResponse(request).content().getFirst().text();
    }

    public ClaudeMessageResponse getClaudeMessageResponse(ClaudeMessageRequest request) {
        return claudeInterface.getMessageResponse(request);
    }

    public ClaudeMessageResponse getClaudeMessageResponse(String model, String system, String... messages) {
        // Create a list of Message objects from the messages where the first message is a system message
        // and the rest alternate between "user" and "assistant"
        String[] types = {"user", "assistant"};

        var userMessages = IntStream.range(0, messages.length)
                .mapToObj(i -> new Message(types[i % 2], new StringContent(messages[i])))
                .toList();
        return getClaudeMessageResponse(
                new ClaudeMessageRequest(model, system, 1024, 0.3, userMessages));
    }

    // System prompts provide context and are provided before the first Human: prompt
    private String formatWithSystemPrompt(String system, String prompt) {
        if (system.isEmpty()) {
            return "\n\nHuman: %s\n\nAssistant:".formatted(prompt);
        }

        return "%s\n\nHuman: %s\n\nAssistant:".formatted(system, prompt);
    }

    public Person extractPerson(String prompt, String model) {
        return extractPerson(prompt, model, DEFAULT_TEMPERATURE);
    }

    public Person extractPerson(String prompt, String model, double temperature) {
        String systemPrompt = """
                In the classpath is a Java record representing a Person. It is annotated
                with @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class), which
                configures Jackson to use snake_case for the JSON field names firstName
                and lastName. The "dob" field is a LocalDate.
                                
                Please extract the relevant fields from the <person> tags in the next
                message into a JSON representation of a Person object. The "origin"
                field represents the place of birth.
                """;
        String text = """
                <person>%s</person>
                """.formatted(prompt);
        try {
            String output = getClaudeResponse(systemPrompt, text, model, temperature);
            logger.debug(output);
            return mapper.readValue(parseJSONFromResponse(output), Person.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseJSONFromResponse(String response) {
        String json = response;
        Pattern pattern = Pattern.compile("```json\n(.*)\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            json = matcher.group(1);
        }
        logger.debug("Extracted: " + json);
        return json;
    }
}
