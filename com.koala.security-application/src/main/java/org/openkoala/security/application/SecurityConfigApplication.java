package org.openkoala.security.application;

import java.util.List;
import java.util.Set;

import org.openkoala.security.core.domain.Actor;
import org.openkoala.security.core.domain.Authority;
import org.openkoala.security.core.domain.MenuResource;
import org.openkoala.security.core.domain.PageElementResource;
import org.openkoala.security.core.domain.Permission;
import org.openkoala.security.core.domain.Role;
import org.openkoala.security.core.domain.Scope;
import org.openkoala.security.core.domain.SecurityResource;
import org.openkoala.security.core.domain.UrlAccessResource;
import org.openkoala.security.core.domain.User;

/**
 * 权限配置应用，按照角色命名，一般都是系统管理员使用该接口，对用户进行权限配置。
 *
 * @author lucas
 */
public interface SecurityConfigApplication {

    /**
     * 撤销参与者。
     *
     * @param actor 撤销的参与者
     */
    void terminateActor(Actor actor);

    /**
     * 挂起用户，让用户不能被使用。
     *
     * @param user 挂起的用户
     */
    void suspendUser(User user);

    /**
     * 激活用户，让用户能够再次被使用。
     *
     * @param user 激活的用户
     */
    void activateUser(User user);

    /**
     * 创建授权，可以是角色和权限。
     *
     * @param authority 创建的授权
     */
    void createAuthority(Authority authority);

    /**
     * 撤销授权，可以是角色和权限。
     *
     * @param authority 撤销的授权
     */
    void terminateAuthority(Authority authority);

    /**
     * 创建权限资源，可以是菜单资源，页面元素资源，方法调用资源，URL访问资源。
     *
     * @param securityResource 创建的权限资源
     */
    void createSecurityResource(SecurityResource securityResource);

    /**
     * 撤销权限资源，可以是菜单资源，页面元素资源，方法调用资源，URL访问资源。
     *
     * @param securityResource 撤销的权限资源
     */
    void terminateSecurityResource(SecurityResource securityResource);

    /**
     * 为权限资源分配授权，即为权限资源分配权限和角色。
     *
     * @param authority        授权
     * @param securityResource 权限资源
     */
    void grantAuthorityToSecurityResource(Authority authority, SecurityResource securityResource);

    /**
     * 为多个权限资源分配授权，即为多个权限资源分配权限和角色。
     *
     * @param authority         授权
     * @param securityResources 权限资源集合
     */
    void grantAuthorityToSecurityResources(Authority authority, List<? extends SecurityResource> securityResources);

    /**
     * 为权限分配角色。
     *
     * @param role       角色
     * @param permission 权限
     */
    void grantRoleToPermission(Role role, Permission permission);

    /**
     * 为多个权限分配角色
     *
     * @param role       角色
     * @param permission 权限集合
     */
    void grantRoleToPermissions(Role role, List<Permission> permission);

    /**
     * 为权限分配多个角色。
     *
     * @param roles      角色集合
     * @param permission 权限
     */
    void grantRolesToPermission(List<Role> roles, Permission permission);

    /**
     * 为多个授权分配参与者(用户、用户组)。
     *
     * @param actor       参与者
     * @param authorities 授权集合
     */
    void grantActorToAuthorities(Actor actor, List<Authority> authorities);

    /**
     * 为授权分配多个参与者(用户、用户组)。
     *
     * @param actors    参与者集合
     * @param authority 授权
     */
    void grantActorsToAuthority(List<Actor> actors, Authority authority);

    /**
     * 从授权中撤销权限资源。
     *
     * @param securityResource 权限资源
     * @param authority        授权
     */
    void terminateSecurityResourceFromAuthority(SecurityResource securityResource, Authority authority);

    /**
     * 从授权中撤销多个权限资源。
     *
     * @param securityResources 权限资源集合
     * @param authority         授权
     */
    void terminateSecurityResourcesFromAuthority(List<? extends SecurityResource> securityResources, Authority authority);

    /**
     * 从权限资源中撤销多个授权。
     *
     * @param authorities      授权集合
     * @param securityResource 权限资源
     */
    void terminateAuthoritiesFromSecurityResource(List<Authority> authorities, SecurityResource securityResource);

    /**
     * 从权限资源中撤销授权。
     *
     * @param authority        授权
     * @param securityResource 权限资源
     */
    void terminateAuthorityFromSecurityResource(Authority authority, SecurityResource securityResource);

    /**
     * 从角色中撤销权限。
     *
     * @param permission 权限
     * @param role       角色
     */
    void terminatePermissionFromRole(Permission permission, Role role);

    /**
     * 从角色中撤销多个权限。
     *
     * @param permissions 权限集合
     * @param role        角色
     */
    void terminatePermissionsFromRole(List<Permission> permissions, Role role);

    /**
     * 从权限中撤销多个角色。
     *
     * @param roles      角色集合
     * @param permission 权限
     */
    void terminateRolesFromPermission(List<Role> roles, Permission permission);

    /**
     * 从授权中撤销参与者。
     *
     * @param actor     参与者
     * @param authority 权限
     */
    void terminateActorFromAuthority(Actor actor, Authority authority);

