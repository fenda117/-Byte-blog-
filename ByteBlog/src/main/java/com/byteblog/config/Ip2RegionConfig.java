package com.byteblog.config;

import org.springframework.context.annotation.Configuration;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.Ip2Region;

import org.springframework.context.annotation.Bean;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Ip配置类，兼容了ipv4,ipv4双协议服务
 * 主要为留言板及后续ip相关做配置
 */
@Configuration
public class Ip2RegionConfig {

    @Bean(destroyMethod = "close")
    public Ip2Region ip2Region() throws Exception {

        ClassPathResource v4Resource = new ClassPathResource("ipdb/ip2region_v4.xdb");
        ClassPathResource v6Resource = new ClassPathResource("ipdb/ip2region_v6.xdb");
        Config v4Config = Config.custom()
                .setCachePolicy(Config.BufferCache) // 全量缓存到内存
                .setXdbInputStream(v4Resource.getInputStream())
                .asV4();

        Config v6Config = Config.custom()
                .setCachePolicy(Config.BufferCache) // 全量缓存到内存
                .setXdbInputStream(v6Resource.getInputStream())
                .asV6();

        return Ip2Region.create(v4Config, v6Config);
    }
}
