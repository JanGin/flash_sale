package me.chan.common;

import lombok.Data;

@Data
public class Result<T> {

    private int retCode = 0;

    private String message = "操作成功";

    private T data;

    private Result(int retCode, String message) {
        this.retCode = retCode;
        this.message = message;
    }

    private Result(T data) {
        this.data = data;
    }


    public static Result error(CodeMsg codeMsg) {
        return new Result(codeMsg.getCode(), codeMsg.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return new Result(data);
    }
}
