package org.openkoala.security.application.impl;

import org.openkoala.security.application.SecurityAccessApplication;
import org.openkoala.security.core.domain.*;

import javax.inject.Named;
import java.util.List;

@Named
public class SecurityAccessApplicationImpl implements SecurityAccessApplication {

    public List<Role> findAllRolesByUserAccount(String userAccount) {
        return User.findAllRolesBy(userAccount);
    }

    public User getUserById(Long userId) {
        return Actor.get(User.class, userId);
    }

    @Override
    public <T extends Actor> T getActorById(Long actorId) {
        return Actor.getActorBy(actorId);
    }

    public User getUserByUserAccount(String userAccount) {
        return User.getByUserAccount(userAccount);
    }

    public List<MenuResource> findMenuResourceByUserAccount(String userAccount) {
        return Authority.findMenuResourceByAuthorities(
                Authorization.findAuthoritiesByActor(
                        getUserByUserAccount(userAccount)));
    }

    @Override
    public boolean updatePassword(User user, String userPassword, String oldUserPassword) {
        return user.updatePassword(userPassword, oldUserPassword);
    }

    @Override
    public Role getRoleBy(Long roleId) {
        return Role.get(Role.class, roleId);
    }

    @Override
    public Permission getPermissionBy(Long permissionId) {
        return Permission.get(Permission.class, permissionId);
    }

    @Override
    public MenuResource getMenuResourceBy(Long menuResourceId) {
        return MenuResource.get(MenuResource.class, menuResourceId);
    }

    @Override
    public List<MenuResource> findAllMenuResourcesByRole(Role role) {
        return role.findMenuResourceByAuthority();
    }

    @Override
    public UrlAccessResource getUrlAccessResourceBy(Long urlAccessResourceId) {
        return UrlAccessResource.getBy(urlAccessResourceId);
    }

    @Override
    public PageElementResource getPageElementResourceBy(Long pageElementResourceId) {
        return PageElementResource.getBy(pageElementResourceId);
    }

    @Override
    public Role getRoleBy(String roleName) {
        return Role.getRoleBy(roleName);
    }

    @Override
    public <T extends Scope> T getScope(Long scopeId) {
        return Scope.getBy(scopeId);
    }

    @Override
    public boolean hasPageElementResource(String identifier) {
        return PageElementResource.hasIdentifier(identifier);
    }

    @Override
    public User getUserByEmail(String email) {
        return User.getByEmail(email);
    }

    @Override
    public User getUserByTelePhone(String telePhone) {
        return User.getByTelePhone(telePhone);
    }

    @Override
    public List<MenuResource> findAllMenuResourcesByIds(Long[] menuResourceIds) {
        return MenuResource.findAllByIds(menuResourceIds);
    }

    @Override
    public boolean checkRoleByName(String roleName) {
        return Role.checkName(roleName);
    }

    @Override
    public <T extends Authority> T getAuthority(Long authorityId) {
        return Authority.getBy(authorityId);
    }

    @Override
    public List<Role> findRolesOfUser(User user) {
        return user.findAllRoles();
    }

    @Override
    public List<Permission> findPermissionsOfUser(User user) {
        return user.findAllPermissions();
    }

    @Override
    public List<MenuResource> findMenuResourcesOfRole(Role role) {
        return role.findMenuResources();
    }

    @Override
    public List<UrlAccessResource> findUrlAccessResourcesOfRole(Role role) {
        return role.findUrlAccessResources();
    }

    @Override
    public List<PageElementResource> findPageElementResourcesOfRole(Role role) {
        return role.findPageElementResources();
    }

    @Override
    public List<SecurityResource> findResourcesByPermission(Permission permission) {
        return permission.findResources();
    }

    @Override
    public boolean hasUserExisted() {
        return User.hasUserExisted();
    }
}
