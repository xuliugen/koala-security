package org.openkoala.security.controller;

import org.apache.shiro.SecurityUtils;
import org.openkoala.koala.commons.InvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SuppressWarnings("unused")
public class LogoutController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);

	/**
	 * 用户退出。
	 * 
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public InvokeResult logout() {
        try {
            SecurityUtils.getSubject().logout();
            return InvokeResult.success();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return InvokeResult.success("用户退出失败。");
        }
    }
}
