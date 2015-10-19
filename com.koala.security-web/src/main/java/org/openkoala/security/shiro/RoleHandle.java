package org.openkoala.security.shiro;

public interface RoleHandle {

    void switchOverRoleOfUser(String roleName);

    void resetRoleName(String name);

}
