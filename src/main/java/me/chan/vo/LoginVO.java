package me.chan.vo;

import lombok.Data;
import me.chan.annotation.constant.FormInputConstant;
import me.chan.annotation.validate.FormInput;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVO {

    @NotNull
    @FormInput(message = "请检查手机号码输入是否正确", type = FormInputConstant.INPUT_MOBILENUMBER)
    private String mobile;

    @NotNull
    @Length(max=32)
    private String password;
}
