package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     * @param joinPoint
     */
    @Before("autoFillPointCut()")
    void autoFill(JoinPoint joinPoint){
        //获取当前方法数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        //获取当前方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object object = args[0];

        //创建赋值的数据
        LocalDateTime localDateTime = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据参数动态获取赋值方法
        if(operationType == OperationType.INSERT){
            try {
                Method setUpdateTime = object.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = object.getClass().getMethod("setUpdateUser", Long.class);
                Method setCreateTime = object.getClass().getMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser = object.getClass().getMethod("setCreateUser", Long.class);

                setUpdateTime.invoke(object, localDateTime);
                setUpdateUser.invoke(object, currentId);
                setCreateTime.invoke(object, localDateTime);
                setCreateUser.invoke(object, currentId);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = object.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = object.getClass().getMethod("setUpdateUser", Long.class);

                setUpdateTime.invoke(object, localDateTime);
                setUpdateUser.invoke(object, currentId);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }




}
