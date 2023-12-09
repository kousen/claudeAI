package com.kousenit.claudeai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class ClaudeAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaudeAiApplication.class, args);
    }

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
    public ClaudeInterface claudeInterface(RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ClaudeInterface.class);
    }


}
