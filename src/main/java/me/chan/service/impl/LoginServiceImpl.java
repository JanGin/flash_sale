package me.chan.service.impl;

import me.chan.common.CodeMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.dao.UserDao;
import me.chan.domain.User;
import me.chan.exception.GlobalException;
import me.chan.service.LoginService;
import me.chan.service.RedisService;
import me.chan.util.CookieUtil;
import me.chan.util.MD5Util;
import me.chan.util.UUIDUtil;
import me.chan.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

    @Value("${user.token.expiration}")
    private long expirationTime;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Transactional
    @Override
    public String doLogin(HttpServletResponse response, LoginVO login) {

        String mobile = login.getMobile();
        String pwd = login.getPassword();

        User user = userDao.getUserByMobile(mobile);
        if (Objects.isNull(user))
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

        String salt = user.getSalt();
        String password = MD5Util.formPwdToDBPwd(pwd, salt);
        if (!StringUtils.equals(password, user.getPassword()))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);

        if (StringUtils.equals(password, user.getPassword())) {
            Integer count = user.getLoginCount();
            user.setLoginCount(++count);
            userDao.saveUser(user);
        }

        //生成token写到cookie中
        String token = UUIDUtil.generate();
        String key = RedisKeyPrefix.USER_KEY + token;
        redisService.set(key, user, expirationTime, TimeUnit.SECONDS);
        CookieUtil.addCookie(response, (int) expirationTime, token);

        return token;
    }

}
