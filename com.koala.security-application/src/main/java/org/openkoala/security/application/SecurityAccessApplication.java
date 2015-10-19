package org.openkoala.security.application;

import org.openkoala.security.core.domain.*;

import java.util.List;

/**
 * 权限访问应用，按照使用角色命名，一般都是用户使用该接口，对系统的正常使用。
 * 减轻使用者的“概念重量”，简化功能视图。也就是说使得用户不必了解他用不着的太多东西。
 * @author lucas
 */
public interface SecurityAccessApplication {


    /**
     * 根据用户账户查找该用户拥有的所有角色。
     * @param userAccount 用户账户
     * @return 用户的所有角色集合
     */
    List<Role> findAllRolesByUserAccount(String userAccount);

    /**
     * 根据用户ID得到用户。
     * @param userId 用户ID
     * @return 用户
     */
    User getUserById(Long userId);

    /**
     * 根据参与者ID得到参与者或者其子类。
     * @param actorId 参与者ID
     * @param <T>     继承参与者
     * @return 参与者或者其子类
     */
    <T extends Actor> T getActorById(Long actorId);

    /**
     * 根据角色ID得到角色。
     * @param roleId 角色ID
     * @return 角色
     */
    Role getRoleBy(Long roleId);

    /**
     * 根据用户账户得到用户。
     * @param userAccount 用户账户
     * @return 用户
     */
    User getUserByUserAccount(String userAccount);

    /**
     * 根据权限ID得到权限。
     * @param permissionId 权限ID
     * @return 权限
     */
    Permission getPermissionBy(Long permissionId);

    /**
     * 根据菜单资源ID得到菜单资源。
     * @param menuResourceId 菜单资源ID
     * @return 菜单资源
     */
    MenuResource getMenuResourceBy(Long menuResourceId);

    /**
     * 根据URL访问资源ID得到URL访问资源。
     * @param urlAccessResourceId URL访问资源ID
     * @return URL访问资源
     */
    UrlAccessResource getUrlAccessResourceBy(Long urlAccessResourceId);

    /**
     * 根据页面元素ID得到页面元素资源。
     * @param pageElementResourceId 页面元素资源ID
     * @return 页面元素资源
     */
    PageElementResource getPageElementResourceBy(Long pageElementResourceId);

    /**
     * 根据角色名称得到角色。
     * @param roleName 角色名称
     * @return 角色
     */
    Role getRoleBy(String roleName);

    /**
     * 根据范围ID得到范围或者得到其子类。
     * @param scopeId 范围ID
     * @param <T>     继承范围
     * @return 范围或者其子类
     */
    <T extends Scope> T getScope(Long scopeId);

    /**
     * 根据用户账户查找用户所拥有的所有菜单资源集合。
     * @param userAccount 用户账户
     * @return 菜单资源集合
     */
    List<MenuResource> findMenuResourceByUserAccount(String userAccount);

    /**
     * 更新用户密码。
     * @param user            用户
     * @param userPassword    新密码
     * @param oldUserPassword 旧密码
     * @return 返回<code>true</code>,成功修改用户密码，返回<code>false</code>,则失败
     */
    boolean updatePassword(User user, String userPassword, String oldUserPassword);

    /**
     * 根据角色查找其拥有的所有菜单资源集合。
     * @param role 角色
     * @return 菜单资源集合
     */
    List<MenuResource> findAllMenuResourcesByRole(Role role);

    /**
     * 判断页面元素元素资源是否已经拥有了该标识符。
     * @param identifier 页面元素资源标识符
     * @return 返回<code>true</code>,拥有，返回<code>false</code>,不存在
     */
    boolean hasPageElementResource(String identifier);

    /**
     * 根据用户邮箱得到用户。
     * @param email 用户邮箱
     * @return 用户
     */
    User getUserByEmail(String email);

    /**
     * 根据用户联系电话得到用户。
     * @param telePhone 用户联系电话
     * @return 用户
     */
    User getUserByTelePhone(String telePhone);

    /**
     * 根据菜单资源ID集合查找菜单资源集合。
     * @param menuResourceIds 菜单资源ID集合
     * @return 菜单资源集合
     */
    List<MenuResource> findAllMenuResourcesByIds(Long[] menuResourceIds);

    /**
     * 检测角色名称是否存在。
     * @param roleName 角色名称
     * @return 返回<code>true</code>,存在；返回<code>false</code>,不存在
     */
    boolean checkRoleByName(String roleName);

    /**
     * 根据授权ID得到授权或者其子类。
     * @param authorityId 授权ID
     * @param <T>         继承授权
     * @return 授权或者其子类
     */
    <T extends Authority> T getAuthority(Long authorityId);

    /**
     * 查找用户所拥有的所有不重复角色集合。
     * @param user 用户
     * @return 角色集合
     */
    List<Role> findRolesOfUser(User user);

    /**
     * 查找用户拥有的所有不重复的权限集合。
     * @param user 用户
     * @return 权限集合
     */
    List<Permission> findPermissionsOfUser(User user);

    /**
     * 查找角色拥有的所有不重复的菜单资源集合。
     * @param role 角色
     * @return 菜单资源集合
     */
    List<MenuResource> findMenuResourcesOfRole(Role role);

    /**
     * 查找角色拥有的所有不重复的URL访问资源集合。
     * @param role 角色
     * @return URL访问资源集合
     */
    List<UrlAccessResource> findUrlAccessResourcesOfRole(Role role);

    /**
     * 查找角色拥有的所有不重复的页面元素资源集合。
     * @param role 角色
     * @return 页面元素资源集合
     */
    List<PageElementResource> findPageElementResourcesOfRole(Role role);

    /**
     * 查找权限拥有的所有不重复的资源集合。
     * @param permission 权限
     * @return 资源集合
     */
    List<SecurityResource> findResourcesByPermission(Permission permission);

    /**
     * 判断仓储中用户是否存在。
     * @return 返回<code>true</code>,存在，返回<code>false</code>,不存在
     */
    boolean hasUserExisted();
}