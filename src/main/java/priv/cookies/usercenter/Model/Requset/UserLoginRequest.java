package priv.cookies.usercenter.Model.Requset;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author cookies
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -470538076355967686L;

    private String userAccount;

    private String userPassword;

}
