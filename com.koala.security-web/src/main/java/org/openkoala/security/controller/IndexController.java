package org.openkoala.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping
@SuppressWarnings("unused")
public class IndexController {

	@RequestMapping(value="/index",method = RequestMethod.GET)
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/unauthorized")
	public String unauthorized() {
		return "/errors/unauthorized";
	}
}
