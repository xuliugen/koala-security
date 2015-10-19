package org.openkoala.security.controller;

import javax.inject.Inject;

import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.SecurityConfigFacade;
import org.openkoala.security.facade.command.ChangeUrlAccessResourcePropsCommand;
import org.openkoala.security.facade.command.CreateUrlAccessResourceCommand;
import org.openkoala.security.facade.dto.PermissionDTO;
import org.openkoala.security.facade.dto.UrlAccessResourceDTO;
import org.openkoala.security.shiro.extend.ShiroFilterChainManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * URL访问资源控制器。
 * 分页都将采用POST请求方式，因GET请求搜索时携带中文会导致乱码。
 *
 * @author lucas
 */
@Controller
@RequestMapping("/auth/url")
@SuppressWarnings("unused")
public class UrlAccessController {

    @Inject
    private SecurityConfigFacade securityConfigFacade;

    @Inject
    private SecurityAccessFacade securityAccessFacade;

    @Inject
    private ShiroFilterChainManager shiroFilterChainManager;

    /**
     * 添加URL访问权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public InvokeResult add(CreateUrlAccessResourceCommand command) {
        return securityConfigFacade.createUrlAccessResource(command);
    }

    /**
     * 更新URL访问权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public InvokeResult update(ChangeUrlAccessResourcePropsCommand command) {
        return securityConfigFacade.changeUrlAccessResourceProps(command);
    }

    /**
     * 撤销URL访问权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/terminate", method = RequestMethod.POST)
    public InvokeResult terminate(Long[] urlAccessResourceIds) {
        shiroFilterChainManager.initFilterChain();
        return securityConfigFacade.terminateUrlAccessResources(urlAccessResourceIds);
    }

    /**
     * 根据条件分页查询URL访问权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQuery", method = RequestMethod.POST)
    public Page<UrlAccessResourceDTO> pagingQuery(int page, int pagesize, UrlAccessResourceDTO queryUrlAccessResourceCondition) {
        return securityAccessFacade.pagingQueryUrlAccessResources(page, pagesize, queryUrlAccessResourceCondition);
    }

    /**
     * 为URL访问权限资源授权权限Permission
     */
    @ResponseBody
    @RequestMapping(value = "/grantPermisssionsToUrlAccessResource", method = RequestMethod.POST)
    public InvokeResult grantPermisssionsToUrlAccessResource(Long permissionId, Long urlAccessResourceId) {
        shiroFilterChainManager.initFilterChain();
        return securityConfigFacade.grantPermisssionToUrlAccessResource(permissionId, urlAccessResourceId);
    }

    /**
     * 从URL访问权限资源中撤销权限Permission
     */
    @ResponseBody
    @RequestMapping(value = "/terminatePermissionsFromUrlAccessResource", method = RequestMethod.POST)
    public InvokeResult terminatePermissionsFromUrlAccessResource(Long permissionId, Long urlAccessResourceId) {
        shiroFilterChainManager.initFilterChain();
        return securityConfigFacade.terminatePermissionFromUrlAccessResource(permissionId, urlAccessResourceId);
    }

    /**
     * 通过URL访问权限资源分页查询已经授权的权限。
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQueryGrantPermissionsByUrlAccessResourceId", method = RequestMethod.POST)
    public Page<PermissionDTO> pagingQueryGrantPermissionsByUrlAccessResourceId(int page, int pagesize,
                                                                                Long urlAccessResourceId) {
        return securityAccessFacade.pagingQueryGrantPermissionsByUrlAccessResourceId(page,
                pagesize, urlAccessResourceId);
    }

    /**
     * 通过URL访问权限资源分页查询还未授权的权限。
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQueryNotGrantPermissionsByUrlAccessResourceId", method = RequestMethod.POST)
    public Page<PermissionDTO> pagingQueryNotGrantPermissionsByUrlAccessResourceId(int page, int pagesize, Long urlAccessResourceId, PermissionDTO queryPermissionCondition) {
        return securityAccessFacade.pagingQueryNotGrantPermissionsByUrlAccessResourceId(page, pagesize, urlAccessResourceId, queryPermissionCondition);
    }

}