    /**
     * 从参与者中撤销多个授权。
     *
     * @param authorities 授权集合
     * @param actor       参与者
     */
    void terminateAuthoritiesFromActor(List<Authority> authorities, Actor actor);

    /**
     * 创建参与者。
     *
     * @param actor 参与者
     */
    void createActor(Actor actor);

    /**
     * 创建范围。
     *
     * @param scope 范围
     */
    void createScope(Scope scope);

    /**
     * 在某个范围下对参与者分配授权。
     *
     * @param actor
     * @param authority
     * @param scope
     */
    void grantActorToAuthorityInScope(Actor actor, Authority authority, Scope scope);

    /**
     * 重置用户的密码。
     *
     * @param user 用户
     */
    void resetPassword(User user);

    /**
     * TODO 是否需要修改为直接传递为对象。
     * <p/>
     * 为菜单创建子菜单。
     *
     * @param child    子菜单
     * @param parentId 父菜单ID
     */
    void createChildToParent(MenuResource child, Long parentId);

    /**
     * 为授权分配多个权限资源。
     *
     * @param securityResources 权限资源集合
     * @param authority         授权
     */
    void grantSecurityResourcesToAuthority(List<? extends SecurityResource> securityResources, Authority authority);

    /**
     * 为授权分配权限资源。
     *
     * @param securityResource 权限资源
     * @param authority        授权
     */
    void grantSecurityResourceToAuthority(SecurityResource securityResource, Authority authority);

    /**
     * 为角色分配权限。
     *
     * @param permission 权限
     * @param role       角色
     */
    void grantPermissionToRole(Permission permission, Role role);

    /**
     * 为角色授权多个权限。
     *
     * @param permissions 权限集合
     * @param role        角色
     */
    void grantPermissionsToRole(List<Permission> permissions, Role role);

    /**
     * 检查授权是否拥有页面元素资源。
     *
     * @param authorities 授权集合
     * @param identifier  页面元素资源标识符
     * @return 如果返回<code>true</code>，授权拥有页面元素资源，如果返回<code>false</code>，就相反
     */
    boolean checkAuthoritiHasPageElementResource(Set<Authority> authorities, String identifier);

    /**
     * 为参与者分配授权。
     *
     * @param authority 授权
     * @param actor     参与者
     */
    void grantAuthorityToActor(Authority authority, Actor actor);

    /**
     * 更改用户账号，需要用户密码进行确认。
     *
     * @param user         用户
     * @param userAccount  更改的用户账号
     * @param userPassword 用户密码
     */
    void changeUserAccount(User user, String userAccount, String userPassword);

    /**
     * 更改用户邮箱，需要用户密码进行确认。
     *
     * @param user         用户
     * @param email        更改的邮箱
     * @param userPassword 用户密码
     */
    void changeUserEmail(User user, String email, String userPassword);

    /**
     * 更改用户联系电话，需要用户密码进行确认。
     *
     * @param user         用户
     * @param telePhone    更改的联系电话
     * @param userPassword 用户密码
     */
    void changeUserTelePhone(User user, String telePhone, String userPassword);

    /**
     * 更改URL访问资源的名称。
     *
     * @param urlAccessResource URL访问资源
     * @param name              需要更改的URL访问资源名称
     */
    void changeNameOfUrlAccessResource(UrlAccessResource urlAccessResource, String name);

    /**
     * 更改URL访问资源的URL属性，URL属性一般不应该随意更改。
     *
     * @param urlAccessResource URL访问资源
     * @param url               需要更改的URL访问资源URL
     */
    void changeUrlOfUrlAccessResource(UrlAccessResource urlAccessResource, String url);

    /**
     * 更改角色的名称。
     *
     * @param role 角色
     * @param name 需要更改的角色名称
     */
    void changeNameOfRole(Role role, String name);

    /**
     * 更改权限的名称。
     *
     * @param permission 权限
     * @param name       需要更改的权限名称
     */
    void changeNameOfPermission(Permission permission, String name);

    /**
     * 更改权限的标识符。
     *
     * @param permission 权限
     * @param identifier 需要更改的权限标识符
     */
    void changeIdentifierOfPermission(Permission permission, String identifier);

    /**
     * 更改页面元素资源的名称。
     *
     * @param pageElementResource 页面元素资源
     * @param name                需要更改的页面元素资源名称
     */
    void changeNameOfPageElementResouce(PageElementResource pageElementResource, String name);

    /**
     * 更改页面元素资源的标识符。
     *
     * @param pageElementResource 页面元素资源
     * @param identifier          需要更改的页面元素资源标识符
     */
    void changeIdentifierOfPageElementResouce(PageElementResource pageElementResource, String identifier);

    /**
     * 更改菜单资源的名称。
     *
     * @param menuResource 菜单资源
     * @param name         需要更改的菜单资源的名称
     */
    void changeNameOfMenuResource(MenuResource menuResource, String name);

    /**
     * 在某个范围下，从授权中撤销参与者。
     *
     * @param actor     参与者
     * @param authority 授权
     * @param scope     范围
     */
    void terminateActorFromAuthorityInScope(Actor actor, Authority authority, Scope scope);

    /**
     * 更改用户的最后修改时间。
     *
     * @param user 用户
     */
    void changeLastModifyTimeOfUser(User user);
}