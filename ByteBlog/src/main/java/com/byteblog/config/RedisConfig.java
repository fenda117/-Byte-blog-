package com.byteblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * 作用：自定义 RedisTemplate 的序列化方式，解决存入 Redis 时出现乱码的问题
 * 实现了@Configuration注解，告诉Spring启动时需要加载配置
 */
@Configuration
public class RedisConfig {
    /**
     * 配置自定义的 RedisTemplate
     * @param factory Redis 连接工厂（Spring Boot 读取 properties 配置后自动创建的，我们直接拿来用）
     * @return 返回配置好序列化器的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建一个空的 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 连接工厂塞给模板
        template.setConnectionFactory(factory);
        // 创建 String 类型的序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // JSON 类型的序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        // 设置普通 Key 的序列化方式为 String
        template.setKeySerializer(stringSerializer);
        // 设置普通 Value 的序列化方式为 String
        template.setValueSerializer(jsonSerializer);
        // 设置 Hash 结构里的 Key 序列化为 String
        template.setHashKeySerializer(stringSerializer);
        // 设置 Hash 结构里的 Value 序列化为 JSON
        template.setHashValueSerializer(jsonSerializer);
        // 初始化参数(检查配置)
        template.afterPropertiesSet();

        return template;
    }
}
