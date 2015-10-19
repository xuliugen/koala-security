package org.openkoala.security.shiro.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomFormAuthenticationFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        LOGGER.info("inito CustomFormAuthenticationFilter onAccessDenied");
        if (request.getAttribute(getFailureKeyAttribute()) != null) {
            return true;
        }

        return super.onAccessDenied(request, response);
    }
}
