package de.neo.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks a method, package, constructor or annotation as {@link Internal}.
 * The marked objects should not be used.
 */
@Documented
@Target({ElementType.METHOD, ElementType.PACKAGE, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Internal {

    /**
     * Is it planned to change the visibility of the object?
     *
     * @return is it planned to change the visibility of the object?
     */
    boolean forVisibilityChange() default false;

}
