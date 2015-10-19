package org.openkoala.security.controller;

import org.apache.commons.lang3.StringUtils;
import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.SecurityConfigFacade;
import org.openkoala.security.facade.command.ChangeUserEmailCommand;
import org.openkoala.security.facade.command.ChangeUserPasswordCommand;
import org.openkoala.security.facade.command.ChangeUserTelePhoneCommand;
import org.openkoala.security.facade.dto.RoleDTO;
import org.openkoala.security.shiro.CurrentUser;
import org.openkoala.security.shiro.RoleHandle;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

/**
 * 当前用户控制器。
 * 分页都将采用POST请求方式，因GET请求搜索时携带中文会导致乱码。
 * @author lucas
 */
@Controller
@RequestMapping("/auth/currentUser")
@SuppressWarnings("unused")
public class CurrentUserController {

    @Inject
    private SecurityConfigFacade securityConfigFacade;

    @Inject
    private SecurityAccessFacade securityAccessFacade;

    @Inject
    private RoleHandle roleHandle;

    /**
     * 更改用户邮箱。
     */
    @ResponseBody
    @RequestMapping(value = "/changeUserEmail", method = RequestMethod.POST)
    public InvokeResult changeUserEmail(ChangeUserEmailCommand command) {
        command.setUserAccount(CurrentUser.getUserAccount());
        InvokeResult result = securityConfigFacade.changeUserEmail(command);
        if (result.isSuccess()) {
            CurrentUser.getPrincipal().setEmail(command.getEmail());
        }
        return result;
    }

    /**
     * 更改用户联系电话。
     */
    @ResponseBody
    @RequestMapping(value = "/changeUserTelePhone", method = RequestMethod.POST)
    public InvokeResult changeUserTelePhone(ChangeUserTelePhoneCommand command) {
        command.setUserAccount(CurrentUser.getUserAccount());
        InvokeResult result = securityConfigFacade.changeUserTelePhone(command);
        if (result.isSuccess()) {
            CurrentUser.getPrincipal().setTelePhone(command.getTelePhone());
        }
        return result;
    }

    /**
     * 更新用户密码。
     */
    @ResponseBody
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public InvokeResult changeUserPassword(ChangeUserPasswordCommand command) {
        command.setUserAccount(CurrentUser.getUserAccount());
        return securityConfigFacade.changeUserPassword(command);
    }

    /**
     * 分页查询用户的角色
     */
    @ResponseBody
    @RequestMapping(value = "/pagingQueryRolesOfUser", method = RequestMethod.POST)
    public Page<RoleDTO> pagingQueryRolesOfUser(int page, int pagesize) {
        String userAccount = CurrentUser.getUserAccount();
        return securityAccessFacade.pagingQueryRolesOfUser(page, pagesize, userAccount);
    }

    /**
     * 切换角色。
     * 角色名称不能为空.
     * 如果是相当角色就不做任何处理。
     */
    @ResponseBody
    @RequestMapping(value = "/switchOverRoleOfUser", method = RequestMethod.POST)
    public InvokeResult switchOverRoleOfUser(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            return InvokeResult.failure("角色名为空。");
        }
        if (CurrentUser.getRoleName().equals(roleName)) {
            return InvokeResult.success();
        }
        if (!securityAccessFacade.checkRoleByName(roleName)) {
            return InvokeResult.failure("角色名不存在。");
        }
        if (!securityAccessFacade.checkUserIsHaveRole(CurrentUser.getUserAccount(), roleName)) {
            return InvokeResult.failure("该角色未分配当前用户");
        }
        try {
            roleHandle.switchOverRoleOfUser(roleName);
            return InvokeResult.success();
        } catch (Exception e) {
            return InvokeResult.failure("角色切换失败。");
        }
    }

    /**
     * 根据用户账户查询用户详细
     */
    @ResponseBody
    @RequestMapping(value = "/getUserDetail", method = RequestMethod.GET)
    public InvokeResult getUserDetail() {
        String userAccount = CurrentUser.getUserAccount();
        return securityAccessFacade.getuserDetail(userAccount);
    }

    /**
     * 查找用户在某个角色下得所有菜单权限资源。
     */
    @ResponseBody
    @RequestMapping(value = "/findAllMenusByUserAsRole", method = RequestMethod.GET)
    public InvokeResult findAllMenusByUserAsRole() {
        return securityAccessFacade.findMenuResourceByUserAsRole(CurrentUser.getUserAccount(), CurrentUser.getRoleName());
    }
}
