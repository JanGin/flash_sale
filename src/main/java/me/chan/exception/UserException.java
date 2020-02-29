package me.chan.exception;

import lombok.Getter;
import me.chan.common.CodeMsg;

@Getter
public class UserException extends RuntimeException {

    private CodeMsg codeMsg;


    public UserException(CodeMsg codeMsg) {
        super(codeMsg.getMessage());
        this.codeMsg = codeMsg;
    }
}
