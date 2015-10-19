package org.openkoala.security.facade.impl;

import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;

import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.application.SecurityAccessApplication;
import org.openkoala.security.application.SecurityConfigApplication;
import org.openkoala.security.application.systeminit.SystemInit;
import org.openkoala.security.application.systeminit.SystemInitFactory;
import org.openkoala.security.core.CorrelationException;
import org.openkoala.security.core.EmailIsExistedException;
import org.openkoala.security.core.IdentifierIsExistedException;
import org.openkoala.security.core.NameIsExistedException;
import org.openkoala.security.core.TelePhoneIsExistedException;
import org.openkoala.security.core.UrlIsExistedException;
import org.openkoala.security.core.UserAccountIsExistedException;
import org.openkoala.security.core.UserPasswordException;
import org.openkoala.security.core.domain.Authority;
import org.openkoala.security.core.domain.MenuResource;
import org.openkoala.security.core.domain.PageElementResource;
import org.openkoala.security.core.domain.Permission;
import org.openkoala.security.core.domain.Role;
import org.openkoala.security.core.domain.UrlAccessResource;
import org.openkoala.security.core.domain.User;
import org.openkoala.security.facade.SecurityConfigFacade;
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
import org.openkoala.security.facade.impl.assembler.MenuResourceAssembler;
import org.openkoala.security.facade.impl.assembler.PageElementResourceAssembler;
import org.openkoala.security.facade.impl.assembler.PermissionAssembler;
import org.openkoala.security.facade.impl.assembler.RoleAssembler;
import org.openkoala.security.facade.impl.assembler.UrlAccessResourceAssembler;
import org.openkoala.security.facade.impl.assembler.UserAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@Transactional(value = "transactionManager_security")
@Named
public class SecurityConfigFacadeImpl implements SecurityConfigFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfigFacadeImpl.class);

    @Inject
    private SecurityConfigApplication securityConfigApplication;

    @Inject
    private SecurityAccessApplication securityAccessApplication;

    @Override
    public InvokeResult createUser(CreateUserCommand command) {
        try {
            User user = UserAssembler.toUser(command);
            securityConfigApplication.createActor(user);
            return InvokeResult.success();
        } catch (UserAccountIsExistedException e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("用户账号:" + command.getUserAccount() + "已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加用户失败。");
        }
    }

    @Override
    public InvokeResult terminateUsers(Long[] userIds, String currentUserAccount) {
        InvokeResult result = null;
        for (Long userId : userIds) {
            result = this.terminateUser(userId, currentUserAccount);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    @Override
    public InvokeResult terminateUser(Long userId, String currentUserAccount) {
        User user = securityAccessApplication.getUserById(userId);
        if (user.getUserAccount().equals(currentUserAccount)) {
            return InvokeResult.failure("不能撤销自己！");
        }
        securityConfigApplication.terminateActor(user);
        return InvokeResult.success();
    }

    @Override
    public InvokeResult resetPassword(Long userId) {
        User user = securityAccessApplication.getUserById(userId);
        securityConfigApplication.resetPassword(user);
        return InvokeResult.success();
    }

    @Override
    public InvokeResult changeUserPassword(ChangeUserPasswordCommand command) {
        User user = securityAccessApplication.getUserByUserAccount(command.getUserAccount());
        return securityAccessApplication.updatePassword(user, command.getUserPassword(), command.getOldUserPassword())
                ? InvokeResult.success()
                : InvokeResult.failure("原始密码输入不正确!");
    }

    @Override
    public InvokeResult terminateRoles(Long[] roleIds) {
        InvokeResult result = null;
        for (Long roleId : roleIds) {
            result = this.terminateRole(roleId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    @Override
    public InvokeResult terminateRole(Long roleId) {
        Role role = null;
        try {
            role = securityAccessApplication.getRoleBy(roleId);
            securityConfigApplication.terminateAuthority(role);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销角色：" + role.getName() + "失败。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销角色失败");
        }
    }

    @Override
    public InvokeResult terminateMenuResources(Long[] resourceIds) {
        InvokeResult result = null;
        for (Long menuResourceId : resourceIds) {
            result = terminateMenuResource(menuResourceId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    @Override
    public InvokeResult terminateMenuResource(Long resourceId) {
        try {
            MenuResource menuResource = securityAccessApplication.getMenuResourceBy(resourceId);
            securityConfigApplication.terminateSecurityResource(menuResource);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("不能撤销，因为有角色或者权限关联。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销菜单全线资源失败。");
        }
    }

    @Override
    public InvokeResult grantRoleToUser(Long userId, Long roleId) {
        try {
            User user = securityAccessApplication.getUserById(userId);
            Role role = securityAccessApplication.getRoleBy(roleId);
            securityConfigApplication.grantAuthorityToActor(role, user);
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("为用户授权一个角色失败。");
        }
    }

    @Override
    public InvokeResult grantRolesToUser(Long userId, Long[] roleIds) {
        try {
            for (Long roleId : roleIds) {
                this.grantRoleToUser(userId, roleId);
            }
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("为用户授权多个角色失败。");
        }
    }

    @Override
    public InvokeResult grantPermissionToUser(Long userId, Long permissionId) {
        try {
            User user = securityAccessApplication.getUserById(userId);
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.grantAuthorityToActor(permission, user);
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("为用户授权一个权限失败。");
        }
    }

    @Override
    public InvokeResult grantPermissionsToUser(Long userId, Long[] permissionIds) {
        try {
            for (Long permissionId : permissionIds) {
                grantPermissionToUser(userId, permissionId);
            }
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("为用户授权多个权限失败。");
        }
    }

    @Override
    public InvokeResult activate(Long userId) {
        User user = null;
        try {
            user = securityAccessApplication.getUserById(userId);
            if (!user.isDisabled()) {
                return InvokeResult.failure("用户：" + user.getUserAccount() + "已经是激活状态，不需要再次激活！");
            }
            securityConfigApplication.activateUser(user);
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("激活用户：" + user.getUserAccount() + "失败。");
        }
    }

    @Override
    public InvokeResult suspend(Long userId, String currentUserAccount) {
        User user = null;
        try {
            user = securityAccessApplication.getUserById(userId);
            if (user.isDisabled()) {
                return InvokeResult.failure("用户：" + user.getUserAccount() + "已经是禁用状态，不需要再次禁用！");
            }

            if (user.getUserAccount().equals(currentUserAccount)) {
                return InvokeResult.failure("不能禁用自己！");
            }
            securityConfigApplication.suspendUser(user);
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("禁用用户：" + user.getUserAccount() + "失败。");
        }

    }

    @Override
    public InvokeResult activate(Long[] userIds) {
        InvokeResult result = null;
        for (Long userId : userIds) {
            result = this.activate(userId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    @Override
    public InvokeResult suspend(Long[] userIds, String currentUserAccount) {
        InvokeResult result = null;
        for (Long userId : userIds) {
            result = this.suspend(userId, currentUserAccount);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    @Override
    public InvokeResult terminateUserFromRole(Long userId, Long roleId) {
        try {
            Role role = securityAccessApplication.getRoleBy(roleId);
            User user = securityAccessApplication.getUserById(userId);
            securityConfigApplication.terminateActorFromAuthority(user, role);
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("撤销用户的一个角色失败。");
        }
    }

    @Override
    public InvokeResult terminateUserFromPermission(Long userId, Long permissionId) {
        try {
            User user = securityAccessApplication.getUserById(userId);
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.terminateActorFromAuthority(user, permission);
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("撤销用户的多个权限失败。");
        }
    }

    // TODO 待优化。。。
    @Override
    public InvokeResult terminateUserFromRoles(Long userId, Long[] roleIds) {
        try {
            User user = securityAccessApplication.getUserById(userId);
            for (Long roleId : roleIds) {
                Role role = securityAccessApplication.getRoleBy(roleId);
                securityConfigApplication.terminateActorFromAuthority(user, role);
            }
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("撤销用户的多个角色失败。");
        }
    }

    @Override
    public InvokeResult terminateUserFromPermissions(Long userId, Long[] permissionIds) {
        try {
            for (Long permissionId : permissionIds) {
                InvokeResult invokeResult = this.terminateUserFromPermission(userId, permissionId);
                if (!invokeResult.isSuccess()) {
                    break;
                }
            }
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("撤销用户的多个权限失败。");
        }

    }

    /**
     * 为角色授权菜单资源。
     */
    @Override
    public InvokeResult grantMenuResourcesToRole(Long roleId, Long[] menuResourceIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);

        // 现在的

        List<MenuResource> targetOwnerMenuResources = securityAccessApplication.findAllMenuResourcesByIds(menuResourceIds);

//		List<MenuResource> targetOwnerMenuResources = transFromMenuResourcesBy(menuResourceIds);


        // 原有的 TODO 可以门面层的查询选中项的方法变成一个。
        List<MenuResource> originalOwnerMenuResources = securityAccessApplication.findAllMenuResourcesByRole(role);

        List<MenuResource> tmpList = Lists.newArrayList(targetOwnerMenuResources);

        // 待添加的
        List<MenuResource> waitingAddList = new ArrayList<MenuResource>();

        // 带删除的
        List<MenuResource> waitingDelList = new ArrayList<MenuResource>();

        // 得到相同的菜单
        targetOwnerMenuResources.retainAll(originalOwnerMenuResources);

        // 原有菜单删除相同菜单
        originalOwnerMenuResources.removeAll(targetOwnerMenuResources);

        // 得到待删除的菜单
        waitingDelList.addAll(originalOwnerMenuResources);

        // 现有菜单删除相同菜单
        tmpList.removeAll(targetOwnerMenuResources);

        // 得到带添加的菜单
        waitingAddList.addAll(tmpList);

        securityConfigApplication.terminateSecurityResourcesFromAuthority(waitingDelList, role);
        securityConfigApplication.grantSecurityResourcesToAuthority(waitingAddList, role);

        LOGGER.info("----> waiting delete menuResource list :{}", waitingDelList);
        LOGGER.info("----> waiting add menuResource list :{}", waitingAddList);
        return InvokeResult.success();
    }

    @Override
    public InvokeResult grantPageElementResourcesToRole(Long roleId, Long[] pageElementResourceIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);
        for (Long pageElementResourceId : pageElementResourceIds) {
            PageElementResource pageElementResource = securityAccessApplication
                    .getPageElementResourceBy(pageElementResourceId);
            securityConfigApplication.grantSecurityResourceToAuthority(pageElementResource, role);
        }
        return InvokeResult.success();
    }

    @Override
    public InvokeResult grantUrlAccessResourcesToRole(Long roleId, Long[] urlAccessResourceIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);
        for (Long urlAccessResourceId : urlAccessResourceIds) {
            UrlAccessResource urlAccessResource = securityAccessApplication.getUrlAccessResourceBy(urlAccessResourceId);
            securityConfigApplication.grantSecurityResourceToAuthority(urlAccessResource, role);
        }
        return InvokeResult.success();

    }

    @Override
    public InvokeResult grantPermissionsToRole(Long roleId, Long[] permissionIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);
        for (Long permissionId : permissionIds) {
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.grantRoleToPermission(role, permission);
            securityConfigApplication.grantPermissionToRole(permission, role);
        }
        return InvokeResult.success();
    }

    @Override
    public InvokeResult terminatePermissionsFromRole(Long roleId, Long[] permssionIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);
        for (Long permissionId : permssionIds) {
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.terminatePermissionFromRole(permission, role);
        }
        return InvokeResult.success();
    }

    @Override
    public InvokeResult terminateUrlAccessResources(Long[] resourceIds) {
        InvokeResult result = null;
        for (Long urlAccessResourceId : resourceIds) {
            result = this.terminateUrlAccessResource(urlAccessResourceId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return InvokeResult.success();
    }

    public InvokeResult terminateUrlAccessResource(Long resourceId) {
        try {
            UrlAccessResource urlAccessResource = securityAccessApplication.getUrlAccessResourceBy(resourceId);
            securityConfigApplication.terminateSecurityResource(urlAccessResource);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销URL访问权限资源失败，有角色或者权限关联。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销URL访问权限资源失败");
        }
    }

    @Override
    public InvokeResult terminateUrlAccessResourcesFromRole(Long roleId, Long[] urlAccessResourceIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);
        for (Long urlAccessResourceId : urlAccessResourceIds) {
            UrlAccessResource urlAccessResource = securityAccessApplication.getUrlAccessResourceBy(urlAccessResourceId);
            securityConfigApplication.terminateSecurityResourceFromAuthority(urlAccessResource, role);
        }
        return InvokeResult.success();
    }

    @Override
    public InvokeResult grantPermisssionToUrlAccessResource(Long permissionId, Long urlAccessResourceId) {
        try {
            UrlAccessResource urlAccessResource = securityAccessApplication.getUrlAccessResourceBy(urlAccessResourceId);
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.grantAuthorityToSecurityResource(permission, urlAccessResource);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("该权限已经被授权给其他的URL访问资源。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("系统错误");
        }
    }

    @Override
    public InvokeResult terminatePermissionFromUrlAccessResource(Long permissionId, Long urlAccessResourceId) {
        UrlAccessResource urlAccessResource = securityAccessApplication.getUrlAccessResourceBy(urlAccessResourceId);
        Permission permission = securityAccessApplication.getPermissionBy(permissionId);
        securityConfigApplication.terminateAuthorityFromSecurityResource(permission, urlAccessResource);
        return InvokeResult.success();
    }

    @Override
    public InvokeResult grantPermisssionToMenuResource(Long permissionId, Long menuResourceId) {
        try{
            MenuResource menuResource = securityAccessApplication.getMenuResourceBy(menuResourceId);
            Permission permssion = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.grantAuthorityToSecurityResource(permssion, menuResource);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("该权限已经被授权给其他的菜单资源。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("系统错误");
        }
    }

    @Override
    public InvokeResult terminatePermissionFromMenuResource(Long permissionId, Long menuResourceId) {
        MenuResource menuResource = securityAccessApplication.getMenuResourceBy(menuResourceId);
        Permission permssion = securityAccessApplication.getPermissionBy(permissionId);
        securityConfigApplication.terminateAuthorityFromSecurityResource(permssion, menuResource);
        return InvokeResult.success();

    }

    @Override
    public InvokeResult terminatePageElementResources(Long[] resourceIds) {
        InvokeResult result = null;
        for (Long pageElementResourceId : resourceIds) {
            result = this.terminatePageElementResource(pageElementResourceId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    public InvokeResult terminatePageElementResource(Long resourceId) {
        try {
            PageElementResource pageElementResource = securityAccessApplication.getPageElementResourceBy(resourceId);
            securityConfigApplication.terminateSecurityResource(pageElementResource);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("因为有角色或者权限，不能撤销。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销页面元素权限资源失败。");
        }
    }

    @Override
    public InvokeResult terminatePageElementResourcesFromRole(Long roleId, Long[] pageElementResourceIds) {
        Role role = securityAccessApplication.getRoleBy(roleId);
        for (Long pageElementResourceId : pageElementResourceIds) {
            PageElementResource pageElementResource = securityAccessApplication
                    .getPageElementResourceBy(pageElementResourceId);
            securityConfigApplication.terminateSecurityResourceFromAuthority(pageElementResource, role);
        }
        return InvokeResult.success();
    }

    @Override
    public InvokeResult grantPermisssionToPageElementResource(Long permissionId, Long pageElementResourceId) {
        try {
            PageElementResource pageElementResource = securityAccessApplication.getPageElementResourceBy(pageElementResourceId);
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.grantAuthorityToSecurityResource(permission, pageElementResource);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("该权限已经被授权给其他的页面元素资源。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("系统错误");
        }
    }

    @Override
    public InvokeResult terminatePermissionFromPageElementResource(Long permissionId, Long pageElementResourceId) {
        PageElementResource pageElementResource = securityAccessApplication.getPageElementResourceBy(pageElementResourceId);
        Permission permission = securityAccessApplication.getPermissionBy(permissionId);
        securityConfigApplication.terminateAuthorityFromSecurityResource(permission, pageElementResource);
        return InvokeResult.success();
    }

    @Override
    public boolean checkUserHasPageElementResource(String userAccount, String roleNameOfUser, String resourceIdentifier) {

        if (!securityAccessApplication.hasPageElementResource(resourceIdentifier)) {
            return true;
        }

        Set<Authority> authorities = new HashSet<Authority>();
        // 可能用户并没有分配角色。因此需要对其获取异常。
        Role role = securityAccessApplication.getRoleBy(roleNameOfUser);
        if(role != null){
            Set<Permission> rolePermissions = role.getPermissions();
            authorities.add(role);
            authorities.addAll(rolePermissions);
        }

        List<Permission> userPermissions = User.findAllPermissionsBy(userAccount);
        authorities.addAll(userPermissions);
        return securityConfigApplication.checkAuthoritiHasPageElementResource(authorities, resourceIdentifier);
    }

    @Override
    public void initSecuritySystem() {
        if (securityAccessApplication.hasUserExisted()) {
            return;
        }
        SystemInit init = SystemInitFactory.INSTANCE.getSystemInit("/META-INF/systemInit/systemInit.xml");
        User user = init.createUser();
        Role role = init.createRole();
        List<MenuResource> menuResources = init.createMenuResourceAndReturnNeedGrant();
        List<PageElementResource> pageElementResources = init.createPageElementResources();
        List<UrlAccessResource> urlAccessResources = init.createUrlAccessResources();
        securityConfigApplication.grantAuthorityToActor(role, user);
        securityConfigApplication.grantSecurityResourcesToAuthority(menuResources, role);
        securityConfigApplication.grantSecurityResourcesToAuthority(pageElementResources, role);
        securityConfigApplication.grantSecurityResourcesToAuthority(urlAccessResources, role);
    }

    @Override
    public InvokeResult changeUserProps(ChangeUserPropsCommand command) {
        User user = securityAccessApplication.getUserById(command.getId());
        user.setName(command.getName());
        user.setDescription(command.getDescription());
        securityConfigApplication.createActor(user);
        securityConfigApplication.changeLastModifyTimeOfUser(user);
        return InvokeResult.success();
    }

    @Override
    public InvokeResult changeUserEmail(ChangeUserEmailCommand command) {
        try {
            User user = securityAccessApplication.getUserByUserAccount(command.getUserAccount());
            securityConfigApplication.changeUserEmail(user, command.getEmail(), command.getUserPassword());
            securityConfigApplication.changeLastModifyTimeOfUser(user);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("邮箱或者密码不能为空！");
        } catch (UserPasswordException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("输入密码错误！");
        } catch (ConstraintViolationException e) { // TODO 获取不到
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("邮箱不合法，请重新输入！");
        } catch (EmailIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("邮箱已经存在，请重新输入！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("系统错误！");
        }
    }

    @Override
    public InvokeResult changeUserTelePhone(ChangeUserTelePhoneCommand command) {
        try {
            User user = securityAccessApplication.getUserByUserAccount(command.getUserAccount());
            securityConfigApplication.changeUserTelePhone(user, command.getTelePhone(), command.getUserPassword());
            securityConfigApplication.changeLastModifyTimeOfUser(user);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("电话或者密码不能为空！");
        } catch (UserPasswordException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("输入密码错误！");
        } catch (TelePhoneIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("联系电话已经存在！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("系统错误！");
        }
    }

    @Override
    public InvokeResult createUrlAccessResource(CreateUrlAccessResourceCommand command) {
        try {
            UrlAccessResource urlAccessResource = UrlAccessResourceAssembler.toUrlAccessResource(command);
            securityConfigApplication.createSecurityResource(urlAccessResource);
            return InvokeResult.success();
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("URL访问权限资源名称：" + command.getName() + "已经存在。");
        } catch (UrlIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("URL访问权限资源URL：" + command.getUrl() + "已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加URL访问权限资源失败");
        }
    }

    @Override
    public InvokeResult changeUrlAccessResourceProps(ChangeUrlAccessResourcePropsCommand command) {
        try {
            UrlAccessResource urlAccessResource = securityAccessApplication.getUrlAccessResourceBy(command.getId());
            securityConfigApplication.changeNameOfUrlAccessResource(urlAccessResource, command.getName());
            urlAccessResource.setDescription(command.getDescription());
            securityConfigApplication.createSecurityResource(urlAccessResource);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("名称或者URL为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更新URL访问权限资源名称：" + command.getName() + "已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更新URL访问权限资源失败。");
        }
    }

    @Override
    public InvokeResult createRole(CreateRoleCommand command) {
        try {
            Role role = RoleAssembler.toRole(command);
            securityConfigApplication.createAuthority(role);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("添加角色名称不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("添加角色名称：" + command.getName() + "已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("添加角色失败。");
        }
    }

    @Override
    public InvokeResult changeRoleProps(ChangeRolePropsCommand command) {
        try {
            Role role = securityAccessApplication.getRoleBy(command.getId());
            securityConfigApplication.changeNameOfRole(role, command.getName());
            role.setDescription(command.getDescription());
            securityConfigApplication.createAuthority(role);

            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("更改角色名称不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("更改角色名称：" + command.getName() + "已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.failure("更改角色失败。");
        }
    }

    @Override
    public InvokeResult createPermission(CreatePermissionCommand command) {
        try {
            Permission permission = PermissionAssembler.toPermission(command);
            securityConfigApplication.createAuthority(permission);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("权限名称或者标识不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加权限失败，权限名称：" + command.getName() + " 已经存在。");
        } catch (IdentifierIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加权限失败，权限标识：" + command.getIdentifier() + " 已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更改权限失败。");
        }

    }

    @Override
    public InvokeResult changePermissionProps(ChangePermissionPropsCommand command) {
        try {
            Permission permission = securityAccessApplication.getPermissionBy(command.getId());
            securityConfigApplication.changeNameOfPermission(permission, command.getName());
            permission.setDescription(command.getDescription());
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("权限名称或者标识不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加权限失败，权限名称：" + command.getName() + " 已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更改权限失败。");
        }
    }

    @Override
    public InvokeResult terminatePermissions(Long[] permissionIds) {
        InvokeResult result = null;
        for (Long permissionId : permissionIds) {
            result = this.terminatePermission(permissionId);
            if (!result.isSuccess()) {
                break;
            }
        }
        return result;
    }

    public InvokeResult terminatePermission(Long permissionId) {
        try {
            Permission permission = securityAccessApplication.getPermissionBy(permissionId);
            securityConfigApplication.terminateAuthority(permission);
            return InvokeResult.success();
        } catch (CorrelationException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("权限有用户或者角色关联，不能撤销。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("撤销权限失败。");
        }
    }

    @Override
    public InvokeResult createPageElementResource(CreatePageElementResourceCommand command) {
        try {
            PageElementResource pageElementResource = PageElementResourceAssembler.toPageElementResource(command);
            securityConfigApplication.createSecurityResource(pageElementResource);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("名称和标识不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("页面元素权限资源名称" + command.getName() + "已经存在");
        } catch (IdentifierIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("页面元素权限资源标识" + command.getIdentifier() + "已经存在");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加页面元素权限资源失败。");
        }
    }

    @Override
    public InvokeResult changePageElementResourceProps(ChangePageElementResourcePropsCommand command) {
        try {
            PageElementResource pageElementResource = securityAccessApplication.getPageElementResourceBy(command.getId());
            securityConfigApplication.changeNameOfPageElementResouce(pageElementResource, command.getName());
            pageElementResource.setDescription(command.getDescription());
            securityConfigApplication.createSecurityResource(pageElementResource);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("名称和标识不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("页面元素权限资源名称" + command.getName() + "已经存在");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更改页面元素权限资源失败。");
        }
    }

    @Override
    public InvokeResult createMenuResource(CreateMenuResourceCommand command) {
        try {
            MenuResource menuResource = MenuResourceAssembler.toMenuResource(command);
            securityConfigApplication.createSecurityResource(menuResource);
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加菜单权限资源名称不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加菜单权限资源名称" + command.getName() + "已经存在");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加菜单权限资源名称" + command.getName() + "已经存在");
        }
    }

    @Override
    public InvokeResult createChildMenuResouceToParent(CreateChildMenuResourceCommand command) {
        try {
            MenuResource menuResource = MenuResourceAssembler.toMenuResource(command);
            securityConfigApplication.createChildToParent(menuResource, command.getParentId());
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加菜单权限资源名称不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加菜单权限资源名称" + command.getName() + "已经存在");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("添加子菜单权限资源失败");
        }
    }

    @Override
    public InvokeResult changeMenuResourceProps(ChangeMenuResourcePropsCommand command) {
        try {
            MenuResource menuResource = securityAccessApplication.getMenuResourceBy(command.getId());
            securityConfigApplication.changeNameOfMenuResource(menuResource, command.getName());
            menuResource.setUrl(command.getUrl());
            menuResource.setMenuIcon(command.getMenuIcon());
            menuResource.setDescription(command.getDescription());
            return InvokeResult.success();
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("菜单权限资源名称不能为空。");
        } catch (NameIsExistedException e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更改菜单权限资源名称" + command.getName() + "已经存在。");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return InvokeResult.failure("更改菜单权限资源失败。");
        }
    }
}
