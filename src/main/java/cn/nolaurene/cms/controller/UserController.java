package cn.nolaurene.cms.controller;

import cn.nolaurene.cms.common.dto.LoginRequest;
import cn.nolaurene.cms.common.vo.User;
import cn.nolaurene.cms.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserLoginService userLoginService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public User userLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return userLoginService.login(loginRequest.getUserName(), loginRequest.getPassword(), request);
    }

}
