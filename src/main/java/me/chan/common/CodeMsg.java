package me.chan.common;

import lombok.Data;

/**
 * 错误信息描述
 */
@Data
public class CodeMsg {


    //通用的错误码
    public static CodeMsg SUCCESS = new CodeMsg(0, "操作成功");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "非法请求");
    public static CodeMsg ACCESS_LIMIT_REACHED= new CodeMsg(500104, "访问太频繁！");
    //登录模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "当前会话不存在，请重新登录");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号码不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "手机号码或密码输入错误");

    //秒杀模块
    public static CodeMsg DO_SALE_ERROR = new CodeMsg(500310, "下单失败，请返回重试");
    public static CodeMsg SALE_ACTIVITY_OVER = new CodeMsg(500311, "当前活动已结束");
    public static CodeMsg SALE_REPEAT_FORBIDDEN = new CodeMsg(500312, "请勿刷单");
    public static CodeMsg SALE_PRODUCT_NOT_EXIST = new CodeMsg(500313, "当前商品已下架或被移除");
    public static CodeMsg SALE_PRODUCT_SELLOUT = new CodeMsg(500315, "该商品卖完啦!");
    public static CodeMsg VERIFY_CODE_FAILED = new CodeMsg(500316, "验证码输入错误或已过期");


    //订单模块
    public static final CodeMsg ORDER_NOT_EXIST = new CodeMsg(500501, "订单不存在或者已失效");

    private CodeMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CodeMsg customizeMessage(Object... obj) {
        String message = String.format(CodeMsg.BIND_ERROR.message, obj);
        return new CodeMsg(this.code, message);
    }

    private int code;

    private String message;
}
