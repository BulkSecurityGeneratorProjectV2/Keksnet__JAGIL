package de.neo.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks the default values of a method as deprecated.
 */
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface DeprecatedDefaults {

    boolean forChange() default false;
}
