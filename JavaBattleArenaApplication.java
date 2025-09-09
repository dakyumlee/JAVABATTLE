package com.javabattle.arena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.javabattle.arena"})
public class JavaBattleArenaApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBattleArenaApplication.class, args);
    }
}
