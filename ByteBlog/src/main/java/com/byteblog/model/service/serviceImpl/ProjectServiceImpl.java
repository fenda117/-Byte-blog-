package com.byteblog.model.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.byteblog.common.ResultCode;
import com.byteblog.exception.BusinessException;
import com.byteblog.model.dto.ProjectDto;
import com.byteblog.model.entity.Project;
import com.byteblog.model.mapper.ProjectMapper;
import com.byteblog.model.service.ProjectService;
import com.byteblog.model.vo.ProjectVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * 项目业务实现类
 * 实现了@RequiredArgsConstructor注解，无需@Aotuwire自动注入
 * 实现了@Slf4j注解，增加日志，避免后端出现不明报错
 * 因ProjectMapper继承BaseMapper，故调用mybatisPlus接口LambdaQueryWrapper实现方法。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final RedisTemplate redisTemplate;

    /**
     * 查询所有项目的方法
     * @return
     */
    @Override
    public List<ProjectVo> listProject() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        //代替mapper写sql的方式
        wrapper.orderByAsc(Project::getSort).orderByAsc(Project::getId);
        List<Project> projects = projectMapper.selectList(wrapper);
        List<ProjectVo> projectList = new ArrayList<>();
        for (Project p : projects) {
            ProjectVo vo = toVo(p);
            projectList.add(vo);
        }
        return projectList;
    }

    /**
     * 展示置顶项目的方法
     * @return
     */
    @Override
    public List<ProjectVo> listTopProject() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        //代替mapper写sql的方式
        wrapper.eq(Project::getFeatured, 1)
                .orderByAsc(Project::getSort)
                .orderByAsc(Project::getId);
        List<Project> projects = projectMapper.selectList(wrapper);
        List<ProjectVo> projectList = new ArrayList<>();
        for (Project p : projects) {
            ProjectVo vo = toVo(p);
            projectList.add(vo);
        }
        return projectList;
    }

    /**
     * 根据id搜索项目
     * @param id
     * @return
     */
    @Override
    public ProjectVo getProjectById(Long id,String ip) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND_PROJECT);
        }
        // 记录浏览量(Redis)
        String ipKey = "view:ip:project:" + id + ":" + ip;
        // 防同一Ip刷屏
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(ipKey, "1", 30, TimeUnit.MINUTES);
        if(Boolean.TRUE.equals(absent)){
           // 增加 Redis 浏览量
            redisTemplate.opsForHash().increment("view:project", id.toString(), 1);
        }
        // 3. 组装返回给前端的数据
        ProjectVo vo = toVo(project);
        // 3.1 获取 Redis 中的增量数据
        Object incrementObj = redisTemplate.opsForHash().get("view:project", id.toString());
        int increment = incrementObj != null ? Integer.parseInt(incrementObj.toString()) : 0;
        // 3.2 前端展示 = MySQL 基础值 + Redis 增量值
        vo.setViewCount((project.getViewCount() == null ? 0 : project.getViewCount()) + increment);

        return vo;
    }

    /**
     * 根据项目名称搜索项目
     * @param name
     * @return
     */
    @Override
    public ProjectVo getProjectByName(String name) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getName, name);
        // 用List接住重名防止报错
        List<Project> list = projectMapper.selectList(wrapper);
        if (list.isEmpty()) {
            throw new BusinessException(ResultCode.NOT_FOUND_PROJECT);
        }

        // 拿到实体，转成 VO 返回
        Project project = list.get(0);
        return toVo(project);
    }

    /**
     * 新建项目
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVo insertProject(ProjectDto dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        projectMapper.insert(project);
        log.info("新建项目成功,项目名称为 {}", dto.getName());
        return toVo(project);
    }

    /**
     * 修改项目
     * @param id
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVo updateProject(long id, ProjectDto dto) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND_PROJECT);
        }
        BeanUtils.copyProperties(dto, project);
        project.setId(id);
        projectMapper.updateById(project);
        log.info("更新项目成功,id为 {}", id);
        return toVo(projectMapper.selectById(id));
    }

    /**
     * 删除项目
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(long id) {
        if (projectMapper.deleteById(id) == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND_PROJECT);
        }
        projectMapper.deleteById(id);
        log.info("删除项目成功,id为 {}", id);
    }
    // 内部方法，避免过多重复代码
    private ProjectVo toVo(Project p) {
        if(p == null) return null;
        ProjectVo vo = new ProjectVo();
        vo.setId(p.getId());
        vo.setName(p.getName());
        vo.setDescription(p.getDescription());
        vo.setGithubUrl(p.getGithubUrl());
        vo.setDemoUrl(p.getDemoUrl());
        vo.setCoverUrl(p.getCoverUrl());
        vo.setViewCount(p.getViewCount());
        vo.setCreateTime(p.getCreateTime());
        // 特殊字段boolean->Integer
        vo.setFeatured(p.getFeatured() != null && p.getFeatured() == 1);
        // 特殊字段List<String>类型
        String techStackStr = p.getTechStack();
        if (techStackStr != null && !techStackStr.trim().isEmpty()) {
            // 按照逗号切割成数组，再转成 List
            List<String> techList = Arrays.asList(techStackStr.split(","));
            vo.setTechStackList(techList);
        } else {
            // 如果没有技术栈，给个空列表，防止前端报错
            vo.setTechStackList(new ArrayList<>());
        }

        return vo;
    }
}
