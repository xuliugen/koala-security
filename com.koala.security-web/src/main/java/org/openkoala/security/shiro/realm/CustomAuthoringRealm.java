package org.openkoala.security.shiro.realm;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.openkoala.security.core.domain.EncryptService;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.dto.UserDTO;
import org.openkoala.security.shiro.CurrentUser;
import org.openkoala.security.shiro.RoleHandle;
import org.openkoala.security.shiro.extend.ShiroFilterChainManager;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * 这里加入一个角色。 自定义Realm 放入用户以及角色和权限
 * @author luzhao
 */
@Named("customAuthoringRealm")
public class CustomAuthoringRealm extends AuthorizingRealm implements RoleHandle {

    @Inject
    private SecurityAccessFacade securityAccessFacade;

    @Inject
    protected EncryptService passwordEncryptService;

    @Inject
    private ShiroFilterChainManager shiroFilterChainManager;

    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo result = new SimpleAuthorizationInfo();
        result.setRoles(shiroUser.getRoles());
        result.setStringPermissions(shiroUser.getPermissions());
        return result;
    }

    // TODO 角色切换适配器 切换角色之后下一次登录的角色？目前好像不控制也没有问题。
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken toToken = null;
        if (token instanceof UsernamePasswordToken) {
            toToken = (UsernamePasswordToken) token;
        }
        UserDTO user = findUser((String) toToken.getPrincipal());
        checkUserStatus(user);
        ShiroUser shiroUser = new ShiroUser(user.getUserAccount(), user.getName());

        settingShiroUserEmail(user, shiroUser);
        settingShiroUserTelePhone(user, shiroUser);

        SimpleAuthenticationInfo result = new SimpleAuthenticationInfo(shiroUser, user.getUserPassword(), getName());
        if (!passwordEncryptService.saltDisabled()) {
            result.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt() + shiroUser.getUserAccount()));
        }
        return result;
    }

    private void settingShiroUserTelePhone(UserDTO user, ShiroUser shiroUser) {
        if (StringUtils.isBlank(user.getTelePhone())) {
            shiroUser.setTelePhone("您还没有联系电话，请添加电话！");
        } else {
            shiroUser.setTelePhone(user.getTelePhone());
        }
    }

    private void settingShiroUserEmail(UserDTO user, ShiroUser shiroUser) {
        if (StringUtils.isBlank(user.getEmail())) {
            shiroUser.setEmail("您还没有邮箱，请添加邮箱！");
        } else {
            shiroUser.setEmail(user.getEmail());
        }
    }

    /**
     * 初始化密码加密匹配器
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(passwordEncryptService.getCredentialsStrategy());
        matcher.setHashIterations(passwordEncryptService.getHashIterations());
        setCredentialsMatcher(matcher);
    }

    @Override
    protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    protected void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    protected void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

	/*------------- Private helper methods  -----------------*/

    /**
     * 查找出用户 // TODO 正则匹配
     * @param username
     * @return
     */
    private UserDTO findUser(String username) {
        UserDTO result = securityAccessFacade.getUserByUserAccount(username);
        if (result == null) {
            result = securityAccessFacade.getUserByEmail(username);
        }
        if (result == null) {
            result = securityAccessFacade.getUserByTelePhone(username);
        }
        if (result == null) {
            throw new UnknownAccountException("current principal is not existed.");
        }
        return result;
    }

    /**
     * 检查用户状态
     * @param user
     */
    private void checkUserStatus(UserDTO user) {
        if (user.getDisabled()) {
            throw new LockedAccountException("current user is suspend.");
        }
    }

    /**
     * 切换角色
     * @param roleName
     */
    public void switchOverRoleOfUser(String roleName) {
        PrincipalCollection principalCollection = CurrentUser.getPrincipals();
        ShiroUser shiroUser = (ShiroUser) principalCollection.getPrimaryPrincipal();
        shiroUser.setRoleName(roleName);
        this.doGetAuthorizationInfo(principalCollection);
    }

    @Override
    public void resetRoleName(String name) {
        switchOverRoleOfUser(name);
        shiroFilterChainManager.initFilterChain();
    }

}
