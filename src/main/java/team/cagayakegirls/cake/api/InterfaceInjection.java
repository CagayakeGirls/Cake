package team.cagayakegirls.cake.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the interface injection behavior of the annotated Mixin/Interface.
 * Requires automatic mixins to be enabled in Cake.
 * If not specified on either the mixin or the interface, defaults to cake.enableAutoInterfaceInjections.
 * Annotating the interface with this overrides the Mixin's behavior.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface InterfaceInjection {
    boolean value() default true;
}
