package cn.nolaurene.cms.exception;

import cn.nolaurene.cms.common.vo.BaseWebResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseWebResult<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return BaseWebResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseWebResult<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return BaseWebResult.fail(e.getMessage());
    }
}
