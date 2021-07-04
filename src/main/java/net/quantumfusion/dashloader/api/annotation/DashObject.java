package net.quantumfusion.dashloader.api.annotation;

import net.quantumfusion.dashloader.api.FactoryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DashObject {
    Class<?>[] value();

    FactoryType overrideType() default FactoryType.DEFAULT;
}
