###  shiro权限框架 --- 授权 
@date 2016-8-24 10:53:16 @author edgar 【edgar_zhang2014@163.com】

     授权
     授权，也叫访问控制，即在应用中控制谁能访问哪些资源（如访问页面/编辑数据/页面操作
     等） 。 在授权中需了解的几个关键对象： 主体 （Subject） 、 资源 （Resource） 、 权限 （Permission） 、
     角色（Role）。
     主体
     主体，即访问应用的用户，在 Shiro 中使用 Subject 代表该用户。用户只有授权后才允许访
     问相应的资源。
     资源
     在应用中用户可以访问的任何东西，比如访问 JSP 页面、查看/编辑某些数据、访问某个业
     务方法、打印文本等等都是资源。用户只要授权后才能访问。
     权限
     安全策略中的原子授权单位，通过权限我们可以表示在应用中用户有没有操作某个资源的
     权力。即权限表示在应用中用户能不能访问某个资源，如：
     访问用户列表页面
     查看/新增/修改/删除用户数据（即很多时候都是 CRUD（增查改删）式权限控制）
     打印文档等等。。。
     如上可以看出，权限代表了用户有没有操作某个资源的权利，即反映在某个资源上的操作
     允不允许，不反映谁去执行这个操作。所以后续还需要把权限赋予给用户，即定义哪个用
     户允许在某个资源上做什么操作（权限），Shiro 不会去做这件事情，而是由实现人员提供。
     Shiro 支持粗粒度权限（如用户模块的所有权限）和细粒度权限（操作某个用户的权限，即
     实例级别的），后续部分介绍。
     角色
     角色代表了操作集合，可以理解为权限的集合，一般情况下我们会赋予用户角色而不是权
     限，即这样用户可以拥有一组权限，赋予权限时比较方便。典型的如：项目经理、技术总
     监、CTO、开发工程师等都是角色，不同的角色拥有一组不同的权限。
     隐式角色：即直接通过角色来验证用户有没有操作权限，如在应用中 CTO、技术总监、开
     发工程师可以使用打印机，假设某天不允许开发工程师使用打印机，此时需要从应用中删
     除相应代码；再如在应用中 CTO、技术总监可以查看用户、查看权限；突然有一天不允许
     技术总监查看用户、查看权限了，需要在相关代码中把技术总监角色从判断逻辑中删除掉；
     即粒度是以角色为单位进行访问控制的，粒度较粗；如果进行修改可能造成多处代码修改。
     显示角色：在程序中通过权限控制谁能访问某个资源，角色聚合一组权限集合；这样假设
     哪个角色不能访问某个资源，只需要从角色代表的权限集合中移除即可；无须修改多处代
     码；即粒度是以资源/实例为单位的；粒度较细。
     跟我学 Shiro——http://jinnianshilongnian.iteye.com/
     21
     请 google 搜索“RBAC”和“RBAC 新解”分别了解“基于角色的访问控制”“基于资源
     的访问控制(Resource-Based Access Control)”。


     授权方式
     Shiro 支持三种方式的授权：
       编程式：通过写 if/else 授权代码块完成
             Subject subject = SecurityUtils.getSubject();
             if(subject.hasRole(“admin”)) {
             //有权限
             } else {
             //无权限
             }
       注解式：通过在执行的 Java 方法上放置相应的注解完成：
           @RequiresRoles("admin")
           public void hello() {
           //有权限
           }
        没有权限将抛出相应的异常；
       JSP/GSP 标签：在 JSP/GSP 页面通过相应的标签完成：
           <shiro:hasRole name="admin">
           <!— 有权限 —>
           </shiro:hasRole>
           
     Permission
     字符串通配符权限
     规则：“资源标识符：操作：对象实例 ID” 即对哪个资源的哪个实例可以进行什么操作。
     其默认支持通配符权限字符串，“:”表示资源/操作/实例的分割；“,”表示操作的分割；
     “*”表示任意资源/操作/实例。
     
     Shiro 对权限字符串缺失部分的处理
     如“user:view”等价于“user:view:*”；而“organization”等价于“organization:*”或者
     “organization:*:*”。可以这么理解，这种方式实现了前缀匹配。
     另外如“user:*”可以匹配如“user:delete”、“user:delete”可以匹配如“user:delete:1”、
     “user:*:1”可以匹配如“user:view:1”、“user”可以匹配“user:view”或“user:view:1”
     等 。即 *可 以 匹配所 有， 不加 *可 以 进行前 缀匹 配；但 是如“ *:view” 不能匹 配
     “system:user:view”，需要使用“*:*:view”，即后缀匹配必须指定前缀（多个冒号就需要
     多个*来匹配）。
     
     授权流程:
     流程如下：
     1、首先调用 Subject.isPermitted*/hasRole*接口，其会委托给 SecurityManager，而
     SecurityManager 接着会委托给 Authorizer；
     2、Authorizer 是真正的授权者，如果我们调用如 isPermitted(“user:view”)，其首先会通过
     PermissionResolver 把字符串转换成相应的 Permission 实例；
     3、在进行授权之前，其会调用相应的 Realm 获取 Subject 相应的角色/权限用于匹配传入的
     角色/权限；
     4、Authorizer 会判断 Realm 的角色/权限是否和传入的匹配，如果有多个 Realm，会委托给
     ModularRealmAuthorizer 进行循环判断，如果匹配如 isPermitted*/hasRole*会返回 true，否
     则返回 false 表示授权失败.