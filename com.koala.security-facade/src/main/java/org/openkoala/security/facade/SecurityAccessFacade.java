package org.openkoala.security.facade;

import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.facade.dto.*;

import java.util.List;
import java.util.Set;

/**
 * 权限访问门面，按照使用角色命名，一般都是用户使用该接口，对系统的正常使用。
 * 参数都是简单类型，返回结果是{@link Page}或者某某DTO。
 * 好处：不会与具体的领域对象耦合。不依赖特定的技术。
 * @author lucas
 */
public interface SecurityAccessFacade {

    /**
     * 根据用户ID得到用户。
     * @param userId 用户ID
     * @return 用户 {@link UserDTO}
     */
    UserDTO getUserById(Long userId);

    /**
     * 根据用户名查找所有的角色。
     * @param userAccount 用户名
     */
    InvokeResult findRolesByUserAccount(String userAccount);

    /**
     * 根据用户账号查找其拥有的所有菜单资源集合。
     * @param userAccount 用户账号
     * @return 菜单资源DTO集合 {@link MenuResourceDTO}
     */
    List<MenuResourceDTO> findMenuResourceByUserAccount(String userAccount);

    /**
     * 根据用户账号查询用户的菜单资源。
     * 包含用户的权限拥有的菜单资源、角色拥有的菜单资源、角色的权限拥有的菜单资源
     * @param userAccount 用户账号
     * @param roleName    角色名称
     */
    InvokeResult findMenuResourceByUserAsRole(String userAccount, String roleName);

    /**
     * 查询所有的菜单树。
     */
    InvokeResult findAllMenusTree();

    /**
     * 查找菜单资源或者URL访问资源拥有的所有的权限。
     * @return 不重复的权限集合 {@link PermissionDTO}
     */
    Set<PermissionDTO> findPermissionsByMenuOrUrl();

    /**
     * 查找菜单资源或者URL访问资源拥有的所有的角色。
     * @return 不重复的角色集合 {@link RoleDTO}
     */
    Set<RoleDTO> findRolesByMenuOrUrl();

    /**
     * 查询出所有的URL访问资源集合，并且有Role 和Permission。
     * @return UrlAuthorityDTO集合{@link UrlAuthorityDTO}
     */
    List<UrlAuthorityDTO> findAllUrlAccessResources();

    /**
     * 根据角色ID查询菜单树（已经选择的有选择标识）。
     * @param roleId 角色ID
     */
    InvokeResult findMenuResourceTreeSelectItemByRoleId(Long roleId);

    /**
     * 通过用户账户得到用户。
     * @param userAccount 用户账户
     * @return 用户 {@link UserDTO}
     */
    UserDTO getUserByUserAccount(String userAccount);

    /**
     * 通过用户的邮箱得到用户。
     * @param email 用户的邮箱
     * @return 用户 {@link UserDTO}
     */
    UserDTO getUserByEmail(String email);

    /**
     * 通过用户联系电话得到用户。
     * @param telePhone 用户的联系电话
     */
    UserDTO getUserByTelePhone(String telePhone);

    /**
     * 根据用户账户和角色名称查找不重复的权限集合
     * @param userAccount 用户账号
     * @param roleName    角色名称
     * @return 不重复的权限集合
     */
    Set<PermissionDTO> findPermissionsByUserAccountAndRoleName(String userAccount, String roleName);

    /**
     * 根据用户账户查询用户详细。
     * @param userAccount 用户账户
     */
    InvokeResult getuserDetail(String userAccount);

    /**
     * 检测角色名是否存在。
     * @param roleName 角色名称
     * @return 返回<code>true</code>,存在；返回<code>false</code>,不存在
     */
    boolean checkRoleByName(String roleName);

    /**
     * 根据用户账号和角色名称检测用户是否有某个角色。
     * @param userAccount 用户账号
     * @param roleName    角色名称
     * @return <code>true</code>，有角色；<code>false</code>，没有角色
     */
    boolean checkUserIsHaveRole(String userAccount, String roleName);

