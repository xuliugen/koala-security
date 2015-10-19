package org.openkoala.security.taglibs;

import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import java.util.Collection;
import java.util.Set;

/**
 * 角色标签，支持与或非（and or not）。
 * 不支持复杂的与或非嵌套使用。
 * 具体的实现交给了{@link org.openkoala.security.taglibs.Authz}。
 * @author lucas
 */
public class RoleTag extends AbstractAuthorizationTag {

    private static final long serialVersionUID = -2908081619697933536L;

    protected Authz authz = null;

    protected String ifAnyRoles = null;

    protected String ifAllRoles = null;

    protected String ifNotRoles = null;


    private boolean hasTextAllRole = false;

    private boolean hasTextAnyRole = false;

    private boolean hasTextNotRole = false;


    /**
     * 验证属性，hasTextAllRole、hasTextAnyRole和hasTextNotRole中必须有一个不为空。
     * @throws JspException 如果hasTextAllRole、hasTextAnyRole和hasTextNotRole属性值都为空，将抛出异常
     */
    @Override
    protected void verifyAttributes() throws JspException {

        hasTextAllRole = StringUtils.hasText(getIfAllRoles());
        hasTextAnyRole = StringUtils.hasText(getIfAnyRoles());
        hasTextNotRole = StringUtils.hasText(getIfNotRoles());

        if ((!hasTextAllRole) && (!hasTextAnyRole) && (!hasTextNotRole)) {
            String msg = "The 'ifAnyRoles' or 'ifNotRoles' or 'ifAllRoles' must be set the another!";
            throw new JspException(msg);
        }
    }

    @Override
    public int onDoStartTag() throws JspException {

        if (authz == null) {
            authz = new AuthzImpl();
        }

        if (hasTextAllRole) {
            final Collection<String> requiredRoles = splitAuthorities(getIfAllRoles());
            if (authz.ifAllRole(requiredRoles)) {
                return EVAL_BODY_INCLUDE;
            }
        }

        if (hasTextAnyRole) {
            final Set<String> expectOneOfRoles = splitAuthorities(getIfAnyRoles());
            if (authz.ifAnyRole(expectOneOfRoles)) {
                return EVAL_BODY_INCLUDE;
            }
        }

        if (hasTextNotRole) {
            final Set<String> expectNoneOfRoles = splitAuthorities(getIfNotRoles());
            if (authz.ifNotRole(expectNoneOfRoles)) {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }

    public String getIfAnyRoles() {
        return ifAnyRoles;
    }

    public void setIfAnyRoles(String ifAnyRole) {
        this.ifAnyRoles = ifAnyRole;
    }

    public String getIfAllRoles() {
        return ifAllRoles;
    }

    public void setIfAllRoles(String ifAllRole) {
        this.ifAllRoles = ifAllRole;
    }

    public String getIfNotRoles() {
        return ifNotRoles;
    }

    public void setIfNotRoles(String ifNotRole) {
        this.ifNotRoles = ifNotRole;
    }

}
