package com.kousenit.claudeai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ClaudeRecords {
    public record ClaudeMessageRequest(
            String model,
            @JsonProperty("system") String systemPrompt,
            @JsonProperty("max_tokens") int maxTokens,
            double temperature,
            List<Message> messages
    ) {
        public record Message(String role, MessageContent content) {}
    }

    sealed interface MessageContent permits StringContent, ContentList {}

    public record ContentList(List<Content> contents) implements MessageContent {}

    sealed interface Content permits ImageContent, TextContent {}

    public record ImageContent(String type, ImageSource source) implements Content {
        record ImageSource(String type,
                           @JsonProperty("media_type") String media_type,
                           String data) {}
    }

    public record TextContent(String type, String text) implements Content {}

    public record StringContent(String text) implements MessageContent {
        @Override
        public String toString() {
            return text;
        }
    }

    public record ClaudeMessageResponse(
            String id,
            String type,
            String role,
            String model,
            @JsonProperty("stop_reason") String stopReason,
            @JsonProperty("stop_sequence") String stopSequence,
            List<Content> content,
            Usage usage) {
        public record Content(String type, String text) {
        }

        public record Usage(@JsonProperty("input_tokens") int inputTokens,
                            @JsonProperty("output_tokens") int outputTokens) {
        }
    }

}
