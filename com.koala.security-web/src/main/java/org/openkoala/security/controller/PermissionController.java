package org.openkoala.security.controller;

import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.SecurityConfigFacade;
import org.openkoala.security.facade.command.ChangePermissionPropsCommand;
import org.openkoala.security.facade.command.CreatePermissionCommand;
import org.openkoala.security.facade.dto.PermissionDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * 权限控制器。
 * 分页都将采用POST请求方式，因GET请求搜索时携带中文会导致乱码。
 * @author lucas
 */
@Controller
@RequestMapping("/auth/permission")
@SuppressWarnings("unused")
public class PermissionController {

    @Inject
    private SecurityAccessFacade securityAccessFacade;

    @Inject
    private SecurityConfigFacade securityConfigFacade;

    /**
     * 添加权限。
     * @param command
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public InvokeResult add(CreatePermissionCommand command) {
        return securityConfigFacade.createPermission(command);
    }

    /**
     * 更新权限
     * @param command
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public InvokeResult update(ChangePermissionPropsCommand command) {
        return securityConfigFacade.changePermissionProps(command);
    }

    /**
     * 撤销权限
     * @param permissionIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/terminate", method = RequestMethod.POST)
    public InvokeResult terminate(Long[] permissionIds) {
        return securityConfigFacade.terminatePermissions(permissionIds);
    }

    /**
     * 根据条件分页查询权限。
     * @param page
     * @param pagesize
     * @param queryPermissionCondition 查询权限条件
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQuery", method = RequestMethod.POST)
    public Page<PermissionDTO> pagingQuery(int page, int pagesize, PermissionDTO queryPermissionCondition) {
        return securityAccessFacade.pagingQueryPermissions(page, pagesize, queryPermissionCondition);
    }

    @ResponseBody
    @RequestMapping(value = "findInfoOfPermission", method = RequestMethod.GET)
    public InvokeResult findInfoOfPermission(Long permissionId) {
        return securityAccessFacade.findInfOfPermission(permissionId);
    }


}
