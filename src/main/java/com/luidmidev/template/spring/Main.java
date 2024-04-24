package com.luidmidev.template.spring;

import com.waipersoft.store.FileStoreProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.luidmidev.template.spring", "com.waipersoft"})
@EnableConfigurationProperties({FileStoreProperties.class})
@ConfigurationPropertiesScan(basePackages = {"com.waipersoft"})

public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
