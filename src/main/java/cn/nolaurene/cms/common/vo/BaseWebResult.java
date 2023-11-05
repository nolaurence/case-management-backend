package cn.nolaurene.cms.common.vo;

import lombok.Data;

@Data
public class BaseWebResult<T> {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 数据
     */
    private T data;

    /**
     * 状态码
     */
    private String code;

    /**
     * 返回的消息
     */
    private String message;

    public BaseWebResult(Boolean success, T data, String code, String message) {
        this.success = success;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> BaseWebResult success(T data) {
        return new BaseWebResult(true, data, "", "success");
    }

    public static <T> BaseWebResult fail(T data, String code, String message) {
        return new BaseWebResult(false, data, code, message);
    }

    public static <T> BaseWebResult fail(String code, String message) {
        return new BaseWebResult(false, null, code, message);
    }

    public static <T> BaseWebResult fail(String message) {
        return new BaseWebResult(false, null, "", message);
    }
}
