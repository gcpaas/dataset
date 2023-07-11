package com.gccloud.common.permission;

import com.gccloud.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 接口权限校验切面
 * @author hongyang
 * @version 1.0
 * @date 2023/5/15 10:51
 */
@Slf4j
@Aspect
@Component("apiPermissionAspect")
public class ApiPermissionAspect {
    @Resource
    private ApiPermissionClient permissionClient;

    @Before("@annotation(apiPermission) || @within(apiPermission)")
    public void doBefore(JoinPoint joinPoint, ApiPermission apiPermission) {
        if (!permissionClient.hasPermissionService()) {
            // 没有权限接口的实现类，不进行权限校验
            return;
        }
        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 判断方法所属的类是否标记了该注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        ApiPermission classAnnotation = targetClass.getAnnotation(ApiPermission.class);
        boolean classRequired = (classAnnotation != null && classAnnotation.required());
        // 判断方法是否标记了该注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiPermission methodAnnotation = method.getAnnotation(ApiPermission.class);
        boolean methodRequired = (methodAnnotation != null && methodAnnotation.required());
        // 判断是否需要登录权限，就近原则，方法上有优先方法上的，方法上没有则使用类上的
        boolean required = methodRequired || classRequired;
        if (required) {
            // 获取权限
            String[] permissions = methodAnnotation != null ? methodAnnotation.permissions() : classAnnotation.permissions();
            // 校验权限
            boolean verify = permissionClient.verifyApiPermission(request, permissions);
            if (!verify) {
                throw new GlobalException("权限不足");
            }
        }
    }

    @AfterThrowing(pointcut = "@annotation(apiPermission) || @within(apiPermission)")
    public void doAfterThrowing(JoinPoint joinPoint, ApiPermission apiPermission) {
        // 记录日志等操作
    }
}
