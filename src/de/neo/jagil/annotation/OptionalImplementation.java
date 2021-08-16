package de.neo.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks a possible but optional implementation of a method.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalImplementation {
}
