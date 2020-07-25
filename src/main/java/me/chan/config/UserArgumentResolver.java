package me.chan.config;

import me.chan.common.GlobalConstant;
import me.chan.common.UserContext;
import me.chan.domain.User;
import me.chan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class UserArgumentResolver implements HandlerMethodArgumentResolver {


    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();

        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {

        /*HttpServletRequest request = nativeWebURequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String cookieToken = getCookieValue(request);
        String paramToken = request.getParameter(GlobalConstant.COOKIE_TOKEN_NAME);
        if (StringUtils.isBlank(cookieToken) && StringUtils.isBlank(paramToken))
            return null;

        String token = StringUtils.isBlank(cookieToken)?paramToken:cookieToken;
        return userService.getUserByToken(token, response);*/
        User user = UserContext.get();
        UserContext.remove();       //避免内存泄漏
        return user;
    }

    private String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if (GlobalConstant.COOKIE_TOKEN_NAME.equals(cookie.getName()))
                    return cookie.getValue();
            }
        }
        return null;
    }
}
