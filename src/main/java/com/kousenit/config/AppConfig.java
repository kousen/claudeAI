package com.kousenit.config;

import com.kousenit.claudeai.ClaudeInterface;
import com.kousenit.claudeai.ClaudeRecords;
import com.kousenit.claudeai.ContentListSerializer;
import com.kousenit.claudeai.StringContentSerializer;
import com.kousenit.gemini.GeminiInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AppConfig {
    @Bean
    public RestClient claudeRestClient(@Value("${claude.baseurl}") String baseUrl,
                                       @Value("${anthropic.api.key}") String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    @Bean
    public ClaudeInterface claudeInterface(@Qualifier("claudeRestClient") RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ClaudeInterface.class);
    }

    @Bean
    public RestClient geminiRestClient(@Value("${gemini.baseurl}") String baseUrl,
                                       @Value("${googleai.api.key}") String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public GeminiInterface geminiInterface(@Qualifier("geminiRestClient") RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GeminiInterface.class);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomSerialization() {
        return builder ->
                builder.serializerByType(ClaudeRecords.StringContent.class, new StringContentSerializer())
                        .serializerByType(ClaudeRecords.ContentList.class, new ContentListSerializer());
    }
}
