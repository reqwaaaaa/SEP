package com.pidu.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 系统管理服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.pidu"})
@MapperScan(basePackages = {"com.pidu.system.mapper"})
public class SEPApp {

    public static void main(String[] args) {
        SpringApplication.run(SEPApp.class, args);
        System.out.println("========================================");
        System.out.println("  SEP Platform - System Service Started");
        System.out.println("  API Docs: http://localhost:8081/doc.html");
        System.out.println("========================================");
    }
}
