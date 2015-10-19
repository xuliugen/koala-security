package org.openkoala.security.controller;


import javax.inject.Inject;

import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.SecurityConfigFacade;
import org.openkoala.security.facade.command.ChangeMenuResourcePropsCommand;
import org.openkoala.security.facade.command.CreateChildMenuResourceCommand;
import org.openkoala.security.facade.command.CreateMenuResourceCommand;
import org.openkoala.security.facade.dto.PermissionDTO;
import org.openkoala.security.shiro.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 菜单权限资源控制器。
 * 分页都将采用POST请求方式，因GET请求搜索时携带中文会导致乱码。
 *
 * @author lucas
 */
@Controller
@RequestMapping("/auth/menu")
@SuppressWarnings("unused")
public class MenuResourceController {

    @Inject
    private SecurityAccessFacade securityAccessFacade;

    @Inject
    private SecurityConfigFacade securityConfigFacade;

    /**
     * 添加菜单权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public InvokeResult add(CreateMenuResourceCommand command) {
        return securityConfigFacade.createMenuResource(command);
    }

    /**
     * 选择父菜单权限资源， 为其添加子菜单权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/addChildToParent", method = RequestMethod.POST)
    public InvokeResult addChildToParent(CreateChildMenuResourceCommand command) {
        return securityConfigFacade.createChildMenuResouceToParent(command);
    }

    /**
     * 更新菜单权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public InvokeResult update(ChangeMenuResourcePropsCommand command) {
        return securityConfigFacade.changeMenuResourceProps(command);
    }

    /**
     * 批量撤销菜单 TODO 捕获详细异常。
     */
    @ResponseBody
    @RequestMapping(value = "/terminate", method = RequestMethod.POST)
    public InvokeResult terminate(Long[] menuResourceIds) {
        return securityConfigFacade.terminateMenuResources(menuResourceIds);
    }

    /**
     * 查找菜单树。
     */
    @ResponseBody
    @RequestMapping(value = "/findAllMenusTree", method = RequestMethod.POST)
    public InvokeResult findAllMenusTree() {
        return securityAccessFacade.findAllMenusTree();
    }

    /**
     * 为菜单权限资源资源授予权限Permission。
     */
    @ResponseBody
    @RequestMapping(value = "/grantPermisssionsToMenuResource", method = RequestMethod.POST)
    public InvokeResult grantPermisssionsToMenuResource(Long permissionId, Long menuResourceId) {
        return securityConfigFacade.grantPermisssionToMenuResource(permissionId, menuResourceId);
    }

    /**
     * 从菜单权限资源中撤销权限Permission。
     */
    @ResponseBody
    @RequestMapping(value = "/terminatePermissionsFromMenuResource", method = RequestMethod.POST)
    public InvokeResult terminatePermissionsFromMenuResource(Long permissionId, Long menuResourceId) {
        return securityConfigFacade.terminatePermissionFromMenuResource(permissionId, menuResourceId);
    }

    /**
     * 通过菜单权限资源ID分页查询已经授权的Permission。
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQueryGrantPermissionsByMenuResourceId", method = RequestMethod.POST)
    public Page<PermissionDTO> pagingQueryGrantPermissionsByMenuResourceId(int page, int pagesize, Long menuResourceId) {
        return securityAccessFacade.pagingQueryGrantPermissionsByMenuResourceId(page, pagesize, menuResourceId);
    }

    /**
     * 通过菜单权限资源ID分页查询还未授权的Permission。
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQueryNotGrantPermissionsByMenuResourceId", method = RequestMethod.POST)
    public Page<PermissionDTO> pagingQueryNotGrantPermissionsByMenuResourceId(int page, int pagesize, Long menuResourceId, PermissionDTO queryPermissionCondition) {
        return securityAccessFacade.pagingQueryNotGrantPermissionsByMenuResourceId(page, pagesize, menuResourceId, queryPermissionCondition);
    }
}
