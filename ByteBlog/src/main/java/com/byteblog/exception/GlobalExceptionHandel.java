package com.byteblog.exception;

import com.byteblog.common.Result;
import com.byteblog.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandel {
    /**
     * 业务异常，直接使用异常里的Code+message 返回
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler
    public Result<Void> handlerBusinessException(BusinessException e, HttpServletRequest request){
        log.warn("【业务异常】: uri={},code={},message={}",request.getRequestURI(),e.getCode(),e.getMessage());// 警告日志
        return Result.fail(e.getCode(),e.getMessage());
    }

    /**
     * RequestBody 上的参数校验失败(如 @NotBlak触发)
     * 把多个字符串错误拼成一条字符串返回
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handlerMethodArgumentNotValid(MethodArgumentNotValidException e){
        String msg=e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        log.warn("【参数校验失败】{}",msg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(),msg);
    }

    /**
     * 请求方式不支持异常(用get请求调用POST接口)
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handlerMethodNotSupported(HttpRequestMethodNotSupportedException e){
        return Result.fail(ResultCode.BAD_REQUEST.getCode(),"请求方式不支持"+e.getMethod());
    }

    /**
     * 请求路径不存在
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handlerNoHandlerFound(NoHandlerFoundException e){
        return Result.fail(ResultCode.NOT_FOUND.getCode(),"接口不存在"+e.getRequestURL());
    }

    /**
     * 兜底，系统级异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handlerException(Exception e,HttpServletRequest request){
        log.error("【系统异常】 uri={}",request.getRequestURI(),e);
        return Result.fail(ResultCode.INTERNAL_ERROR);
    }
}
