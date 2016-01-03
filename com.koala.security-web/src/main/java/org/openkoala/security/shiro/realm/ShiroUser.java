package org.openkoala.security.shiro.realm;

import com.google.common.collect.Sets;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.domain.InstanceFactory;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.dto.PermissionDTO;
import org.openkoala.security.facade.dto.RoleDTO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shiro用户，主要是让Shiro能够支持多字段的方式登录系统。 目前实现账号、联系电话、和邮箱三种登录方式。 如果还不能满足需求可以扩展该类。
 * @author lucas
 */
public class ShiroUser implements Serializable {

    private static final long serialVersionUID = 573154901435223916L;

    private SecurityAccessFacade securityAccessFacade;

    private String userAccount;

    private String name;

    private String roleName;

    private String email;

    private String telePhone;

    public ShiroUser(String userAccount, String name) {
        super();
        this.userAccount = userAccount;
        this.name = name;
        this.roleName = getRoleNameByUserAccount();
    }

    public Set<String> getRoles() {
        return Sets.newHashSet(roleName);
    }

    public Set<String> getPermissions() {
        Set<String> results = new HashSet<String>();
        Set<PermissionDTO> permissions = getSecurityAccessFacade().findPermissionsByUserAccountAndRoleName(userAccount, roleName);
        for (PermissionDTO permission : permissions) {
            results.add(permission.getIdentifier());
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public String getRoleNameByUserAccount() {
        InvokeResult result = getSecurityAccessFacade().findRolesByUserAccount(userAccount);
        if (result.isSuccess()) {
            List<RoleDTO> roles = (List<RoleDTO>) result.getData();
            if (!roles.isEmpty()) {
                return roles.get(0).getName();
            }
            return "暂未分配角色";
        }
        return "暂未分配角色";
    }

    public String getUserAccount() {
        return userAccount;
    }

    public String getName() {
        return name;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelePhone() {
        return telePhone;
    }

    public void setTelePhone(String telePhone) {
        this.telePhone = telePhone;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getUserAccount()).append(getName()).append(getRoleName()).append(getTelePhone()).append(getEmail()).build();
    }

    public SecurityAccessFacade getSecurityAccessFacade() {
        if (securityAccessFacade == null) {
            securityAccessFacade = InstanceFactory.getInstance(SecurityAccessFacade.class);
        }
        return securityAccessFacade;
    }

}
