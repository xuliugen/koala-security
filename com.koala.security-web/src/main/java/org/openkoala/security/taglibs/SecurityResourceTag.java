package org.openkoala.security.taglibs;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import org.springframework.util.StringUtils;

/**
 * 资源标签。
 * 直接判断用户是否拥有该资源，如果拥有该资源就显示，如果没有该资源，讲跳过（即不显示）。
 * 具体实现也是交给{@link org.openkoala.security.taglibs.Authz}。
 *
 * @author lucas
 */
public class SecurityResourceTag extends AbstractAuthorizationTag {

    private static final long serialVersionUID = 8833958170778849746L;

    protected Authz authz = null;

    /**
     * 资源标识
     */
    protected String identifier;

    private boolean hasTextIdentifier = false;

    @Override
    protected void verifyAttributes() throws JspException {
        hasTextIdentifier = StringUtils.hasText(identifier);
        if (!hasTextIdentifier) {
            throw new JspException("The 'identifier' must be set!");
        }
    }

    @Override
    public int onDoStartTag() throws JspException {
        if (hasTextIdentifier) {
            initAuthz();
            if (authz.hasSecurityResource(identifier)) {
                return EVAL_BODY_INCLUDE;
            }
        }
        return SKIP_BODY;
    }

    private void initAuthz() {
        if (authz == null) {
            authz = new AuthzImpl();
            initServletContext();
        }
        if (authz.getServletContext() == null) {
            initServletContext();
        }
    }

    private void initServletContext() {
        ServletContext servletContext = this.pageContext.getServletContext();
        authz.setServletContext(servletContext);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isHasTextIdentifier() {
        return hasTextIdentifier;
    }

    public void setHasTextIdentifier(boolean hasTextIdentifier) {
        this.hasTextIdentifier = hasTextIdentifier;
    }

}
