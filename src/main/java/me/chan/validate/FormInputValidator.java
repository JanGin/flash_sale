package me.chan.validate;

import me.chan.annotation.FormInput;
import me.chan.annotation.FormInputType;
import me.chan.util.ValidationUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FormInputValidator implements ConstraintValidator<FormInput, String> {

    private boolean required = false;

    private String inputType;

    @Override
    public void initialize(FormInput constraintAnnotation) {
        required = constraintAnnotation.required();
        inputType = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (required) {
            if (FormInputType.INPUT_MOBILENUMBER.equals(inputType)) {
                return ValidationUtil.isMobileNumber(s);
            } else if (FormInputType.INPUT_PASSWORD.equals(inputType)) {
                return ValidationUtil.isValidPwd(s);
            } else {
                return ValidationUtil.isFormInput(s);
            }
        }
        return true;
    }
}
