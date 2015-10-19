package org.openkoala.security.taglibs;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;
import java.util.Collection;

/**
 * 标签库中所有的代码逻辑都在该接口中。
 * 特别说明：用户拥有的权限包括2个（用户直接拥有的权限和用户当前使用的角色所拥有的权限）。
 * @author lucas
 */
public interface Authz {

    /**
     * 得到当前应用的上下文。
     * @return Spring应用上下文 {@link ApplicationContext}
     */
    ApplicationContext getApplicationContext();

    /**
     * 设置当前的应用上下文。
     * @param applicationContext 应用上下文，这里使用的是spring应用上下文。
     */
    void setApplicationContext(ApplicationContext applicationContext);

    /**
     * 得到当前Servlet的上下文。
     * @return {@link ServletContext}
     */
    public ServletContext getServletContext();

    /**
     * 设置当前的Servlet的上下文。
     * @param servletContext
     */
    public void setServletContext(ServletContext servletContext);

    /**
     * 得到当事人，这里可以看做是 {@link org.openkoala.security.shiro.realm.ShiroUser}。
     * @return 返回当事人
     */
    Object getPrincipal();

    /**
     * 如果当前用户拥有所有的角色，就返回<code>true</code>,反之则相反。
     * @param roles 所有的角色集合
     * @return 返回布尔值(boolean)
     */
    boolean ifAllRole(Collection<String> roles);

    /**
     * 如果当前用户拥有其中一个角色，就返回<code>true</code>,反之则相反。
     * @param roles 所有的角色集合
     * @return 返回布尔值(boolean)
     */
    boolean ifAnyRole(Collection<String> roles);

    /**
     * 如果当前用户没有这些角色集合，那么就返回<code>true</code>,反之则相反。
     * @param roles 所有的角色集合
     * @return 返回布尔值(boolean)
     */
    boolean ifNotRole(Collection<String> roles);

    /**
     * 如果当前用户拥有所有的角色，就返回<code>true</code>,反之则相反。
     * @param permissions 所有的权限集合
     * @return 返回布尔值(boolean)
     */
    boolean ifAllPermission(Collection<String> permissions);

    /**
     * 如果当前用户拥有其中一个权限，就返回<code>true</code>,反之则相反。
     * @param permissions 所有的权限集合
     * @return 返回布尔值(boolean)
     */
    boolean ifAnyPermission(Collection<String> permissions);

    /**
     * 如果当前用户没有这些权限集合，就返回<code>true</code>,反之则相反。
     * @param permissions 所有的权限集合
     * @return 返回布尔值(boolean)
     */
    boolean ifNotPermission(Collection<String> permissions);

    boolean hasSecurityResource(String name);

}
