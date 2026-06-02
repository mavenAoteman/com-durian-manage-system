package com.durian.manage.system.common.exception;

/**
 * 业务异常：携带 HTTP 状态码 + 业务 code + 用户可读 message。
 * Controller 直接抛出，由 GlobalExceptionHandler 统一转 JSON 响应。
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int status;
    private final int code;

    public ApiException(int status, int code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, 400, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(401, 401, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(403, 403, message);
    }

    public static ApiException tooManyRequests(String message) {
        return new ApiException(429, 429, message);
    }

    public static ApiException locked(String message) {
        return new ApiException(423, 423, message);
    }
}
