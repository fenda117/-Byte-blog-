package com.byteblog.model.controller;

import com.byteblog.common.Result;
import com.byteblog.model.dto.ProjectDto;
import com.byteblog.model.service.ProjectService;
import com.byteblog.model.vo.ProjectVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 项目展示控制器
 实现了@RequiredArgsConstructor注解
 实现了@RestController注解
 是看了@RequestMapping注解
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/projects")

public class ProjectController {

    private final ProjectService projectService;
    // 展示所有项目，项目首页用
    @GetMapping
    public Result<List<ProjectVo>> list() {
        return Result.success(projectService.listProject());
    }
    // 展示置顶项目
    @GetMapping("/featured")
    public Result<List<ProjectVo>> listTop() {
        return Result.success(projectService.listTopProject());
    }
    // 根据id查询项目
    @GetMapping("/{id}")
    public Result<ProjectVo> ProjectById(@PathVariable Long id, HttpServletRequest request) {
        // 获取用户ip
        String ip = request.getRemoteAddr();
        return Result.success(projectService.getProjectById(id,ip));
    }
    // 根据姓名查询项目
    @GetMapping("/name/{name}")
    public Result<ProjectVo> ProjectByName(@PathVariable String name) {
        return Result.success(projectService.getProjectByName(name));
    }
    // 新建项目
    @PostMapping
    public Result<ProjectVo> insert(@Valid @RequestBody ProjectDto dto) {
        ProjectVo vo = projectService.insertProject(dto);
        return Result.success("保存项目成功😄蒸蚌",vo);
    }
    // 修改项目
    @PutMapping("/{id}")
    public Result<ProjectVo> update(@PathVariable Long id,@Valid @RequestBody ProjectDto dto) {
        ProjectVo vo = projectService.updateProject(id, dto);
        return  Result.success("修改项目成功，辛苦啦",vo);
    }
    // 删除项目
    @DeleteMapping("/{id}")
    public  Result<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success("删除项目成功！",null);
    }

}
