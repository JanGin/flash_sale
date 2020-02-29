package me.chan.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {


    public static boolean isMobileNumber(String number) {

        Pattern pattern = Pattern.compile("^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|16[0|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public static boolean isValidPwd(String password) {
        //至少6个字符，至少1个大写字母，1个小写字母和1个数字
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isFormInput(String input) {
        return !StringUtils.isEmpty(input);
    }
}
