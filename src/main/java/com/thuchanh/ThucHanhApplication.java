package com.thuchanh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class ThucHanhApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThucHanhApplication.class, args);
    }

}


