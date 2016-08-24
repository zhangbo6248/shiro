package realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;

/**
 * @title 自定义Realm1实现
 * @author  edgar
 * @date 2016-8-23 13:50:48
 */
public class MyRealm1 implements Realm {

    //返回一个唯一的 Realm 名字
    public String getName() {
        return "myrealm1";
    }

    //判断此 Realm 是否支持此 Token
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken; //仅支持UsernamePasswordToken类型的Token
    }

	/**
     * 认证(根据 Token 获取认证信息)
     * @param token
     * @return
     * @throws AuthenticationException
     */
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();  //得到用户名
        String password = new String((char[])token.getCredentials()); //得到密码
        if(!"zhang".equals(username)) {
            throw new UnknownAccountException(); //如果用户名错误
        }
        if(!"123".equals(password)) {
            throw new IncorrectCredentialsException(); //如果密码错误
        }

        System.out.println(username+password);
        
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
