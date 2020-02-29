package me.chan.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class MD5Util {

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "6c8d2a7e";

    public static String inputPwdToFormPwd(String inputPwd) {
        String str = "" + salt.charAt(0)+salt.charAt(2) + inputPwd +salt.charAt(4) + salt.charAt(3);
        String result = md5(str);
        if (log.isDebugEnabled())
            log.debug("[inputPwdToFormPwd]--form password is {}", result);

        return result;
    }

    public static String formPwdToDBPwd(String formPwd, String salt) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPwd +salt.charAt(4) + salt.charAt(3);
        return md5(str);
    }

    public static String inputPwdToDbPwd(String inputPwd, String saltDB) {
        String formPwd = inputPwdToFormPwd(inputPwd);
        String dbPwd = formPwdToDBPwd(formPwd, saltDB);
        return dbPwd;
    }


    public static void main(String[] args) {
        // 3cf206a72c2019aeeefb1a09568b2410
        System.out.println(inputPwdToDbPwd("123456a.","6c8d2a7e"));
    }
}
