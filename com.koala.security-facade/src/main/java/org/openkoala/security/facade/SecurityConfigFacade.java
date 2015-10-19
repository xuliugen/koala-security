package org.openkoala.security.facade;

import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.command.ChangeMenuResourcePropsCommand;
import org.openkoala.security.facade.command.ChangePageElementResourcePropsCommand;
import org.openkoala.security.facade.command.ChangePermissionPropsCommand;
import org.openkoala.security.facade.command.ChangeRolePropsCommand;
import org.openkoala.security.facade.command.ChangeUrlAccessResourcePropsCommand;
import org.openkoala.security.facade.command.ChangeUserEmailCommand;
import org.openkoala.security.facade.command.ChangeUserPasswordCommand;
import org.openkoala.security.facade.command.ChangeUserPropsCommand;
import org.openkoala.security.facade.command.ChangeUserTelePhoneCommand;
import org.openkoala.security.facade.command.CreateChildMenuResourceCommand;
import org.openkoala.security.facade.command.CreateMenuResourceCommand;
import org.openkoala.security.facade.command.CreatePageElementResourceCommand;
import org.openkoala.security.facade.command.CreatePermissionCommand;
import org.openkoala.security.facade.command.CreateRoleCommand;
import org.openkoala.security.facade.command.CreateUrlAccessResourceCommand;
import org.openkoala.security.facade.command.CreateUserCommand;

/**
 * 权限配置门面，按照使用角色命名，一般都是系统管理员使用该接口，对用户进行权限配置。
 * 参数都是以某某命令，返回结果都是调用结果{@link InvokeResult}。
 * 好处：以命令的方式可以很清晰的表达出每一次的操作。不会与具体的领域对象耦合。不依赖特定的技术。
 *
 * @author lucas
 */
public interface SecurityConfigFacade {

    /**
     * 创建用户。
     *
     * @param command 创建用户命令 {@link CreateUserCommand}
     */
    InvokeResult createUser(CreateUserCommand command);

    /**
     * 创建权限。
     *
     * @param command 创建权限命令 {@link CreatePermissionCommand}
     */
    InvokeResult createPermission(CreatePermissionCommand command);

    /**
     * 创建角色。
     *
     * @param command 创建角色命令 {@link CreateRoleCommand}
     */
    InvokeResult createRole(CreateRoleCommand command);

    /**
     * 创建菜单资源。
     *
     * @param command 创建菜单资源命令 {@link CreateMenuResourceCommand}
     */
    InvokeResult createMenuResource(CreateMenuResourceCommand command);

    /**
     * 创建子菜单资源。
     *
     * @param command 创建子菜单资源命令 {@link CreateChildMenuResourceCommand}
     */
    InvokeResult createChildMenuResouceToParent(CreateChildMenuResourceCommand command);

    /**
     * 创建页面元素资源。
     *
     * @param command 创建页面元素资源命令 {@link CreatePageElementResourceCommand}
     */
    InvokeResult createPageElementResource(CreatePageElementResourceCommand command);

    /**
     * 创建URL访问资源
     *
     * @param command 创建URL访问资源命令 {@link CreateUrlAccessResourceCommand}
     */
    InvokeResult createUrlAccessResource(CreateUrlAccessResourceCommand command);

    /**
     * 更改多个用户属性。
     *
     * @param command 更改用户属性命令 {@link ChangeUserPropsCommand}
     */
    InvokeResult changeUserProps(ChangeUserPropsCommand command);

    /**
     * 更改用户邮箱。
     *
     * @param command 更改用户邮箱命令 {@link ChangeUserEmailCommand}
     */
    InvokeResult changeUserEmail(ChangeUserEmailCommand command);

    /**
     * 更改用户联系电话。
     *
     * @param command 更改用户联系电话命令 {@link ChangeUserTelePhoneCommand}
     */
    InvokeResult changeUserTelePhone(ChangeUserTelePhoneCommand command);

    /**
     * 更改URL访问资源。
     *
     * @param command 更改URL访问资源命令 {@link ChangeUrlAccessResourcePropsCommand}
     */
    InvokeResult changeUrlAccessResourceProps(ChangeUrlAccessResourcePropsCommand command);

    /**
     * 更改多个角色属性。
     *
     * @param command 更改多个角色属性命令 {@link ChangeRolePropsCommand}
     * @return
     */
    InvokeResult changeRoleProps(ChangeRolePropsCommand command);

