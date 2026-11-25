package com.byteblog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 种类分类 请求体
 */
@Data
public class CategoryDto {

    @NotBlank(message = "分类名不能为空")
    @Size(max = 50, message = "分类名不能超过20个字")
    private String name;

    private Integer sort;

}
