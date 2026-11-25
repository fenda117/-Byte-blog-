package com.byteblog.model.service;

import com.byteblog.model.vo.SearchItemVo;

import java.util.List;

/**
 * 全站搜索业务接口
 */
public interface SearchItemService {

    List<SearchItemVo> fullSearch(String keyword);

}
