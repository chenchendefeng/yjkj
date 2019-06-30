package com.jiayi.platform.common.logger;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
public class TimeLogAspect {

    public static final Logger log = LoggerFactory.getLogger(TimeLogAspect.class);

    @Around(value = "@within(com.jiayi.platform.common.logger.TimeLog) || @annotation(com.jiayi.platform.common.logger.TimeLog)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        TimeLog timeLog = method.getAnnotation(TimeLog.class);
        if (timeLog == null) {
            timeLog = (TimeLog) signature.getDeclaringType().getAnnotation(TimeLog.class);
        }

        Object[] args = proceedingJoinPoint.getArgs();
        Object result = proceedingJoinPoint.proceed(args);
        Signature sig = proceedingJoinPoint.getSignature();
        if (!(sig instanceof MethodSignature)) {
            log.error("signature error: {}", sig);
        }
        MethodSignature msig = (MethodSignature) sig;
        Object target = proceedingJoinPoint.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        long time = System.currentTimeMillis() - start;
        String argsString = "";
        if (timeLog.printArgs()) {
            argsString = StringUtils.join(args, ",");
        }
        if (time > timeLog.threshold()) {

            log.warn("{} {}ms, threshold {}ms, {}.{}({})",
                    StringUtils.joinWith(" ", timeLog.value(), "cost").trim(),
                    time, timeLog.threshold(),
                    target.getClass().getName(), currentMethod.getName(), argsString);
        } else {
            if (timeLog.debugOnly()) {
                log.debug("{} {}ms, {}.{}({})", StringUtils.joinWith(" ", timeLog.value(), "cost").trim(),
                        time, target.getClass().getName(), currentMethod.getName(), argsString);
            } else {
                log.info("{} {}ms, {}.{}({})", StringUtils.joinWith(" ", timeLog.value(), "cost").trim(),
                        time, target.getClass().getName(), currentMethod.getName(), argsString);
            }
        }
        return result;
    }
}
