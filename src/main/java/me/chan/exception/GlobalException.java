package me.chan.exception;

import lombok.Getter;
import me.chan.common.CodeMsg;

@Getter
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.getMessage());
        this.codeMsg = codeMsg;
    }
}
