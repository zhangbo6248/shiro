package shiro.chapter02.permission;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * @title 权限转换器
 * @description  RolePermissionResolver 用于根据角色解析相应的权限集合。
 *@author edgar
 * @date 2016-8-24 17:08:54
 */
public class BitAndWildPermissionResolver implements PermissionResolver {

	/**
     * 转换为通配符的 WildcardPermission
     * @param permissionString
     * @return
     */
    public Permission resolvePermission(String permissionString) {
        if(permissionString.startsWith("+")) {
            return new BitPermission(permissionString);
        }
        return new WildcardPermission(permissionString);
    }
}
