package net.oskarstrom.dashloader.api.annotation;

import net.oskarstrom.dashloader.api.enums.ConstructorMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Constructors are automatic since 2.1
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@Deprecated(since = "2.1", forRemoval = true)
public @interface DashConstructor {
    ConstructorMode value();
}
