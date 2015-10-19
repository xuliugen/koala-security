package org.openkoala.security.taglibs;

import org.openkoala.security.facade.SecurityConfigFacade;
import org.openkoala.security.shiro.CurrentUser;
import org.openkoala.security.shiro.realm.ShiroUser;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Collection;

public class AuthzImpl implements Authz {

    private ApplicationContext applicationContext;

    private SecurityConfigFacade securityConfigFacade;

    private ServletContext servletContext;

    public ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        }
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getPrincipal() {
        return CurrentUser.getPrincipal();
    }

    @Override
    public boolean ifAllRole(Collection<String> roles) {
        return CurrentUser.getSubject().hasAllRoles(roles);
    }

    @Override
    public boolean ifAnyRole(Collection<String> roles) {
        for (String role : roles) {
            if (CurrentUser.getSubject().hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean ifNotRole(Collection<String> roles) {
        return !ifAnyRole(roles);
    }

    @Override
    public boolean ifAllPermission(Collection<String> permissions) {
        return CurrentUser.getSubject().isPermittedAll(permissions.toString());
    }

    @Override
    public boolean ifAnyPermission(Collection<String> permissions) {
        for (String permission : permissions) {
            if (CurrentUser.getSubject().isPermitted(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean ifNotPermission(Collection<String> permissions) {
        return !ifAnyPermission(permissions);
    }

    @Override
    public boolean hasSecurityResource(String securityResourceIdentifier) {
        ShiroUser shiroUser = (ShiroUser) getPrincipal();
        if (shiroUser == null) {
            return false;
        }

        String userAccount = shiroUser.getUserAccount();
        String currentRoleName = shiroUser.getRoleName();

        if (securityConfigFacade == null) {
            securityConfigFacade = getApplicationContext().getBean(SecurityConfigFacade.class);
        }

        return securityConfigFacade.checkUserHasPageElementResource(userAccount, currentRoleName, securityResourceIdentifier);

    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
