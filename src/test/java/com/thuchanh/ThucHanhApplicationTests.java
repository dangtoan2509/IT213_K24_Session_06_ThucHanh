package com.thuchanh;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(excludeName = {
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration",
        "org.springframework.ai.autoconfigure.google.genai.GoogleGenAiAutoConfiguration"
})
class ThucHanhApplicationTests {

    @Test
    void contextLoads() {
    }

}



