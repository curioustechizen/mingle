package mingle.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MingleFragment {
    Class<?> base();
    Class<?>[] mixins() default {};
    String name() default "";
}
