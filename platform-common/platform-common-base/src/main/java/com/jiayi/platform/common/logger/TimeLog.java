package com.jiayi.platform.common.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : weichengke
 * @date : 2019-03-05 16:25
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeLog {
    String value() default "";

    boolean printArgs() default true;

    int threshold() default 500; //告警的阈值，操作这个值，log基本将以warn打出来

    boolean debugOnly() default true;
}
