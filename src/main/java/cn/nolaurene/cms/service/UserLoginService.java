package cn.nolaurene.cms.service;

import cn.nolaurene.cms.common.constants.UserConstants;
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

    private static String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Resource
    UserMapper userMapper;

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
