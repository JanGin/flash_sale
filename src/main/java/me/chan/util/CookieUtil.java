package me.chan.util;

import me.chan.common.GlobalConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, int maxAge, String val) {
        Cookie cookie = new Cookie(GlobalConstant.COOKIE_TOKEN_NAME, val);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
