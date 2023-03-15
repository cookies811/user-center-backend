package priv.cookies.usercenter.Service;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import priv.cookies.usercenter.Model.User;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("cookies");
        user.setUserAccount("cookies");
        user.setAvatarUrl("https://test.com");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setEmail("cookies@qq.com");
        user.setPhone("10086");

        boolean res = userService.save(user);

        Assertions.assertTrue(res);

    }

    @Test
    void userRegister() {
        String userAccount = "cook";
        String userPassword = "";
        String checkPassword = "123456";

        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);

        userAccount="1337518";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);

        userAccount = "cookies";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
    }

    @Test
    void testUserRegister() {
        String userAccount = "cook";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(6,result);


    }
}