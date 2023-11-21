package cn.nolaurene.cms.common.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 校验密码
     */
    private String checkPassword;
}
