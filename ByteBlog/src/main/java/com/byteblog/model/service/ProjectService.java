package com.byteblog.model.service;

import com.byteblog.model.dto.ProjectDto;
import com.byteblog.model.vo.ProjectVo;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * 项目业务接口
 */

public interface ProjectService {
    /**
     * 查询所有项目
     * @return
     */
    List<ProjectVo> listProject();

    /**
     * 置顶项目
     * @return
     */
    List<ProjectVo> listTopProject();

    /**
     * 根据id查询项目
     * @param id
     * @param ip
     * @return
     */
    ProjectVo getProjectById(Long id,String ip);

    /**
     * 根据name查询项目
     * @param name
     * @return
     */
    ProjectVo getProjectByName(String name);

    /**
     * 创建新的展示项目界面
     * @param dto
     * @return
     */
    ProjectVo insertProject(ProjectDto dto);

    /**
     * 修改项目界面
     * @param id
     * @param dto
     * @return
     */
    ProjectVo updateProject(long id,ProjectDto dto);

    /**
     * 删除项目
     * @param id
     * @return
     */
    void deleteProject(long id);


}
