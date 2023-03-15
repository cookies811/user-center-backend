package priv.cookies.usercenter.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import priv.cookies.usercenter.Model.Requset.UserLoginRequest;
import priv.cookies.usercenter.Model.Requset.UserRegisterRequest;
import priv.cookies.usercenter.Model.User;
import priv.cookies.usercenter.Service.UserService;
import priv.cookies.usercenter.common.BaseResponse;
import priv.cookies.usercenter.common.ErrorCode;
import priv.cookies.usercenter.common.ResultUtil;
import priv.cookies.usercenter.exception.BusinessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static priv.cookies.usercenter.constant.UserConstant.ADMIN_ROLE;
import static priv.cookies.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author cookies
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
    //public long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        //return result;
        return ResultUtil.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequestRequest, HttpServletRequest request) {
        if (userLoginRequestRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequestRequest.getUserAccount();
        String userPassword = userLoginRequestRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        int result = userService.userLogout(request);
        return ResultUtil.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) throw new BusinessException(ErrorCode.NOT_LOGIN);
        long id = currentUser.getId();
        
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtil.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);

        List<User> users = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(users);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtil.success(b);
    }

    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
