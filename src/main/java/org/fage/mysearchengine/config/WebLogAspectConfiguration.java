package org.fage.mysearchengine.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Caizhfy
 * @email caizhfy@163.com
 * @createTime 2017年12月6日
 * @description web常规访问切面拦截日志记录
 */
@Aspect
@Component
public class WebLogAspectConfiguration {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut(value = "execution(public * org.fage.mysearchengine.*.controller..*.*(..)) && " +
            "!execution(public * org.fage.mysearchengine.web.controller.SearchController.get5ItemsList(..))")
    public void webLog() {
    }

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 常规web请求日志
     * @param joinPoint
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("客户机IP地址————> " + request.getRemoteAddr());
        logger.info("客户机请求URL————> " + request.getRequestURL().toString());
//        logger.info("客户机请求HTTP方法————> " + request.getMethod());
        logger.info("客户机请求方法————> " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("客户机请求参数————> " + Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 常规web响应日志
     * @param ret
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
//        logger.info("服务器返回内容————> " + ret);
        logger.info("服务器处理花费时间————> " + (System.currentTimeMillis() - startTime.get()) + "ms");
    }
}