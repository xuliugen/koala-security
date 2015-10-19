package org.openkoala.security.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openkoala.security.shiro.extend.CustomDefaultFilterChainManager;
import org.openkoala.security.shiro.extend.ShiroFilterChainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 初始化Shiro过滤器链。
 *
 * @author lucas
 */
public class SystemEvenmentListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemEvenmentListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
        CustomDefaultFilterChainManager filterChainManager = applicationContext.getBean(CustomDefaultFilterChainManager.class);
        ShiroFilterChainManager shiroFilterChainManager = applicationContext.getBean(ShiroFilterChainManager.class);
        filterChainManager.init();
        shiroFilterChainManager.init();
        shiroFilterChainManager.initFilterChain();
        LOGGER.info("init System Evenment.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
