package org.openkoala.security.shiro.jcaptcha;

import com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 验证码过滤器，继承{@link AccessControlFilter}，AccessControlFilter提供了访问控制的基本功能：是否允许访问，当访问拒绝的时候如何处理。
 * 用户登录功能，很多时候都需要验证码支持，验证码常用目的就是为了防止机器人模拟真实用户登录而恶意访问，
 * 如暴力破解（因此服务端返回的登录提示信息比较模糊，而不是比较详细的信息）。
 * 采用JCaptcha开源的Java类库生成验证码图片。
 * @author lucas
 */
public class JCaptchaValidateFilter extends AccessControlFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JCaptchaValidateFilter.class);

    /**
     * 默认情况下验证码可用，可以通过spring bean 的方式进行更改。
     */
    private boolean jCaptchaDisabled = false;

    /**
     * 前端输入的验证码
     */
    private String jCaptchaCode = "jCaptchaCode";

    /**
     * 验证失败
     */
    private String failureKeyAttribute = "shiroLoginFailure";

    /**
     * 是否允许访问
     * 请求方式，登录的时候会有两个请求，一个是GET请求返回登录页面(这个就不需要走验证码过滤器)，一个是POST请求用于用户登录（需要验证码过滤器）。
     * 如果jCaptchaDisabled是false或者请求方式是POST就验证输入的验证码是否正确。
     * 如果jCaptchaDisabled是true或者请求不是POST那么就不需要验证码，直接允许通过。
     * @param request     进来的是<code>ServletRequest</code>
     * @param response    离开的是<code>ServletResponse</code>
     * @param mappedValue [urls]配置中拦截器参数部分，其实就是每一次请求的URL所对应的角色或者权限
     * @return 返回的是boolean，true 表示允许访问，false 表示不能访问
     * @throws Exception 如果发生任何错误将抛出异常
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        LOGGER.info("into JCaptchaValidateFilter isAccessAllowed");
        request.setAttribute("jCaptchaDisabled", jCaptchaDisabled);
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        if (jCaptchaDisabled || !POST_METHOD.equalsIgnoreCase(httpServletRequest.getMethod())) {
            return true;
        }
        String jCaptchaCodeParamter = httpServletRequest.getParameter(jCaptchaCode);

        if (httpServletRequest.getSession(false) == null) {
            return false;
        }

        return SimpleImageCaptchaServlet.validateResponse(httpServletRequest, jCaptchaCodeParamter);
    }

    /**
     * 访问拒绝时是否已经处理了，如果返回<code> true </code>表示需要继续处理; 如果返回<code>false </code>表示该拦截器实例已经处理了,将直接返回即可。
     * 目前是把验证失败failureKeyAttribute放入request请求中，用于后面继续处理。因此返回值一直都是<code> true </code>。
     * @param request  进来的是<code>ServletRequest</code>
     * @param response 离开的是<code>ServletResponse</code>
     * @return 返回<code> true </code>
     * @throws Exception 如果发生任何错误将抛出异常
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        request.setAttribute(failureKeyAttribute, "jCaptchaCode.error");
        return true;
    }

	/*------------- getter setter methods  -----------------*/

    public boolean isjCaptchaDisabled() {
        return jCaptchaDisabled;
    }

    public void setjCaptchaDisabled(boolean jCaptchaDisabled) {
        this.jCaptchaDisabled = jCaptchaDisabled;
    }

    public String getjCaptchaCode() {
        return jCaptchaCode;
    }

    public void setjCaptchaCode(String jCaptchaCode) {
        this.jCaptchaCode = jCaptchaCode;
    }

    public String getFailureKeyAttribute() {
        return failureKeyAttribute;
    }

    public void setFailureKeyAttribute(String jCaptchaFailure) {
        this.failureKeyAttribute = jCaptchaFailure;
    }

}
