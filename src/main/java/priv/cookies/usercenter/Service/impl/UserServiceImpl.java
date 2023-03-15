package priv.cookies.usercenter.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import priv.cookies.usercenter.Model.User;
import priv.cookies.usercenter.Service.UserService;
import priv.cookies.usercenter.Mapper.UserMapper;
import org.springframework.stereotype.Service;
import priv.cookies.usercenter.common.ErrorCode;
import priv.cookies.usercenter.exception.BusinessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static priv.cookies.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 饼干
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-02-03 21:10:35
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值
     */
    private static final String SALT = "COOK";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.1 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        if (userAccount.length() < 4)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        if (userPassword.length() < 8 || checkPassword.length() < 8)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能小于八位");
        //1.2 账号不能包括特殊字符
        String validPattern = "[`~!@#$%^&()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包括特殊字符");

        //1.3 密码和校验密码需要一致
        if (!userPassword.equals(checkPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");

        //1.4 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能重复");
            //return -1;
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"保存失败");

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码为空");
        if (userAccount.length() < 4)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        if (userPassword.length() < 8)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");

        //账号不能包括特殊字符
        String validPattern = "[`~!@#$%^&()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword!");
            throw new BusinessException(ErrorCode.NULL_ERROR,"账号不存在");
        }

        //用户脱敏
        User safetyUser = getSafetyUser(user);

        //记录用户登录状态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
        
    }

    /**
     * 用户脱敏
     * @param user 需要脱敏的用户
     * @return 脱敏后用户信息
     */
    @Override
    public User getSafetyUser(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"账号不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setCreateTime(user.getCreateTime());

        return safetyUser;
    }

    /**
     * 用户退出登录
     * @param httpServletRequest 获取当前登录信息
     * @return 成功返回1
     */
    @Override
    public int userLogout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




