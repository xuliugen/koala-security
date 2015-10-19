package org.openkoala.security.application.impl;

import com.google.common.collect.Sets;
import org.openkoala.security.application.SecurityConfigApplication;
import org.openkoala.security.core.domain.*;

import javax.inject.Named;
import java.util.List;
import java.util.Set;

@Named
public class SecurityConfigApplicationImpl implements SecurityConfigApplication {

    public void createActor(Actor actor) {
        actor.save();
    }

    public void terminateActor(Actor actor) {
        actor.remove();
    }

    public void suspendUser(User user) {
        user.disable();
    }

    public void activateUser(User user) {
        user.enable();
    }

    public void createAuthority(Authority authority) {
        authority.save();
    }

    public void terminateAuthority(Authority authority) {
        authority.remove();
    }

    public void createSecurityResource(SecurityResource securityResource) {
        securityResource.save();
    }

    public void terminateSecurityResource(SecurityResource securityResource) {
        securityResource.remove();
    }

    public void grantRoleToPermission(Role role, Permission permission) {
        role.addPermission(permission);
    }

    public void grantRoleToPermissions(Role role, List<Permission> permissions) {
        role.addPermissions(permissions);
    }

    public void grantRolesToPermission(List<Role> roles, Permission permission) {
        for (Role role : roles) {
            role.addPermission(permission);
        }
    }

    public void grantActorToAuthority(Actor actor, Authority authority) {
        actor.grant(authority);
    }

    public void grantActorToAuthorities(Actor actor, List<Authority> authorities) {
        for (Authority authority : authorities) {
            this.grantActorToAuthority(actor, authority);
        }
    }

    public void grantActorsToAuthority(List<Actor> actors, Authority authority) {
        for (Actor actor : actors) {
            this.grantActorToAuthority(actor, authority);
        }
    }

    public void terminateSecurityResourceFromAuthority(SecurityResource securityResource, Authority authority) {
        authority.terminateSecurityResource(securityResource);
    }

    public void terminateSecurityResourcesFromAuthority(List<? extends SecurityResource> securityResources, Authority authority) {
        authority.terminateSecurityResources(Sets.newHashSet(securityResources));
    }

    public void terminateAuthoritiesFromSecurityResource(List<Authority> authorities, SecurityResource securityResource) {
        for (Authority authority : authorities) {
            terminateAuthorityFromSecurityResource(authority, securityResource);
        }
    }

    public void terminateAuthorityFromSecurityResource(Authority authority, SecurityResource securityResource) {
        authority.terminateSecurityResource(securityResource);
    }

    public void terminatePermissionFromRole(Permission permission, Role role) {
        role.terminatePermission(permission);
    }

    public void terminatePermissionsFromRole(List<Permission> permissions, Role role) {
        role.terminatePermissions(permissions);
    }

    public void terminateRolesFromPermission(List<Role> roles, Permission permission) {
        for (Role role : roles) {
            terminateRoleFromPermission(role, permission);
        }
    }

    private void terminateRoleFromPermission(Role role, Permission permission) {
        role.terminatePermission(permission);
    }

    public void terminateActorFromAuthority(Actor actor, Authority authority) {
        actor.terminate(authority);
    }

    public void terminateAuthoritiesFromActor(List<Authority> authorities, Actor actor) {
        for (Authority authority : authorities) {
            this.terminateActorFromAuthority(actor, authority);
        }
    }

    public void createScope(Scope scope) {
        scope.save();
    }

    public void grantActorToAuthorityInScope(Actor actor, Authority authority, Scope scope) {
        actor.grant(authority, scope);
    }

    @Override
    public void grantAuthorityToSecurityResource(Authority authority, SecurityResource securityResource) {
        authority.addSecurityResource(securityResource);
    }

    @Override
    public void grantAuthorityToSecurityResources(Authority authority, List<? extends SecurityResource> securityResources) {
        authority.addSecurityResources(securityResources);
    }

    @Override
    public void resetPassword(User user) {
        user.resetPassword();
    }

    @Override
    public void createChildToParent(MenuResource child, Long parentId) {
        MenuResource parent = MenuResource.get(MenuResource.class, parentId);
        parent.addChild(child);
    }

    @Override
    public void grantSecurityResourcesToAuthority(List<? extends SecurityResource> securityResources, Authority authority) {
        authority.addSecurityResources(securityResources);
    }

    @Override
    public void grantPermissionToRole(Permission permission, Role role) {
        role.addPermission(permission);
    }

    @Override
    public void grantSecurityResourceToAuthority(SecurityResource securityResource, Authority authority) {
        authority.addSecurityResource(securityResource);
    }

    @Override
    public void grantPermissionsToRole(List<Permission> permissions, Role role) {
        role.addPermissions(permissions);
    }

    @Override
    public boolean checkAuthoritiHasPageElementResource(Set<Authority> authorities, String identifier) {
        return Authority.checkHasPageElementResource(authorities, identifier);
    }

    @Override
    public void grantAuthorityToActor(Authority authority, Actor actor) {
        actor.grant(authority);
    }

    @Override
    public void changeUserAccount(User user, String userAccount, String userPassword) {
        user.changeUserAccount(userAccount, userPassword);
    }

    @Override
    public void changeUserEmail(User user, String email, String userPassword) {
        user.changeEmail(email, userPassword);
    }

    @Override
    public void changeUserTelePhone(User user, String telePhone, String userPassword) {
        user.changeTelePhone(telePhone, userPassword);
    }

    @Override
    public void changeNameOfUrlAccessResource(UrlAccessResource urlAccessResource, String name) {
        urlAccessResource.changeName(name);
    }

    @Override
    public void changeUrlOfUrlAccessResource(UrlAccessResource urlAccessResource, String url) {
        urlAccessResource.changeUrl(url);
    }

    @Override
    public void changeNameOfRole(Role role, String name) {
        role.changeName(name);
    }

    @Override
    public void changeNameOfPermission(Permission permission, String name) {
        permission.changeName(name);
    }

    @Override
    public void changeIdentifierOfPermission(Permission permission, String identifier) {
        permission.changeIdentifier(identifier);
    }

    @Override
    public void changeNameOfPageElementResouce(PageElementResource pageElementResource, String name) {
        pageElementResource.changeName(name);
    }

    @Override
    public void changeIdentifierOfPageElementResouce(PageElementResource pageElementResource, String identifier) {
        pageElementResource.changeIdentifier(identifier);
    }

    @Override
    public void changeNameOfMenuResource(MenuResource menuResource, String name) {
        menuResource.changeName(name);
    }

    @Override
    public void terminateActorFromAuthorityInScope(Actor actor, Authority authority, Scope scope) {
        actor.terminateAuthorityInScope(authority, scope);
    }

    @Override
    public void changeLastModifyTimeOfUser(User user) {
        user.changeLastModifyTime();
    }

}
