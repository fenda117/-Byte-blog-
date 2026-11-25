package com.byteblog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应数据结构
 * List:当前页数据
 * total:总条数
 * pageNum:当前页码
 * pageSize:每页条数
 * @param <T>
 * 作用:所有列表分页接口返回统一结构，前端分页组件直接对接
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    // 私有字段
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;

    /**
     *  空分页的方法
     * @param pageNum 分页数量
     * @param pageSize 分页大小
     * @return 返回一个空的分页
     * @param <T> 泛型
     */
    public static <T> PageResult<T> empty(int pageNum,int pageSize){
        // 创建一个空的列表(Collections.emptyList())
        return new PageResult<>(Collections.emptyList(),0L,pageNum,pageSize);
    }

    /**
     * 构造分页返回结果的方法
     * @param t
     * @param total 分页总数
     * @param pageNum 分页数量
     * @param pageSize 分页大小
     * @return 返回构造分页
     * @param <T> 泛型
     */
    public static <T> PageResult<T> of(List<T> t,long total,int pageNum,int pageSize){
        return new PageResult<>(t,total,pageNum,pageSize);
    }

}
