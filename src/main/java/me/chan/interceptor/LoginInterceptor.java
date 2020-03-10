package me.chan.interceptor;

import me.chan.annotation.NeedLogin;
import me.chan.common.UserContext;
import me.chan.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            NeedLogin needLogin = hm.getMethodAnnotation(NeedLogin.class);
            if (needLogin != null && needLogin.need()) {
                User user = UserContext.get();
                if (null == user) {
                    response.sendRedirect("/login");
                    return false;
                }
            }
        }

        return true;
    }
}
