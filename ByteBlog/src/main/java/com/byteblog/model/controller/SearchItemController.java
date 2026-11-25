package com.byteblog.model.controller;


import com.byteblog.common.Result;
import com.byteblog.model.service.SearchItemService;
import com.byteblog.model.vo.SearchItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 全站搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchItemController {

    private final SearchItemService searchItemService;

    /**
     * 全站搜索
     */
    @GetMapping
    public Result<List<SearchItemVo>> search(@RequestParam(required = false) String keyword) {
        return Result.success(searchItemService.fullSearch(keyword));
    }
}
