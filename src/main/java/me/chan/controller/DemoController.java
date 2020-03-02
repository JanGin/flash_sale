package me.chan.controller;

import com.alibaba.fastjson.JSONObject;
import me.chan.common.CodeMsg;
import me.chan.common.Result;
import me.chan.domain.User;
import me.chan.mq.MessageSender;
import me.chan.service.RedisService;
import me.chan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageSender sender;

    @RequestMapping("/test")
    public String test() {
        return "demo is running";
    }

    @GetMapping("/user/{id}")
    public Result testQuery(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping("/user")
    public Result testTransaction(User user) {
       try {
           userService.testTx(user);
       } catch (Exception e) {
            return Result.error(CodeMsg.SERVER_ERROR);
       }
       return Result.success(user);
    }

    @PostMapping("/redisSet")
    public Result testSet2Redis(@RequestBody User user) {

        String userJSON = JSONObject.toJSONString(user);
        redisService.set("user", userJSON, 5, TimeUnit.MINUTES);
        return Result.success(CodeMsg.SUCCESS);
    }

    @GetMapping("/redisGet")
    public Result testGetFromRedis(@RequestParam("key") String key) {
        String obj = (String)redisService.get(key);
        User data = JSONObject.parseObject(obj, User.class);
        return Result.success(data);
    }

    @RequestMapping("/mq")
    public Result testMQ() {
        sender.send("hello world");
        return Result.success(CodeMsg.SUCCESS);
    }
}
