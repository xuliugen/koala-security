package org.openkoala.security.shiro.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

/**
 * 角色授权过滤器，Shiro自带的角色授权过滤器无法满足需求（即{@link RolesAuthorizationFilter}验证的是用户拥有的所有角色，却没有用户拥有任意一个角色。），
 * 因此重写Shiro的{@link RolesAuthorizationFilter}。
 *
 * @author lucas
 */
public class AnyRoleAuthorizationFilter extends RolesAuthorizationFilter {

    /**
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws IOException 如果发生任何错误就抛出异常
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        Subject subject = getSubject(request, response);

        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            // no roles specified, so nothing to check - allow access.
            return true;
        }

        Set<String> roles = CollectionUtils.asSet(rolesArray);
        for (String role : roles) {
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
