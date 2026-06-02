package com.durian.manage.system.web.exception;

import com.durian.manage.system.common.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理：把 ApiException / 参数校验异常 / 其他异常统一转成 JSON。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException e) {
        log.info("ApiException: status={} code={} msg={}", e.getStatus(), e.getCode(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(buildBody(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String msg = fe != null ? fe.getDefaultMessage() : "参数错误";
        return ResponseEntity.status(400).body(buildBody(400, msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("参数错误");
        return ResponseEntity.status(400).body(buildBody(400, msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(buildBody(400, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(500).body(buildBody(500, "系统异常，请稍后再试"));
    }

    private Map<String, Object> buildBody(int code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("message", message);
        return body;
    }
}
