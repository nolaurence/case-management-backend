package cn.nolaurene.cms.service;

import cn.nolaurene.cms.common.constants.UserConstants;
import cn.nolaurene.cms.common.enums.ErrorCode;
import cn.nolaurene.cms.common.enums.LoginErrorEnum;
import cn.nolaurene.cms.common.vo.User;
import cn.nolaurene.cms.dal.entity.UserDO;
import cn.nolaurene.cms.dal.mapper.UserMapper;
import cn.nolaurene.cms.exception.BusinessException;
import io.mybatis.mapper.example.Example;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserLoginService {

    private static final String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Resource
    UserMapper userMapper;

    public long register(String username, String password, String checkPassword) {
        // 1. 参数校验
        if (StringUtils.isAnyBlank(username, password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "参数为空");
        }
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "用户账号果断");
        }
        if (password.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "用户密码过短");
        }

        // 不能包含特殊字符
        Matcher matcher = Pattern.compile(validPattern).matcher(username);
        if (matcher.find()) {
            return -1;
        }

        // 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            return -1;
        }

        // 账户不能重复
        UserDO userDO = new UserDO();
        userDO.setUserAccount(username);
        Optional<UserDO> userDO1 = userMapper.selectOne(userDO);
        if (userDO1.isPresent()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "行号重复");
        }

        // 2. 加密
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 3. 写数据库
        userDO.setUserAccount(username);
        userDO.setUserPassword(encryptedPassword);

        int i = userMapper.insertSelective(userDO);
        if (i <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR.getCode(), "注册失败");
        }
        return userDO.getId();
    }

    public User login(String userAccount, String password, HttpServletRequest httpServletRequest) {
        // 1. 校验参数
        LoginErrorEnum checkResult = checkAccount(userAccount);
        if(checkResult != LoginErrorEnum.SUCCESS) {
            throw new BusinessException(checkResult.getErrorCode(), checkResult.getErrorMessage());
        }
        if (password.length() < UserConstants.MIN_PASSWORD_LENGTH) {
            throw new BusinessException(LoginErrorEnum.PASSWORD_TOO_SHORT.getErrorCode(), LoginErrorEnum.PASSWORD_TOO_SHORT.getErrorMessage());
        }

        // 2. 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 查询用户是否存在
        UserDO userDO = new UserDO();
        userDO.setUserAccount(userAccount);
        userDO.setUserPassword(encryptedPassword);
        userDO.setIsDelete(false);

        Optional<UserDO> userDO1 = userMapper.selectOne(userDO);
        if (userDO1.isEmpty()) {
            throw new BusinessException(LoginErrorEnum.USER_NOT_EXIST.getErrorCode(), LoginErrorEnum.USER_NOT_EXIST.getErrorMessage());
        }

        // 3. 用户脱敏：模型转换
        User userInfoToReturn = getSafetyUser(userDO1.get());

        // 种cookie
        httpServletRequest.getSession().setAttribute(UserConstants.USER_LOGIN_STATE, userInfoToReturn);
        return userInfoToReturn;
    }

    public int logout(HttpServletRequest httpServletRequest) {
        // 移除登录态
        httpServletRequest.getSession().removeAttribute(UserConstants.USER_LOGIN_STATE);
        return 0;
    }

    private LoginErrorEnum checkAccount(String userAccount) {
        if (StringUtils.isBlank(userAccount)) {
            return LoginErrorEnum.ACCOUNT_EMPTY;
        }
        if (userAccount.length() < UserConstants.MIN_USER_ACCOUNT_LENGTH) {
            return LoginErrorEnum.ACCOUNT_TOO_SHORT;
        }
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return LoginErrorEnum.ACCOUNT_BAD_CHARACTER;
        }
        return LoginErrorEnum.SUCCESS;
    }

    private User getSafetyUser(UserDO userDO) {
        User user = new User();
        user.setUserName(userDO.getUserName());
        user.setAvatarUrl(userDO.getAvatarUrl());
        user.setGender(userDO.getGender());
        user.setPhone(userDO.getPhone());
        user.setEmail(userDO.getEmail());
//        user.setUserId(userDO.getId());
        user.setUserAccount(userDO.getUserAccount());
        user.setUserRole(userDO.getUserRole());
//        user.setPlanetCode(userDO.getPlanetCode());
        return user;
    }

}
