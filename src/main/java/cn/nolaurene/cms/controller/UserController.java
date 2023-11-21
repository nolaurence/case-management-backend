package cn.nolaurene.cms.controller;

import cn.nolaurene.cms.common.dto.LoginRequest;
import cn.nolaurene.cms.common.dto.RegisterRequest;
import cn.nolaurene.cms.common.enums.ErrorCode;
import cn.nolaurene.cms.common.vo.BaseWebResult;
import cn.nolaurene.cms.common.vo.User;
import cn.nolaurene.cms.exception.BusinessException;
import cn.nolaurene.cms.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static cn.nolaurene.cms.common.constants.UserConstants.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserLoginService userLoginService;

    @PostMapping("/register")
    public BaseWebResult<Long> userRegister(@RequestBody RegisterRequest registerRequest) {
        // 校验
        if (null == registerRequest || StringUtils.isAnyBlank(
                registerRequest.getUserName(),
                registerRequest.getPassword(),
                registerRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMessage());
        }
        long userId = userLoginService.register(
                registerRequest.getUserName(),
                registerRequest.getPassword(),
                registerRequest.getCheckPassword());

        return BaseWebResult.success(userId);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public User userLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userLoginService.login(loginRequest.getUserName(), loginRequest.getPassword(), request);
    }

    @GetMapping("/logout")
    public BaseWebResult userLogout(HttpServletRequest request) {
        if (null == request) {
            throw new BusinessException(ErrorCode.NOT_LOGIN.getCode(), ErrorCode.NOT_LOGIN.getMessage());
        }
        int result = userLoginService.logout(request);
        return BaseWebResult.success(result);
    }

//    /**
//     * 获取当前用户
//     */
//    @GetMapping("/current")
//    public BaseWebResult<User> getCurrentUser(HttpServletRequest request) {
//        User currentUserInfo = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
//
//        if (null == currentUserInfo) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN.getCode(), ErrorCode.NOT_LOGIN.getMessage());
//        }
//        long userId = currentUserInfo.getUserAccount();
//    }

}
