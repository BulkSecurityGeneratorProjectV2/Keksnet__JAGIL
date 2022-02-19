package de.neo.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks the method signature as deprecated and that it will be changed in the next major release.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeprecatedSignature {
}
