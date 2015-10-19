package org.openkoala.security.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openkoala.security.facade.SecurityConfigFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 权限初始化监听器。
 * 
 * @author luzhao
 * 
 */
public class SecurityDBInitListener implements ServletContextListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityDBInitListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
		SecurityConfigFacade securityConfigFacade = applicationContext.getBean(SecurityConfigFacade.class);
		securityConfigFacade.initSecuritySystem();
		LOGGER.info("init Security db.");
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
}
