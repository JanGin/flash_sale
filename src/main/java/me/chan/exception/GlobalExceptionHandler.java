package me.chan.exception;

import lombok.extern.slf4j.Slf4j;
import me.chan.common.CodeMsg;
import me.chan.common.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public Result<String> handleException(Exception e) {

        e.printStackTrace();
        log.error("error:{}", e.getMessage());

        if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.customizeMessage(msg));
        }

        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        }

        return Result.error(CodeMsg.SERVER_ERROR);
    }
}