    /**
     * 更改多个权限属性。
     *
     * @param command 更改多个权限属性 {@link ChangePermissionPropsCommand}
     */
    InvokeResult changePermissionProps(ChangePermissionPropsCommand command);

    /**
     * 更改多个页面元素属性。
     *
     * @param command 更改多个页面元素属性 {@link ChangePageElementResourcePropsCommand}
     */
    InvokeResult changePageElementResourceProps(ChangePageElementResourcePropsCommand command);

    /**
     * 更改多个菜单属性。
     *
     * @param command 更改多个菜单属性命令 {@link ChangeMenuResourcePropsCommand}
     * @return
     */
    InvokeResult changeMenuResourceProps(ChangeMenuResourcePropsCommand command);

    /**
     * 更改用户密码。
     *
     * @param command 更改用户密码 {@link ChangeUserPasswordCommand}
     * @return
     */
    InvokeResult changeUserPassword(ChangeUserPasswordCommand command);

    /**
     * 根据用户ID重置用户密码。
     *
     * @param userId 用户ID
     */
    InvokeResult resetPassword(Long userId);

    /**
     * 撤销用户，当前用户不能撤销自己。
     *
     * @param userId
     * @return
     */
    InvokeResult terminateUser(Long userId, String currentUserAccount);

    /**
     * 撤销多个用户，当前用户不能撤销自己。
     *
     * @param userIds            用户ID集合
     * @param currentUserAccount 当前用户账号
     */
    InvokeResult terminateUsers(Long[] userIds, String currentUserAccount);

    /**
     * 撤销角色。
     *
     * @param roleId 角色ID
     */
    InvokeResult terminateRole(Long roleId);

    /**
     * 撤销多个角色
     *
     * @param roleIds 角色ID集合
     */
    InvokeResult terminateRoles(Long[] roleIds);

    /**
     * 撤销权限。
     *
     * @param permissionId 权限ID
     */
    InvokeResult terminatePermission(Long permissionId);

    /**
     * 撤销多个权限。
     *
     * @param permissionIds 权限ID集合
     */
    InvokeResult terminatePermissions(Long[] permissionIds);

    /**
     * 撤销菜单资源。
     *
     * @param resourceId 菜单资源ID
     */
    InvokeResult terminateMenuResource(Long resourceId);

    /**
     * 撤销多个菜单资源
     *
     * @param resourceIds 菜单资源ID集合
     */
    InvokeResult terminateMenuResources(Long[] resourceIds);

    /**
     * 撤销URL访问资源。
     *
     * @param resourceId URL访问资源ID
     */
    InvokeResult terminateUrlAccessResource(Long resourceId);

    /**
     * 撤销多个URL访问资源
     *
     * @param resourceIds URL访问资源ID集合
     * @return
     */
    InvokeResult terminateUrlAccessResources(Long[] resourceIds);

    /**
     * 撤销页面元素资源。
     *
     * @param resourceId 页面元素资源ID
     */
    InvokeResult terminatePageElementResource(Long resourceId);

    /**
     * 撤销多个页面元素资源。
     *
     * @param resourceIds 页面元素资源ID集合
     */
    InvokeResult terminatePageElementResources(Long[] resourceIds);

    /**
     * 为用户分配角色。
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    InvokeResult grantRoleToUser(Long userId, Long roleId);

    /**
     * 为用户分配多个角色。
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     * @return
     */
    InvokeResult grantRolesToUser(Long userId, Long[] roleIds);

    /**
     * 为用户分配权限。
     *
     * @param userId       用户ID
     * @param permissionId 权限ID
     */
    InvokeResult grantPermissionToUser(Long userId, Long permissionId);

    /**
     * 为用户分配多个权限。
     *
     * @param userId        用户ID
     * @param permissionIds 权限ID集合
     */
    InvokeResult grantPermissionsToUser(Long userId, Long[] permissionIds);

    /**
     * 激活用户。
     *
     * @param userId 用户ID
     */
    InvokeResult activate(Long userId);

    /**
     * 挂起用户，当前用户不能让自己挂起。
     *
     * @param userId             用户ID
     * @param currentUserAccount 当前用户账号
     */
    InvokeResult suspend(Long[] userId, String currentUserAccount);

    /**
     * 激活多个用户。
     *
     * @param userIds 用户ID集合
     */
    InvokeResult activate(Long[] userIds);

    /**
     * 挂起多个用户，当前用户不能挂起自己。
     *
     * @param userIds            用户ID
     * @param currentUserAccount 当前用户账号
     */
    InvokeResult suspend(Long userIds, String currentUserAccount);

