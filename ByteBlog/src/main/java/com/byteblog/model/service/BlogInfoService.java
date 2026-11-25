package com.byteblog.model.service;

import com.byteblog.model.dto.BlogInfoDto;
import com.byteblog.model.vo.BlogInfoVo;
import org.springframework.stereotype.Service;

/**
 * 个人信息业务接口
 */

public interface BlogInfoService {

    /**
     *  查询个人信息
     * @return
     */
    BlogInfoVo getInfo();

    /**
     * 更新个人信息
     * @param dto
     * @return
     */
    BlogInfoVo updateInfo(BlogInfoDto dto);

}
