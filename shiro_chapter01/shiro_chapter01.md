###  shiro权限框架 --- 身份认证 
@date 2016-8-23 11:19:52 @author edgar 【edgar_zhang2014@163.com】


                                第一章 身份验证
    身份验证，即在应用中谁能证明他就是他本人。一般提供如他们的身份 ID 一些标识信息来
    表明他就是他本人，如提供身份证，用户名/密码来证明。
    在 shiro 中，用户需要提供 principals （身份）和 credentials（证明）给 shiro，从而应用能
    验证用户身份：
    principals：身份，即主体的标识属性，可以是任何东西，如用户名、邮箱等，唯一即可。
    一个主体可以有多个 principals， 但只有一个 Primary principals， 一般是用户名/密码/手机号。
    credentials：证明/凭证，即只有主体知道的安全值，如密码/数字证书等。
    最常见的 principals 和 credentials 组合就是用户名/密码了。 接下来先进行一个基本的身份认
    证。
    另外两个相关的概念是之前提到的 Subject 及 Realm，分别是主体及验证主体的数据源。
    
    
    身份验证的步骤：
    1、收集用户身份/凭证，即如用户名/密码；
    2、调用 Subject.login 进行登录，如果失败将得到相应的 AuthenticationException 异常，根
       据异常提示用户错误信息；否则登录成功；
    3、最后调用 Subject.logout 进行退出操作。
    
    测试总结问题点：
    1、用户名/密码硬编码在 ini 配置文件，以后需要改成如数据库存储，且密码需要加密存储；
    2、用户身份 Token 可能不仅仅是用户名/密码，也可能还有其他的，如登录时允许用户名/
    邮箱/手机号同时登录。
    
    
    身份认证流程：
        流程如下：
        1、首先调用 Subject.login(token)进行登录，其会自动委托给 Security Manager，
        调用之前必须通过 SecurityUtils. setSecurityManager()设置；
             //获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
            Factory<org.apache.shiro.mgt.SecurityManager> factory =
                    new IniSecurityManagerFactory("classpath:shiro-jdbc-realm.ini");
            //得到SecurityManager实例 并绑定给SecurityUtils
            org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
            SecurityUtils.setSecurityManager(securityManager);
        
        2、SecurityManager 负责真正的身份验证逻辑；它会委托给 Authenticator 进行身份验证；
        3、Authenticator 才是真正的身份验证者，Shiro API 中核心的身份认证入口点，此处可以自
            定义插入自己的实现；
        4、Authenticator 可能会委托给相应的 AuthenticationStrategy（认证策略）进行多Realm身份验证，
            默认ModularRealmAuthenticator 会调用 AuthenticationStrategy 进行多 Realm 身份验证；
        5、Authenticator 会把相应的 token 传入 Realm，从 Realm 获取身份验证信息，如果没有返
            回/抛出异常表示身份验证失败了。此处可以配置多个 Realm，将按照相应的顺序及策略进行访问。
    
    
    Realm：域， Shiro 从从 Realm 获取安全数据（如用户、角色、权限），就是说 SecurityManager
    要验证用户身份， 那么它需要从 Realm 获取相应的用户进行比较以确定用户身份是否合法；
    也需要从 Realm 得到用户相应的角色/权限进行验证用户是否能进行操作； 可以把 Realm 看
    成 DataSource ， 即 安 全 数 据 源 。 如 我 们 之 前 的 ini 配 置 方 式 将 使 用
    org.apache.shiro.realm.text.IniRealm。
    
    Shiro 默认提供的 Realm：
        以后一般继承 AuthorizingRealm （授权）即可； 其继承了 AuthenticatingRealm （即身份验证） ，
        而且也间接继承了 CachingRealm（带有缓存实现）。其中主要默认实现如下：
        org.apache.shiro.realm.text.IniRealm：[users]部分指定用户名/密码及其角色；[roles]部分指
        定角色即权限信息；
        org.apache.shiro.realm.text.PropertiesRealm：user.username=password,role1,role2 指定用户
        名/密码及其角色；role.role1=permission1,permission2 指定角色及权限信息；
        org.apache.shiro.realm.jdbc.JdbcRealm： 通过 sql 查询相应的信息， 如 “select password from
        users where username = ?”获取用户密码，“select password, password_salt from users where
        username = ?”获取用户密码及盐；“select role_name from user_roles where username = ?”
        获取用户角色；“select permission from roles_permissions where role_name = ?”获取角色对
        应的权限信息；也可以调用相应的 api 进行自定义 sql；
    
    JDBC Realm 使用 【详情参考shiro教程.pdf】
    
    
    Authenticator(认证) 及 AuthenticationStrategy(认证策略)
    
        Authenticator 的职责是验证用户帐号，是 Shiro API 中身份验证核心的入口点
    
        public AuthenticationInfo authenticate(AuthenticationToken authenticationToken)
            throws AuthenticationException;
    
    
        如果验证成功，将返回 AuthenticationInfo 验证信息；此信息中包含了身份及凭证；如果验
        证失败将抛出相应的 AuthenticationException 实现。
        SecurityManager 接口继承了 Authenticator，另外还有一个 ModularRealmAuthenticator 实现，
        其委托给多个 Realm 进行验证，验证规则通过 AuthenticationStrategy 接口指定，默认提供
        的实现：
        FirstSuccessfulStrategy：只要有一个 Realm 验证成功即可，只返回第一个 Realm 身份验证
        成功的认证信息，其他的忽略；
        AtLeastOneSuccessfulStrategy： 只要有一个 Realm 验证成功即可， 和 FirstSuccessfulStrategy
        不同，返回所有 Realm 身份验证成功的认证信息；
        AllSuccessfulStrategy：所有 Realm 验证成功才算成功，且返回所有 Realm 身份验证成功的
        认证信息，如果有一个失败就失败了。
        ModularRealmAuthenticator 默认使用 AtLeastOneSuccessfulStrategy 策略。
        
        假设我们有三个 realm：
        myRealm1： 用户名/密码为 zhang/123 时成功，且返回身份/凭据为 zhang/123；
        myRealm2： 用户名/密码为 wang/123 时成功，且返回身份/凭据为 wang/123；
        myRealm3： 用户名/密码为 zhang/123 时成功，且返回身份/凭据为 zhang@163.com/123，
        和 myRealm1 不同的是返回时的身份变了；
    
        对 于 AtLeastOneSuccessfulStrategy 和 FirstSuccessfulStrategy 的 区 别 ， 请 参 照
        testAtLeastOneSuccessfulStrategyWithSuccess和 testFirstOneSuccessfulStrategyWithSuccess测
        试方法。唯一不同点一个是返回所有验证成功的 Realm 的认证信息；另一个是只返回第一
        个验证成功的 Realm 的认证信息。
        
        自定义 AuthenticationStrategy 实现，首先看其 API：（详情参考：authenticator.strategy.AtLeastTwoAuthenticatorStrategy/OnlyOneAuthenticatorStrategy）
        因为每个 AuthenticationStrategy 实例都是无状态的，所有每次都通过接口将相应的认证信
        息传入下一次流程；通过如上接口可以进行如合并/返回第一个验证成功的认证信息。
        自定义实现时一般继承 org.apache.shiro.authc.pam.AbstractAuthenticationStrategy 即可