    /**
     * 从角色中撤销与用户的关系。
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    InvokeResult terminateUserFromRole(Long userId, Long roleId);

    /**
     * 从多个角色中撤销与用户的关系。
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     */
    InvokeResult terminateUserFromRoles(Long userId, Long[] roleIds);

    /**
     * 从权限中撤销与用户的关系。
     *
     * @param userId       用户ID
     * @param permissionId 权限ID
     */
    InvokeResult terminateUserFromPermission(Long userId, Long permissionId);

    /**
     * 从多个权限中撤销与用户的关系。
     *
     * @param userId        用户ID
     * @param permissionIds 权限ID集合
     */
    InvokeResult terminateUserFromPermissions(Long userId, Long[] permissionIds);

    /**
     * 为角色分配多个菜单资源。
     *
     * @param roleId          角色ID
     * @param menuResourceIds 菜单资源ID集合
     */
    InvokeResult grantMenuResourcesToRole(Long roleId, Long[] menuResourceIds);

    /**
     * 为角色分配多个页面元素资源。
     *
     * @param roleId                 角色ID
     * @param pageElementResourceIds 页面元素资源ID
     */
    InvokeResult grantPageElementResourcesToRole(Long roleId, Long[] pageElementResourceIds);

    /**
     * 为角色分配多个URL访问资源。
     *
     * @param roleId      角色ID
     * @param resourceIds URL访问资源ID集合
     */
    InvokeResult grantUrlAccessResourcesToRole(Long roleId, Long[] resourceIds);

    /**
     * 为角色分配多个权限。
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID集合
     */
    InvokeResult grantPermissionsToRole(Long roleId, Long[] permissionIds);

    /**
     * 从角色中撤销与权限的关系。
     *
     * @param roleId       角色ID
     * @param permssionIds 权限ID
     */
    InvokeResult terminatePermissionsFromRole(Long roleId, Long[] permssionIds);

    /**
     * 从角色中撤销与多个URL访问资源的关系。
     *
     * @param roleId               角色ID
     * @param urlAccessResourceIds URL访问资源集合
     */
    InvokeResult terminateUrlAccessResourcesFromRole(Long roleId, Long[] urlAccessResourceIds);

    /**
     * 从角色中撤销与多个页面元素资源的关系。
     *
     * @param roleId                 角色ID
     * @param pageElementResourceIds 页面元素资源ID集合
     */
    InvokeResult terminatePageElementResourcesFromRole(Long roleId, Long[] pageElementResourceIds);

    /**
     * 为URL访问资源分配权限。
     *
     * @param permissionId        权限ID
     * @param urlAccessResourceId URL访问资源ID
     */
    InvokeResult grantPermisssionToUrlAccessResource(Long permissionId, Long urlAccessResourceId);

    /**
     * 为菜单资源分配权限。
     *
     * @param permissionId   权限ID
     * @param menuResourceId 菜单资源ID
     */
    InvokeResult grantPermisssionToMenuResource(Long permissionId, Long menuResourceId);

    /**
     * 为页面元素资源分配权限。
     *
     * @param permissionId          权限ID
     * @param pageElementResourceId 页面元素资源ID
     */
    InvokeResult grantPermisssionToPageElementResource(Long permissionId, Long pageElementResourceId);

    /**
     * 从URL访问资源中撤销与权限的关系。
     *
     * @param permissionId        权限ID
     * @param urlAccessResourceId URL访问资源ID
     */
    InvokeResult terminatePermissionFromUrlAccessResource(Long permissionId, Long urlAccessResourceId);

    /**
     * 从菜单资源中撤销与权限的关系。
     *
     * @param permissionId   权限ID
     * @param menuResourceId 菜单资源ID
     * @return
     */
    InvokeResult terminatePermissionFromMenuResource(Long permissionId, Long menuResourceId);

    /**
     * 从页面元素资源中撤销与权限的关系。
     *
     * @param permissionId          权限ID
     * @param pageElementResourceId 页面元素资源ID
     * @return
     */
    InvokeResult terminatePermissionFromPageElementResource(Long permissionId, Long pageElementResourceId);

    /**
     * 检查用户是否拥有页面元素资源。
     *
     * @param userAccount        用户账号
     * @param roleNameOfUser     用户的角色
     * @param resourceIdentifier 资源标识符
     */
    boolean checkUserHasPageElementResource(String userAccount, String roleNameOfUser, String resourceIdentifier);

    /**
     * 初始化系统权限资源。
     */
    public void initSecuritySystem();

}