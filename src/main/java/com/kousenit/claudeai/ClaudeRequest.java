package com.kousenit.claudeai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ClaudeRequest(String model,
                            @NotBlank String prompt,
                            int maxTokensToSample,
                            @Size(max = 2) double temperature) {}
