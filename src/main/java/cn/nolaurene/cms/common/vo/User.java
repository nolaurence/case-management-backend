package cn.nolaurene.cms.common.vo;

import lombok.Data;

@Data
public class User {

    public String userName;

    private String userAccount;

    private String avatarUrl;

    private Integer gender;

//    private String password;

    private String phone;

    private String email;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;

    private Integer userRole;
}
