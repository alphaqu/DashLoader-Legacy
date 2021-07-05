package net.oskarstrom.dashloader.api.annotation;

import net.oskarstrom.dashloader.api.enums.ConstructorMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface DashConstructor {

    ConstructorMode value();
}
