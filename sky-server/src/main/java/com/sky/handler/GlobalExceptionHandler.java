package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'ccxzk' for key 'employee.idx_username'

        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            //获取账号信息
            String[] split = message.split(" ");
            String username = split[2];

            // 拼接错误信息
            String msg= "账号[" + username + "]" + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error("发生" + MessageConstant.UNKNOWN_ERROR);
        }
    }

}
