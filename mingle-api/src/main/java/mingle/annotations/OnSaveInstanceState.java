package mingle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mingle.Mingle;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OnSaveInstanceState {
    int order() default Mingle.ORDER_DEFAULT;
}
