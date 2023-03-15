package priv.cookies.usercenter.Service;

import org.springframework.http.HttpRequest;
import priv.cookies.usercenter.Model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 饼干
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-02-03 21:10:35
*/
public interface UserService extends IService<User> {


    /**
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     *
     * @param userAccount        用户账号
     * @param userPassword       用户密码
     * @param httpServletRequest 前端发送的请求
     * @return                   用户脱敏信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 用户信息脱敏
     * @param user 待脱敏用户
     * @return 脱敏用户
     */
    User getSafetyUser(User user);

    /**
     * 用户退出登录
     * @param httpServletRequest 获取当前登录信息
     * @return 成功返回1
     */
    int userLogout(HttpServletRequest httpServletRequest);
}
