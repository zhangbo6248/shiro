package shiro.chapter02.permission;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.Arrays;
import java.util.Collection;

/**
 * @title 权限转换器
 * @description  RolePermissionResolver 用于根据角色解析相应的权限集合。
 *@author edgar
 * @date 2016-8-24 16:24:30
 */
public class MyRolePermissionResolver implements RolePermissionResolver {

	/**
     * 将角色权限字符串转换成相应的 Permission 实例
     * @param roleString
     * @return
     */
    public Collection<Permission> resolvePermissionsInRole(String roleString) {
        if("role1".equals(roleString)) {
            return Arrays.asList((Permission)new WildcardPermission("menu:*"));
        }
        return null;
    }
}
