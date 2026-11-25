package com.byteblog.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.byteblog.model.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目实体类Mapper
 * 实现@Mapper注解 由SpringBoot动态代理
 * 继承BaseMapper<Project>，由MyBatis实现基础增删改查
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

}