    /**
     * 根据用户ID查找用户信息：包含用户本身信息，用户授权信息（即：拥有的角色、拥有的权限）。
     * @param userId 用户ID
     */
    InvokeResult findInfoOfUser(Long userId);

    /**
     * 根据权限ID查询权限信息包含权限授权信息即菜单资源、页面元素资源、URL访问资源。
     * @param permissionId 权限ID
     */
    InvokeResult findInfOfPermission(Long permissionId);

    /**
     * 分页查询用户信息。
     * @param pageIndex          要设置的页码
     * @param pageSize           要设置的页大小
     * @param queryUserCondition 查询用户条件 {@link UserDTO}
     * @return 分页对象 {@link Page}，包含用户集合 {UserDTO}
     */
    Page<UserDTO> pagingQueryUsers(int pageIndex, int pageSize, UserDTO queryUserCondition);

    /**
     * 根据角色条件分页查询角色集合。
     * @param pageIndex          要设置的页码
     * @param pageSize           要设置的页大小
     * @param queryRoleCondition 查询角色条件 {@link RoleDTO}
     * @return 分页对象 {@link Page}，包含角色集合 {RoleDTO}
     */
    Page<RoleDTO> pagingQueryRoles(int pageIndex, int pageSize, RoleDTO queryRoleCondition);

    /**
     * 根据角色查询条件和用户ID分页查询没有分配的角色。
     * @param pageIndex          要设置的页码
     * @param pageSize           要设置的页大小
     * @param queryRoleCondition 查询角色条件 {@link RoleDTO}
     * @param userId             用户ID
     * @return 分页对象 {@link Page}，包含角色集合 {RoleDTO}
     */
    Page<RoleDTO> pagingQueryNotGrantRoles(int pageIndex, int pageSize, RoleDTO queryRoleCondition, Long userId);

    /**
     * 根据用户ID分页查询已经分配的角色集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param userId    用户ID
     * @return 分页对象 {@link Page}，包含角色集合 {@link RoleDTO}
     */
    Page<RoleDTO> pagingQueryGrantRolesByUserId(int pageIndex, int pageSize, Long userId);

    /**
     * 根据用户账号分页查询用户拥有的所有角色集合。
     * @param pageIndex   要设置的页码
     * @param pageSize    要设置的页大小
     * @param userAccount 用户账号
     * @return 分页对象 {@link Page}，包含角色集合 {@link RoleDTO}
     */
    Page<RoleDTO> pagingQueryRolesOfUser(int pageIndex, int pageSize, String userAccount);

    /**
     * 根据URL访问查询条件分页查询所有的URL访问资源集合。
     * @param pageIndex                       要设置的页码
     * @param pageSize                        要设置的页大小
     * @param queryUrlAccessResourceCondition 查询URL访问资源条件
     * @return 分页对象 {@link Page}，包含URL访问资源集合 {@link UrlAccessResourceDTO}
     */
    Page<UrlAccessResourceDTO> pagingQueryUrlAccessResources(int pageIndex, int pageSize, UrlAccessResourceDTO queryUrlAccessResourceCondition);

