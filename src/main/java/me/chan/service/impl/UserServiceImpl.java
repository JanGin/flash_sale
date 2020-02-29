package me.chan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.chan.common.CodeMsg;
import me.chan.common.RedisKeyPrefix;
import me.chan.dao.UserDao;
import me.chan.domain.User;
import me.chan.exception.UserException;
import me.chan.service.RedisService;
import me.chan.service.UserService;
import me.chan.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    @Value("${user.token.expiration}")
    private long expirationTime;

    @Override
    public User getUserById(Integer id) {
        return userDao.getById(id);
    }

    @Transactional
    @Override
    public void testTx(User user) {
        userDao.addUser(user);
        User user1 = userDao.getById(1);
        if (user1 != null) {
            User user2 = new User();
            user2.setId(user1.getId());
            user2.setMobile(user1.getMobile());
            userDao.addUser(user2);
        }
    }

    @Override
    public User getUserByToken(String token, HttpServletResponse response) {
        String json = redisService.get(RedisKeyPrefix.USER_KEY+token).toString();
        User user = JSONObject.parseObject(json, User.class);
        if (null == user)
            throw new UserException(CodeMsg.SESSION_ERROR);

        //延长token的有效期
        redisService.set(RedisKeyPrefix.USER_KEY+token, user, expirationTime, TimeUnit.SECONDS);
        CookieUtil.addCookie(response, (int) expirationTime, token);
        return user;
    }
}