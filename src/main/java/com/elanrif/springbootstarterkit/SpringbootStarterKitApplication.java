package com.elanrif.springbootstarterkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringbootStarterKitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootStarterKitApplication.class, args);
    }

}
