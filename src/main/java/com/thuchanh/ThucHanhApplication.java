package com.thuchanh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"})
public class ThucHanhApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThucHanhApplication.class, args);
    }

}


