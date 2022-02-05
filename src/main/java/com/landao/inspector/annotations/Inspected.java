package com.landao.inspector.annotations;

import com.landao.inspector.model.enums.TrimType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Inspected {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    /**
     * 对于数字,表示最小值(包含)
     * 对于字符串,表示枝减后的最小长度(包含)
     * @apiNote 对于数字，如果想包含0，就保持默认，必须是非负数，min设置为1，可以是负数，设置为Long.MIN_VALUE
     */
    long min() default 0;

    /**
     * 对于数字,表示最大值(包含)
     * 对于字符串,表示枝减后的最大长度(包含)
     * @apiNote 字符串必须指定非最大值的长度,建议为数据库保存的最大长度,否则会产生系统错误
     */
    long max() default Long.MAX_VALUE;

    /**
     * 字符串的枝剪类型
     */
    TrimType trimType() default TrimType.All;

}
