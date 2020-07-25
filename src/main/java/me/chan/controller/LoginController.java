package me.chan.controller;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.CodeMsg;
import me.chan.common.Result;
import me.chan.exception.GlobalException;
import me.chan.service.LoginService;
import me.chan.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {


    @Autowired
    private LoginService loginService;

    @GetMapping()
    public String login() {
        return "login";
    }


    @PostMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVO login) {

        if (null == login) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String result = loginService.doLogin(response, login);
        log.info("the login result is {}", result);
        return Result.success(result);
    }
}
