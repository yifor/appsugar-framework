package org.appsugar.framework.serializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassMapping {
    /**
     * 对应class的alias名称,使序列化体积减少不需要写类名
     */
    int value();
}
