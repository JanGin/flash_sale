package me.chan.service;

import me.chan.vo.LoginVO;

import javax.servlet.http.HttpServletResponse;

public interface LoginService {

    String doLogin(HttpServletResponse response, LoginVO login);
}
