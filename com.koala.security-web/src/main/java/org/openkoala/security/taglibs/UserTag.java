package org.openkoala.security.taglibs;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.openkoala.security.shiro.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 大部分都直接是使用shiro中的代码。
 * property这里可以看做是{@link org.openkoala.security.shiro.realm.ShiroUser}。
 *
 * @author lucas
 */
public class UserTag extends AbstractAuthorizationTag {

    private static final long serialVersionUID = 1012006811404857600L;

    private static final Logger log = LoggerFactory.getLogger(UserTag.class);

    /**
     * The type of principal to be retrieved, or null if the default principal should be used.
     */
    private String type;

    /**
     * The property identifier to retrieve of the principal, or null if the <tt>toString()</tt> value should be used.
     */
    private String property;

    /**
     * The default value that should be displayed if the user is not authenticated, or no principal is found.
     */
    private String defaultValue;

    @SuppressWarnings({"unchecked"})
    public int onDoStartTag() throws JspException {
        String strValue = null;

        if (CurrentUser.getSubject() != null) {

            // Get the principal to print out
            Object principal;

            if (type == null) {
                principal = CurrentUser.getSubject().getPrincipal();
            } else {
                principal = getPrincipalFromClassName();
            }

            // Get the string value of the principal
            if (principal != null) {
                if (property == null) {
                    strValue = principal.toString();
                } else {
                    strValue = getPrincipalProperty(principal, property);
                }
            }

        }

        // Print out the principal value if not null
        if (strValue != null) {
            try {
                pageContext.getOut().write(strValue);
            } catch (IOException e) {
                throw new JspTagException("Error writing [" + strValue + "] to JSP.", e);
            }
        }

        return SKIP_BODY;
    }

    @SuppressWarnings({"unchecked"})
    private Object getPrincipalFromClassName() {
        Object principal = null;

        try {
            Class<?> cls = Class.forName(type);
            principal = CurrentUser.getSubject().getPrincipals().oneByType(cls);
        } catch (ClassNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to find class for identifier [" + type + "]");
            }
        }
        return principal;
    }


    private String getPrincipalProperty(Object principal, String property) throws JspTagException {
        String strValue = null;

        try {
            BeanInfo bi = Introspector.getBeanInfo(principal.getClass());

            // Loop through the properties to get the string value of the specified property
            boolean foundProperty = false;
            for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                if (pd.getName().equals(property)) {
                    Object value = pd.getReadMethod().invoke(principal, (Object[]) null);
                    strValue = String.valueOf(value);
                    foundProperty = true;
                    break;
                }
            }

            if (!foundProperty) {
                final String message = "Property [" + property + "] not found in principal of type [" + principal.getClass().getName() + "]";
                if (log.isErrorEnabled()) {
                    log.error(message);
                }
                throw new JspTagException(message);
            }

        } catch (Exception e) {
            final String message = "Error reading property [" + property + "] from principal of type [" + principal.getClass().getName() + "]";
            if (log.isErrorEnabled()) {
                log.error(message, e);
            }
            throw new JspTagException(message, e);
        }

        return strValue;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getProperty() {
        return property;
    }


    public void setProperty(String property) {
        this.property = property;
    }


    public String getDefaultValue() {
        return defaultValue;
    }


    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
