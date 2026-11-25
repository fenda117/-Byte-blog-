package com.byteblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 个人博客项目启动类
 */
@EnableScheduling
@SpringBootApplication
@MapperScan("com.byteblog.model.mapper")
public class ByteBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByteBlogApplication.class, args);
        System.out.println("【ByteBlog】个人博客项目启动成功 http://127.0.0.1:8080/");
    }


}
