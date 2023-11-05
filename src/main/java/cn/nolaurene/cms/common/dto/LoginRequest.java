package cn.nolaurene.cms.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequest {

    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

}
