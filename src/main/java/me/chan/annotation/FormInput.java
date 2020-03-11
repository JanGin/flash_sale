package me.chan.annotation;

import me.chan.validate.FormInputValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,  ElementType.ANNOTATION_TYPE})
@NotNull
@Constraint(
        validatedBy = {FormInputValidator.class}
)
public @interface FormInput {

    String message() default "表单项内容输入有误";

    String type() default FormInputType.FORM_INPUT;

    boolean required() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
}
