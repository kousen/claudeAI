package com.kousenit.gemini;

import java.util.List;

record SafetyRating(String category, String probability) { }
record PromptFeedback(List<SafetyRating> safetyRatings) { }

public record GeminiResponse(List<Candidate> candidates, PromptFeedback promptFeedback) {
    public record Candidate(Content content, String finishReason, int index, List<SafetyRating> safetyRatings) {
        public record Content(List<Part> parts, String role) {
            public record Part(String text) { }
        }
    }

}