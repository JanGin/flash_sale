package me.chan.service;

import me.chan.domain.User;

import javax.servlet.http.HttpServletResponse;

public interface UserService {

    User getUserById(Integer id);

    void testTx(User user);

    User getUserByToken(String token, HttpServletResponse response);
}
