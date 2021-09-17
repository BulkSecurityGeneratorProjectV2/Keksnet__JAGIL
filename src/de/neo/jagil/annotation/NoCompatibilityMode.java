package de.neo.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks that a method does not support CompatibilityMode.
 */
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoCompatibilityMode {
}
