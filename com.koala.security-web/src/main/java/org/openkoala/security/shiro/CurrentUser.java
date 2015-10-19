package org.openkoala.security.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.openkoala.security.shiro.realm.ShiroUser;

/**
 * 当前用户，能够直接获取当前用户，也能够直接获取当前用户的账户，当前用户的角色。
 * @author lucas
 */
public final class CurrentUser {

    /**
     * 获取当事人,即当前登录的用户可以看做是一个当事人。
     * 它是一个系统用户的标志，可以是账号、用户名、联系电话、邮箱等等，也可以自己扩展自己所期望的。
     * @return {@link ShiroUser} 它包含用户名、账号等
     */
    public static ShiroUser getPrincipal() {
        return (ShiroUser) getSubject().getPrincipal();
    }

    /**
     * 获取当前用户的账号。
     * @return 用户的账号
     */
    public static String getUserAccount() {
        return getPrincipal().getUserAccount();
    }

    /**
     * 获取当前用户的角色名称。
     * @return 用户的角色名称
     */
    public static String getRoleName() {
        return getPrincipal().getRoleName();
    }

    /**
     * 获取当前用户的联系电话
     * @return 用户的联系电话
     */
    public static String getTelePhone() {
        return getPrincipal().getTelePhone();
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static PrincipalCollection getPrincipals() {
        return getSubject().getPrincipals();
    }

    public static org.apache.shiro.mgt.SecurityManager getSecurityManager() {
        return SecurityUtils.getSecurityManager();
    }

}
