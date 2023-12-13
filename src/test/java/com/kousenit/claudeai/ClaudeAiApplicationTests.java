package com.kousenit.claudeai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootTest
class ClaudeAiApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        Arrays.stream(context.getBeanDefinitionNames())
                .filter(name -> name.startsWith("gemini"))
                .forEach(System.out::println);
    }

}
