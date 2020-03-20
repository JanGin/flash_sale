package me.chan.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chan.annotation.NeedLogin;
import me.chan.annotation.TrafficLimit;
import me.chan.common.CodeMsg;
import me.chan.common.GlobalConstant;
import me.chan.common.Result;
import me.chan.common.UserContext;
import me.chan.domain.User;
import me.chan.service.StringRedisService;
import me.chan.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private StringRedisService strRedisService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //尝试获取并设置登陆用户, 以免之后的参数处理类获取失败
        User user = getUser(request, response);
        UserContext.setUser(user);

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            TrafficLimit tl = hm.getMethodAnnotation(TrafficLimit.class);
            if (tl != null) {
                Long userId = Long.MIN_VALUE;
                String key = request.getRequestURI();
                NeedLogin nl = hm.getMethodAnnotation(NeedLogin.class);
                if (nl != null && nl.value()) {
                    if (user != null) {
                        userId = user.getId();
                        key = key + "_" + userId;
                    }
                }

                int times = tl.accessTimes();
                int period = tl.withinSeconds();

                String val = strRedisService.get(key);
                if (StringUtils.isNotBlank(val)) {
                    int count = Integer.valueOf(val);
                    if (count > times) {     //超过访问上限
                        outputErrorMessage(response, CodeMsg.ACCESS_LIMIT_REACHED);
                        return false;
                    }
                    strRedisService.incr(key);
                } else {
                    strRedisService.set(key, "1", period, TimeUnit.SECONDS);
                }

            }
        }

        return true;
    }

    private void outputErrorMessage(HttpServletResponse response, CodeMsg cm) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            OutputStream out = response.getOutputStream();
            String data = JSONObject.toJSONString(Result.error(cm));
            out.write(data.getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String cookieToken = getCookieValue(request);
        String paramToken = request.getParameter(GlobalConstant.COOKIE_TOKEN_NAME);
        if (StringUtils.isBlank(cookieToken) && StringUtils.isBlank(paramToken))
            return null;

        String token = StringUtils.isBlank(cookieToken)?paramToken:cookieToken;
        return userService.getUserByToken(token, response);
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
