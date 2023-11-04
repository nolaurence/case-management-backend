package cn.nolaurene.cms.dal.entity;

import io.mybatis.provider.Entity.Table;
import io.mybatis.provider.Entity.Column;
import lombok.Data;

import java.util.Date;

/**
 * Description:用户
 * @author 郭富康
 * Date 2023-11-04
 */
@Data
@Table("user")
public class UserDO {

    /**
     * 用户昵称
     */
    @Column("user_name")
    private String userName;

    /**
     * id
     */
    @Column(id = true, remark = "主键", updatable = false, insertable = false)
    private Long id;

    /**
     * 账号
     */
    @Column("user_account")
    private String userAccount;

    /**
     * 用户头像
     */
    @Column("avatar_url")
    private String avatarUrl;

    /**
     * 性别
     */
    @Column("gender")
    private Byte gender;

    /**
     * 密码
     */
    @Column("user_password")
    private String userPassword;

    /**
     * 电话
     */
    @Column("phone")
    private String phone;

    /**
     * 邮箱
     */
    @Column("email")
    private String email;

    /**
     * 状态 0 - 正常
     */
    @Column("user_status")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @Column("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @Column("is_delete")
    private Byte isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    @Column("user_role")
    private Integer userRole;

    /**
     * 星球编号
     */
    @Column("planet_code")
    private String planetCode;
}