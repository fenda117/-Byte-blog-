package com.byteblog.exception;

import com.byteblog.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException{
    // 业务码
    private final int code;

    public  BusinessException(String msg){
        super(msg);
        this.code = ResultCode.INTERNAL_ERROR.getCode();
    }

    public BusinessException(ResultCode resultCode){
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }
    public BusinessException(ResultCode resultCode,String msg){
        super(msg);
        this.code = resultCode.getCode();
    }
}
