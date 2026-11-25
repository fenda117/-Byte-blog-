package com.byteblog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一结果集
 * 实现序列化
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    //序列化版本号
    @Serial
    private static final long serialVersionUID = 1L;
    //私有字段
    private Integer code;
    private String msg;
    private T data;
    /**
     * 成功有参数的方法
     * @param data 返回的数据
     * @return 返回成功的快捷方法
     */
    public static<T> Result<T> success(T data){
        return new Result<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMsg(),data);
    }

    /**
     * 成功无参数的方法
     * @return 返回成功的快捷方法(无参数)
     * @param <T> 泛型
     */
    public static<T> Result<T> success(){
        return success(null);
    }

    /**
     * 成功自定义消息及数据的方法
     * @param msg 自定义消息
     * @param data 数据
     * @return 自定义成功消息的方法
     * @param <T>  泛型
     */
    public static<T> Result<T> success(String msg,T data){
        return new Result<>(ResultCode.SUCCESS.getCode(),msg,data);
    }

    /**
     * 失败返回500的方法
     * @param msg 自定义消息
     * @return 返回500失败的方法
     * @param <T> 泛型
     */
    public static<T> Result<T> fail(String msg){
        return new Result<>(ResultCode.INTERNAL_ERROR.getCode(),msg,null);
    }

    /**
     * 失败传递枚举信息的方法
     * @param resultCode (枚举类code,msg)
     * @return 返回失败传递枚举信息的方法
     * @param <T> 泛型
     */
    public static<T> Result<T> fail(ResultCode resultCode){
        return new Result<>(resultCode.getCode(),resultCode.getMsg(),null);
    }
    /**
     * 失败自定义错误码及消息的方法
     * @param code 自定义错误码
     * @param msg 自定义消息
     * @return 失败返回自定义消息的方法
     * @param <T> 泛型
     */
    public static<T> Result<T> fail(int code,String msg){
        return new Result<>(code,msg,null);
    }

}