    /**
     * 根据角色ID分页查找已经分配的URL访问资源集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param roleId    角色ID
     * @return 分页对象 {@link Page}，包含URL访问资源集合 {@link UrlAccessResourceDTO}
     */
    Page<UrlAccessResourceDTO> pagingQueryGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId);

    /**
     * 根据角色ID查找所有没有分配的URL访问资源集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param roleId    角色ID
     * @return 分页对象 {@link Page}，包含URL访问资源集合 {@link UrlAccessResourceDTO}
     */
    Page<UrlAccessResourceDTO> pagingQueryNotGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId);

    /**
     * 根据角色ID和URL访问资源条件查询已经分配的URL访问资源集合。
     * @param pageIndex                       要设置的页码
     * @param pageSize                        要设置的页大小
     * @param roleId                          角色ID
     * @param queryUrlAccessResourceCondition 查询URL访问资源条件
     * @return 分页对象 {@link Page}，包含URL访问资源集合 {@link UrlAccessResourceDTO}
     */
    Page<UrlAccessResourceDTO> pagingQueryGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId, UrlAccessResourceDTO queryUrlAccessResourceCondition);

    /**
     * 根据角色ID和URL访问资源条件查询没有分配的URL访问资源集合。
     * @param pageIndex                       要设置的页码
     * @param pageSize                        要设置的页大小
     * @param roleId                          角色ID
     * @param queryUrlAccessResourceCondition 查询URL访问资源条件
     * @return 分页对象 {@link Page}，包含URL访问资源集合 {@link UrlAccessResourceDTO}
     */
    Page<UrlAccessResourceDTO> pagingQueryNotGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId, UrlAccessResourceDTO queryUrlAccessResourceCondition);

    /**
     * 根据页面元素资源条件查询所有的页面元素资源集合。
     * @param pageIndex                         要设置的页码
     * @param pageSize                          要设置的页大小
     * @param queryPageElementResourceCondition 查询页面元素资源条件 {@link PageElementResourceDTO}
     * @return 分页对象 {@link Page}，包含页面元素集合 {@link PageElementResourceDTO}
     */
    Page<PageElementResourceDTO> pagingQueryPageElementResources(int pageIndex, int pageSize, PageElementResourceDTO queryPageElementResourceCondition);

    /**
     * 根据角色查询已经分配的页面元素资源集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param roleId    角色ID
     * @return 分页对象 {@link Page}，包含页面元素集合 {@link PageElementResourceDTO}
     */
    Page<PageElementResourceDTO> pagingQueryGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId);

    /**
     * 根据角色查询没有分配的页面元素资源集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param roleId    角色ID
     * @return 分页对象 {@link Page}，包含页面元素集合 {@link PageElementResourceDTO}
     */
    Page<PageElementResourceDTO> pagingQueryNotGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId);

    /**
     * 根据角色ID和页面元素资源条件分页查询已经分配的页面元素资源集合。
     * @param pageIndex                         要设置的页码
     * @param pageSize                          要设置的页大小
     * @param roleId                            角色ID
     * @param queryPageElementResourceCondition 查询页面元素资源条件 {@link PageElementResourceDTO}
     * @return 分页对象 {@link Page}，包含页面元素集合 {@link PageElementResourceDTO}
     */
    Page<PageElementResourceDTO> pagingQueryGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId, PageElementResourceDTO queryPageElementResourceCondition);

    /**
     * 根据角色ID和页面元素资源条件分页查询没有分配的页面元素资源集合。
     * @param pageIndex                         要设置的页码
     * @param pageSize                          要设置的页大小
     * @param roleId                            角色ID
     * @param queryPageElementResourceCondition 查询页面元素资源条件 {@link PageElementResourceDTO}
     * @return 分页对象 {@link Page}，包含页面元素集合 {@link PageElementResourceDTO}
     */
    Page<PageElementResourceDTO> pagingQueryNotGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId, PageElementResourceDTO queryPageElementResourceCondition);

    /**
     * 根据权限查询条件分页查询权限信息。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @return 分页对象 {@link Page}，包含用户集合 {PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryPermissions(int pageIndex, int pageSize, PermissionDTO queryPermissionCondition);

    /**
     * 根据用户ID分页查询已经分配的权限集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param userId    用户ID
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryGrantPermissionByUserId(int pageIndex, int pageSize, Long userId);

    /**
     * 根据角色ID分页查询没有分配的权限集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param roleId    角色ID
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId);

    /**
     * 根据角色ID分页查询已经分配的权限集合。
     * @param pageIndex 要设置的页码
     * @param pageSize  要设置的页大小
     * @param roleId    角色ID
     * @return 分页对象 {@link Page}，包含页面元素集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId);

    /**
     * 根据URL访问资源ID分页查询已经授权的权限。
     * @param pageIndex           要设置的页码
     * @param pageSize            要设置的页大小
     * @param urlAccessResourceId URL访问资源ID
     * @return 分页对象 {@link Page}，包含权限 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryGrantPermissionsByUrlAccessResourceId(int pageIndex, int pageSize, Long urlAccessResourceId);

    /**
     * 根据URL访问资源分页查询没有授权的权限集合。
     * @param pageIndex           要设置的页码
     * @param pageSize            要设置的页大小
     * @param urlAccessResourceId URL访问资源ID
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByUrlAccessResourceId(int pageIndex, int pageSize, Long urlAccessResourceId);

    /**
     * 根据菜单资源ID分页查询已经分配的权限。
     * @param pageIndex      要设置的页码
     * @param pageSize       要设置的页大小
     * @param menuResourceId 菜单资源ID
     * @return 分页对象 {@link Page}，包含权限 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryGrantPermissionsByMenuResourceId(int pageIndex, int pageSize, Long menuResourceId);

    /**
     * 根据菜单资源ID分页查询没有分配的权限集合。
     * @param pageIndex      要设置的页码
     * @param pageSize       要设置的页大小
     * @param menuResourceId 菜单资源ID
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByMenuResourceId(int pageIndex, int pageSize, Long menuResourceId);

    /**
     * 根据用户ID和权限条件分页查询没有分配的权限。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @param userId                   用户ID
     * @return 分页对象 {@link Page}，包含权限 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByUserId(int pageIndex, int pageSize, PermissionDTO queryPermissionCondition, Long userId);

    /**
     * 根据角色ID和查询权限条件分页查询已经分配的权限。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param roleId                   角色ID
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @return 分页对象 {@link Page}，包含权限 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId, PermissionDTO queryPermissionCondition);

    /**
     * 根据页面元素资源ID分页查询已经分配的权限。
     * @param pageIndex             要设置的页码
     * @param pageSize              要设置的页大小
     * @param pageElementResourceId 页面元素资源ID
     * @return 分页对象 {@link Page}，包含权限 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryGrantPermissionsByPageElementResourceId(int pageIndex, int pageSize, Long pageElementResourceId);

    /**
     * 根据页面元素资源ID分页查询没有分配的权限集合。
     * @param pageIndex             要设置的页码
     * @param pageSize              要设置的页大小
     * @param pageElementResourceId 页面元素资源ID
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByPageElementResourceId(int pageIndex, int pageSize, Long pageElementResourceId);

    /**
     * 根据角色ID和查询权限条件分页查询没有分配的权限集合。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param roleId                   角色ID
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId, PermissionDTO queryPermissionCondition);

    /**
     * 根据菜单资源ID和查询权限条件分页查询没有分配的权限集合。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param menuResourceId           菜单资源ID
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByMenuResourceId(int pageIndex, int pageSize, Long menuResourceId, PermissionDTO queryPermissionCondition);

    /**
     * 根据页面元素资源ID和查询权限条件分页查询没有分配的权限集合。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param pageElementResourceId    页面元素资源ID
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByPageElementResourceId(int pageIndex, int pageSize, Long pageElementResourceId, PermissionDTO queryPermissionCondition);

    /**
     * 根据URL访问资源ID和查询权限条件分页查询没有分配的权限集合。
     * @param pageIndex                要设置的页码
     * @param pageSize                 要设置的页大小
     * @param urlAccessResourceId      URL访问资源ID
     * @param queryPermissionCondition 查询权限条件 {@link PermissionDTO}
     * @return 分页对象 {@link Page}，包含权限集合 {@link PermissionDTO}
     */
    Page<PermissionDTO> pagingQueryNotGrantPermissionsByUrlAccessResourceId(int pageIndex, int pageSize, Long urlAccessResourceId, PermissionDTO queryPermissionCondition);
}
