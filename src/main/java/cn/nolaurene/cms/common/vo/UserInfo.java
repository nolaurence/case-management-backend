package cn.nolaurene.cms.common.vo;

import lombok.Data;

import java.util.List;

/**
 * 和ant design pro 前端的CurrentUser保持一致
 */
@Data
public class UserInfo {

    private String name;

    private String avatar;

    private String userid;

    private String email;

    private String signature;

    private String title;

    private String group;

    private List<KeyLabel> tags;

    private Integer notifyCount;

    private Integer unreadCount;

    private String country;

    private String access;

    private String address;

    private String phone;
}
