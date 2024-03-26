package com.kousenit.claudeai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.claudeai.ClaudeRecords.*;
import static com.kousenit.claudeai.ClaudeRecords.ClaudeMessageRequest;
import static com.kousenit.claudeai.ClaudeRecords.ClaudeMessageRequest.Message;
import static com.kousenit.claudeai.ClaudeRecords.StringContent;

@SpringBootTest
public class JsonSerializerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testStringContentSerialization() throws Exception {
        StringContent stringContent = new StringContent("Hello, Claude");
        Message message = new Message("user", stringContent);
        String expectedJson = """
                {"role":"user","content":"Hello, Claude"}""";

        String actualJson = objectMapper.writeValueAsString(message);

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    public void testClaudeMessageRequestSerialization() throws Exception {
        ClaudeMessageRequest request = new ClaudeMessageRequest(
                "claude-3-haiku-20240307",
                "",
                1024,
                0.3,
                List.of(new Message("user", new StringContent("Hello, world")))
        );
        String expectedJson = """
                {
                    "model":"claude-3-haiku-20240307",
                    "system":"",
                    "max_tokens":1024,
                    "temperature":0.3,
                    "messages":[{"role":"user","content":"Hello, world"}]
                }""";

        String actualJson = objectMapper.writeValueAsString(request);
        System.out.println(actualJson);

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void serializeMessageContainingTextContent() throws Exception {
        var message = new Message("user", new ContentList(
                List.of(new TextContent("text", "Hello, Claude"))));
        var request = new ClaudeMessageRequest("claude-3-haiku-20240307",
                "", 1024, 0.3, List.of(message));
        var expectedJson = """
                {
                    "model":"claude-3-haiku-20240307",
                    "system":"",
                    "max_tokens":1024,
                    "temperature":0.3,
                    "messages":[
                        {
                            "role":"user",
                            "content":[{"type": "text", "text": "Hello, Claude"}]
                        }
                    ]
                }""";

        var actualJson = objectMapper.writeValueAsString(request);
        System.out.println(actualJson);

        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}
