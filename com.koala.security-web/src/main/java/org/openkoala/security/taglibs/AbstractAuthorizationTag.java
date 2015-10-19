package org.openkoala.security.taglibs;

import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashSet;
import java.util.Set;

/**
 * 所有标签的基类，自定义标签应该继承该类。
 * 验证标签属性值，以及对标签开始进行处理，可以不实现。
 * 如果多个值就使用<code>,</code>进行分割。
 * @author lucas
 */
public abstract class AbstractAuthorizationTag extends TagSupport {

    private static final long serialVersionUID = 4069161514316265327L;

    private static final String DEFAULT_NAMES_DELIMETER = ",";

    /**
     * 验证属性，交给子类实现，可以不实现。
     * @throws JspException 发生任何错误将抛出异常
     */
    protected void verifyAttributes() throws JspException {
    }

    /**
     * 标签开始时做处理。
     * @return 详细请参照 {@link javax.servlet.jsp.tagext.Tag}
     * @throws JspException 发生任何错误将抛出异常
     */
    public abstract int onDoStartTag() throws JspException;

    @Override
    public int doStartTag() throws JspException {

        verifyAttributes();

        return onDoStartTag();
    }

    /**
     * 标签中的属性值可以使多个，以英文逗号分割，转换为不重复的字符串集合。
     * @param authorities 以英文逗号分割的字符串。
     * @return 不重复的字符串集合
     */
    protected Set<String> splitAuthorities(String authorities) {
        String[] authorityArray = StringUtils.tokenizeToStringArray(authorities, DEFAULT_NAMES_DELIMETER);
        Set<String> results = new HashSet<String>(authorityArray.length);
        for (String authority : authorityArray) {
            results.add(authority);
        }
        return results;
    }

}
