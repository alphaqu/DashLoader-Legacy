package net.oskarstrom.dashloader.api.annotation;

import net.oskarstrom.dashloader.api.enums.DashDataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DashObject {
    Class<?> value() default NullPointerException.class;


    DashDataType overrideType() default DashDataType.DEFAULT;
}
