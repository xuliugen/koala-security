package org.openkoala.security.facade.impl;

import java.text.MessageFormat;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.dayatang.domain.InstanceFactory;
import org.dayatang.querychannel.QueryChannelService;
import org.dayatang.utils.Page;
import org.openkoala.koala.commons.InvokeResult;
import org.openkoala.security.application.SecurityAccessApplication;
import org.openkoala.security.core.domain.*;
import org.openkoala.security.facade.SecurityAccessFacade;
import org.openkoala.security.facade.dto.MenuResourceDTO;
import org.openkoala.security.facade.dto.PageElementResourceDTO;
import org.openkoala.security.facade.dto.PermissionDTO;
import org.openkoala.security.facade.dto.RoleDTO;
import org.openkoala.security.facade.dto.UrlAccessResourceDTO;
import org.openkoala.security.facade.dto.UrlAuthorityDTO;
import org.openkoala.security.facade.dto.UrlPermissionDTO;
import org.openkoala.security.facade.dto.UrlRoleDTO;
import org.openkoala.security.facade.dto.UserDTO;
import org.openkoala.security.facade.impl.assembler.MenuResourceAssembler;
import org.openkoala.security.facade.impl.assembler.PageElementResourceAssembler;
import org.openkoala.security.facade.impl.assembler.PermissionAssembler;
import org.openkoala.security.facade.impl.assembler.RoleAssembler;
import org.openkoala.security.facade.impl.assembler.UrlAccessResourceAssembler;
import org.openkoala.security.facade.impl.assembler.UserAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

@SuppressWarnings("unchecked")
@Named
public class SecurityAccessFacadeImpl implements SecurityAccessFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAccessFacadeImpl.class);

	@Inject
	private SecurityAccessApplication securityAccessApplication;

	private QueryChannelService queryChannelService;

	public QueryChannelService getQueryChannelService() {
		if (queryChannelService == null) {
			queryChannelService = InstanceFactory.getInstance(QueryChannelService.class, "queryChannel_security");
		}
		return queryChannelService;
	}

	public UserDTO getUserById(Long userId) {
		return UserAssembler.toUserDTO(
                securityAccessApplication.getUserById(userId));
	}

	@Override
	public UserDTO getUserByUserAccount(String userAccount) {
		return UserAssembler.toUserDTO(
                securityAccessApplication.getUserByUserAccount(userAccount));
	}

	@Override
	public UserDTO getUserByEmail(String email) {
		return UserAssembler.toUserDTO(
                securityAccessApplication.getUserByEmail(email));
	}

	@Override
	public UserDTO getUserByTelePhone(String telePhone) {
		return UserAssembler.toUserDTO(
                securityAccessApplication.getUserByTelePhone(telePhone));
	}

	public InvokeResult findRolesByUserAccount(String userAccount) {
		try {
			List<RoleDTO> results = new ArrayList<RoleDTO>();
			for (Role role : securityAccessApplication.findAllRolesByUserAccount(userAccount)) {
				results.add(RoleAssembler.toRoleDTO(role));
			}
			return InvokeResult.success(results);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return InvokeResult.failure("根据用户名查找所有的角色失败。");
		}

	}

	public List<MenuResourceDTO> findMenuResourceByUserAccount(String userAccount) {
		List<MenuResourceDTO> results = new ArrayList<MenuResourceDTO>();
		for (MenuResource menuResource : securityAccessApplication.findMenuResourceByUserAccount(userAccount)) {
			results.add(MenuResourceAssembler.toMenuResourceDTO(menuResource));
		}
		return results;
	}

	@Override
	public InvokeResult findMenuResourceByUserAsRole(String userAccount, String roleName) {

		Set<Authority> authorities = new HashSet<Authority>();
        Role role = securityAccessApplication.getRoleBy(roleName);
        if (role != null) {
            authorities.add(role);
            authorities.addAll(role.getPermissions());
        }

		authorities.addAll(User.findAllPermissionsBy(userAccount));
		List<MenuResourceDTO> results = findTopMenuResourceByUserAccountAsRole(authorities);
		List<MenuResourceDTO> childrenMenuResources = findAllMenuResourceByUserAccountAsRole(authorities);

		List<MenuResourceDTO> all = new ArrayList<MenuResourceDTO>();
		all.addAll(results);
		all.addAll(childrenMenuResources);

		addMenuChildrenToParent(all);

		return InvokeResult.success(results);
	}

	@Override
	public InvokeResult findAllMenusTree() {
		List<MenuResourceDTO> results = findTopMenuResource();
		List<MenuResourceDTO> childrenMenuResources = findChidrenMenuResource();
		List<MenuResourceDTO> all = new ArrayList<MenuResourceDTO>();
		all.addAll(results);
		all.addAll(childrenMenuResources);
		addMenuChildrenToParent(all);
		return InvokeResult.success(results);
	}

	@Override
	public InvokeResult findMenuResourceTreeSelectItemByRoleId(Long roleId) {
		try {
			List<MenuResourceDTO> allMenResourcesAsRole = getQueryChannelService()
					.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.MenuResourceDTO(_resource.id,_resource.name) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN  _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId")
					.addParameter("resourceType", MenuResource.class)
					.addParameter("authorityId", roleId)
					.list();

            List<MenuResourceDTO> allMenuResources = (List<MenuResourceDTO>) findAllMenusTree().getData();
			for (MenuResourceDTO menuResourceDTO : allMenuResources) {
				setCheckPropertyToMenuResourceDTO(menuResourceDTO.getChildren(), allMenResourcesAsRole);
				menuResourceDTO.setChecked(allMenResourcesAsRole.contains(menuResourceDTO));
			}
			return InvokeResult.success(allMenuResources);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return InvokeResult.failure("根据角色ID查询菜单权限资源树带有已经选中项失败");
		}
	}

	private void setCheckPropertyToMenuResourceDTO(List<MenuResourceDTO> childrenMenuResources, List<MenuResourceDTO> allMenResourcesAsRole) {
		if (!childrenMenuResources.isEmpty()) {
			for (MenuResourceDTO childMenuResource : childrenMenuResources) {
				childMenuResource.setChecked(allMenResourcesAsRole.contains(childMenuResource));
				setCheckPropertyToMenuResourceDTO(childMenuResource.getChildren(), allMenResourcesAsRole);
			}
		}
	}

	@Override
	public Set<PermissionDTO> findPermissionsByMenuOrUrl() {
		List<PermissionDTO> results = getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name,_authority.identifier,_authority.description,_resource.url) FROM ResourceAssignment _resourceAssignment  JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE Type(_authority) = Permission AND TYPE(_resource) = MenuResource OR TYPE(_resource) = UrlAccessResource")
				.list();
		return Sets.newHashSet(results);
	}

	@Override
	public Set<RoleDTO> findRolesByMenuOrUrl() {
		List<RoleDTO> results = getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.RoleDTO(_authority.id, _authority.name,_authority.description, _resource.url) FROM ResourceAssignment _resourceAssignment  JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_authority) = Role AND (TYPE(_resource) = MenuResource OR TYPE(_resource) = UrlAccessResource)")
				.list();
		return Sets.newHashSet(results);
	}

	@Override
	public List<UrlAuthorityDTO> findAllUrlAccessResources() {
		List<UrlAuthorityDTO> results = findAllUrls();
		for (UrlAuthorityDTO result : results) {
			for (UrlRoleDTO urlRole : findAllUrlRoles()) {
				if (result.getUrl().equals(urlRole.getUrl())) {
					result.addRole(
                            urlRole.getRole());
				}
			}
			for (UrlPermissionDTO urlPermission : findAllUrlPermissions()) {
				if (result.getUrl().equals(urlPermission.getUrl())) {
					result.addPermission(
                            urlPermission.getPermission());
				}
			}
		}
		return results;
	}

	@Override
	public Page<UserDTO> pagingQueryUsers(int pageIndex, int pageSize, UserDTO queryUserCondition) {
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UserDTO(_user.id, _user.version, _user.name, _user.userAccount, _user.createDate, _user.description, _user.createOwner, _user.lastModifyTime, _user.disabled) FROM User _user where 1=1");
		assembleUserJpqlAndConditionValues(queryUserCondition, jpql, "_user", conditionVals);

		return getQueryChannelService().createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<RoleDTO> pagingQueryRoles(int pageIndex, int pageSize, RoleDTO queryRoleCondition) {
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.RoleDTO(_role.id, _role.name, _role.description) FROM Role _role where 1 = 1");
		assembleRoleJpqlAndConditionValues(queryRoleCondition, jpql, "_role", conditionVals);

		return getQueryChannelService().createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryPermissions(int pageIndex, int pageSize, PermissionDTO queryPermissionCondition) {
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name, _permission.identifier, _permission.description) FROM Permission _permission where 1 = 1");
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_permission", conditionVals);

		return getQueryChannelService().createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<RoleDTO> pagingQueryNotGrantRoles(int pageIndex, int pageSize, RoleDTO queryRoleCondition, Long userId) {
		if (userId == null) {
			InvokeResult.failure("用户ID不能为空");
		}
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.RoleDTO(_role.id, _role.name, _role.description)  FROM Role _role WHERE 1 = 1 ");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		assembleRoleJpqlAndConditionValues(queryRoleCondition, jpql, "_role", conditionVals);
		jpqlHasWhereCondition(jpql);
		jpql.append(" _role.id NOT IN(SELECT _authority.id FROM Authorization _authorization JOIN _authorization.actor _actor JOIN _authorization.authority _authority WHERE _actor.id= :userId)");
		conditionVals.put("userId", userId);

		return getQueryChannelService()//
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryGrantPermissionByUserId(int pageIndex, int pageSize, Long userId) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name, _authority.identifier ,_authority.description)");
		jpql.append(" FROM Authorization _authorization JOIN _authorization.actor _actor JOIN _authorization.authority _authority");
		jpql.append(" WHERE TYPE(_authority) = :authorityType");
		jpql.append(" AND _actor.id = :userId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		parameters.put("authorityType", Permission.class);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(parameters)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<RoleDTO> pagingQueryGrantRolesByUserId(int pageIndex, int pageSize, Long userId) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.RoleDTO(_authority.id, _authority.name, _authority.description)");
		jpql.append(" FROM Authorization _authorization JOIN _authorization.actor _actor JOIN _authorization.authority _authority");
		jpql.append(" WHERE TYPE(_authority) = :authorityType");
		jpql.append(" AND _actor.id = :userId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("authorityType", Role.class);
		parameters.put("userId", userId);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(parameters)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name,_permission.identifier, _permission.description)");
		jpql.append(" FROM Permission _permission WHERE _permission.id NOT IN(SELECT _permission.id FROM Permission _permission JOIN _permission.roles _role WHERE _role.id = :roleId)");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("roleId", roleId);

		return getQueryChannelService()//
				.createJpqlQuery(jpql.toString())
				.setParameters(parameters)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name,_permission.identifier, _permission.description) FROM Permission _permission JOIN _permission.roles _role WHERE _role.id = :roleId");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("roleId", roleId);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(parameters)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<UrlAccessResourceDTO> pagingQueryUrlAccessResources(int pageIndex, int pageSize, UrlAccessResourceDTO queryUrlAccessResourceCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UrlAccessResourceDTO(_urlAccessResource.id, _urlAccessResource.name, _urlAccessResource.url,_urlAccessResource.description) FROM UrlAccessResource _urlAccessResource where 1 = 1");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		assembleUrlAccessResourceJpqlAndConditionValues(queryUrlAccessResourceCondition, jpql, "_urlAccessResource", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<UrlAccessResourceDTO> pagingQueryGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId) {
		return getQueryChannelService()//不需要TYPE(_authority)的类型 因为主键是唯一的,能够确定是什么具体维度。
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.UrlAccessResourceDTO(_resource.id, _resource.name, _resource.url,_resource.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId")
				.addParameter("resourceType", UrlAccessResource.class)
				.addParameter("authorityId", roleId)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<UrlAccessResourceDTO> pagingQueryNotGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.UrlAccessResourceDTO(_securityResource.id, _securityResource.name, _securityResource.url,_securityResource.description) FROM SecurityResource _securityResource WHERE TYPE(_securityResource) = :resourceType AND _securityResource.id NOT IN (SELECT _resource.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId)")
                .addParameter("resourceType",UrlAccessResource.class)
                .addParameter("authorityId", roleId)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryGrantPermissionsByUrlAccessResourceId(int pageIndex, int pageSize, Long urlAccessResourceId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name,_authority.identifier, _authority.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_authority) = :authorityType AND _resource.id = :resourceId")
				.addParameter("authorityType", Permission.class)
				.addParameter("resourceId", urlAccessResourceId)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByUrlAccessResourceId(int pageIndex, int pageSize, Long urlAccessResourceId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name, _authority.identifier,_authority.description) FROM Authority _authority WHERE _authority.id NOT IN(SELECT _authority.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _resource.id = :resourceId AND TYPE(_authority) = :authorityType) AND TYPE(_authority) = :authorityType")
				.addParameter("resourceId", urlAccessResourceId)
				.addParameter("authorityType", Permission.class)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryGrantPermissionsByMenuResourceId(int pageIndex, int pageSize, Long menuResourceId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name, _authority.identifier,_authority.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _resource.id = :resourceId AND TYPE(_authority) = :authorityType")
				.addParameter("resourceId", menuResourceId)
				.addParameter("authorityType", Permission.class)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByMenuResourceId(int pageIndex, int pageSize, Long menuResourceId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name,_authority.identifier, _authority.description) FROM Authority _authority WHERE _authority.id NOT IN(SELECT _authority.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _resource.id = :resourceId AND TYPE(_authority) = :authorityType)  AND TYPE(_authority) = :authorityType")
				.addParameter("authorityType", Permission.class)
				.addParameter("resourceId", menuResourceId)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByUserId(int pageIndex, int pageSize, PermissionDTO queryPermissionCondition, Long userId) {
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id,_permission.name, _permission.identifier,_permission.description)  FROM Permission _permission WHERE 1=1 ");
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_permission", conditionVals);
		jpqlHasWhereCondition(jpql);
		jpql.append("_permission.id NOT IN(SELECT _authority.id FROM Authorization _authorization JOIN _authorization.actor _actor JOIN _authorization.authority _authority WHERE TYPE(_authority) = :authorityType AND _actor.id= :userId)");
		conditionVals.put("authorityType", Permission.class);
		conditionVals.put("userId", userId);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PageElementResourceDTO> pagingQueryPageElementResources(int page, int pageSize, PageElementResourceDTO queryPageElementCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PageElementResourceDTO(_resource.id,_resource.version, _resource.name,_resource.identifier, _resource.description) FROM PageElementResource _resource WHERE 1 = 1");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		assemblePageElementResourceJpqlAndConditionValues(queryPageElementCondition, jpql, "_resource", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(page, pageSize)
				.pagedList();
	}

	@Override
	public Page<PageElementResourceDTO> pagingQueryGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PageElementResourceDTO(_resource.id,_resource.version, _resource.name,_resource.identifier, _resource.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND TYPE(_authority) = :authorityType AND _authority.id = :authorityId")
				.addParameter("resourceType", PageElementResource.class)
				.addParameter("authorityType", Role.class)
				.addParameter("authorityId", roleId)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PageElementResourceDTO> pagingQueryNotGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PageElementResourceDTO(_pageElementResource.id,_pageElementResource.version, _pageElementResource.name, _pageElementResource.identifier, _pageElementResource.description) FROM PageElementResource _pageElementResource WHERE _pageElementResource.id NOT IN(SELECT _resource.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId )")
				.addParameter("resourceType", PageElementResource.class)
				.addParameter("authorityId", roleId)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryGrantPermissionsByPageElementResourceId(int pageIndex, int pageSize, Long pageElementResourceId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name,_authority.identifier, _authority.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _resource.id = :resourceId AND TYPE(_authority) = :authorityType")
				.addParameter("resourceId", pageElementResourceId)
				.addParameter("authorityType", Permission.class)
				.setPage(pageIndex, pageSize)//
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByPageElementResourceId(int pageIndex, int pageSize, Long pageElementResourceId) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name,_permission.identifier, _permission.description) FROM Permission _permission WHERE _permission.id NOT IN(SELECT _authority.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _resource.id = :resourceId AND TYPE(_authority) = :authorityType)")
				.addParameter("resourceId", pageElementResourceId)
				.addParameter("authorityType", Permission.class)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Set<PermissionDTO> findPermissionsByUserAccountAndRoleName(String userAccount, String roleName) {
		Set<Permission> permissions = new HashSet<Permission>();
		permissions.addAll(
                User.findAllPermissionsBy(userAccount)); // 所有的权限

		permissions.addAll(
                (Role.getRoleBy(roleName)) // 得到角色
                        .getPermissions()); // 得到角色的所有权限

		Set<PermissionDTO> results = new HashSet<PermissionDTO>();
		for (Permission permission : permissions) {
			results.add(
                    PermissionAssembler.toPermissionDTO(permission));
		}
		return results;
	}

	@Override
	public Page<RoleDTO> pagingQueryRolesOfUser(int pageIndex, int pageSize, String userAccount) {
		return getQueryChannelService()
				.createJpqlQuery("SELECT NEW org.openkoala.security.facade.dto.RoleDTO(_authority.id, _authority.name, _authority.description) FROM Authorization _authorization JOIN  _authorization.actor _actor JOIN _authorization.authority _authority WHERE _actor.userAccount = :userAccount AND TYPE(_authority) = :authorityType GROUP BY _authority.id")
				.addParameter("authorityType", Role.class)
				.addParameter("userAccount", userAccount)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public InvokeResult getuserDetail(String userAccount) {
		return InvokeResult.success(
                UserAssembler.toUserDTONotPassword(
                        securityAccessApplication.getUserByUserAccount(userAccount)));
    }

	@Override
	public boolean checkRoleByName(String roleName) {
		return securityAccessApplication.checkRoleByName(roleName);
	}

	@Override
	public boolean checkUserIsHaveRole(String userAccount, String roleName) {
		List<Role> roles = securityAccessApplication.findAllRolesByUserAccount(userAccount);
		if (roles.isEmpty()) {
			return false;
		}
		for (Role each : roles) {
			if (each.getName().equals(roleName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public InvokeResult findInfoOfUser(Long userId) {
		User user = securityAccessApplication.getUserById(userId);
		UserDTO result = UserAssembler.toUserDTOThatDetail(user);
		result.setRoles(RoleAssembler.toRoleDTOs(securityAccessApplication.findRolesOfUser(user)));
		result.setPermissions(PermissionAssembler.toPermissionDTOs(securityAccessApplication.findPermissionsOfUser(user)));
		return InvokeResult.success(result);
	}

    @Override
    public InvokeResult findInfOfPermission(Long permissionId) {
        Permission permission = securityAccessApplication.getPermissionBy(permissionId);
        PermissionDTO result = PermissionAssembler.toPermissionDTO(permission);
        List<SecurityResource> resources = securityAccessApplication.findResourcesByPermission(permission);
        for(SecurityResource resource : resources) {
            if(resource instanceof MenuResource){
                result.setMenuResource(MenuResourceAssembler.toMenuResourceDTO((MenuResource)resource));
            }else if(resource instanceof UrlAccessResource){
               result.setUrlAccessResource(UrlAccessResourceAssembler.toUrlAccessResourceDTO((UrlAccessResource)resource));
            }else if(resource instanceof PageElementResource){
                result.setPageElementResource(PageElementResourceAssembler.toPageElementResourceDTO((PageElementResource) resource));
            }
        }
        return InvokeResult.success(result);
    }

	@Override
	public Page<UrlAccessResourceDTO> pagingQueryGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId, UrlAccessResourceDTO queryUrlAccessResourceCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UrlAccessResourceDTO(_resource.id, _resource.name, _resource.url,_resource.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", UrlAccessResource.class);
		conditionVals.put("authorityId", roleId);
		assembleUrlAccessResourceJpqlAndConditionValues(queryUrlAccessResourceCondition, jpql, "_resource", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<UrlAccessResourceDTO> pagingQueryNotGrantUrlAccessResourcesByRoleId(int pageIndex, int pageSize, Long roleId, UrlAccessResourceDTO queryUrlAccessResourceCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UrlAccessResourceDTO(_securityResource.id, _securityResource.name, _securityResource.url,_securityResource.description) FROM SecurityResource _securityResource WHERE TYPE(_securityResource) = :resourceType  AND _securityResource.id NOT IN (SELECT _resource.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId)");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", UrlAccessResource.class);
		conditionVals.put("authorityId", roleId);
		assembleUrlAccessResourceJpqlAndConditionValues(queryUrlAccessResourceCondition, jpql, "_securityResource", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PageElementResourceDTO> pagingQueryGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId, PageElementResourceDTO queryPageElementResourceCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PageElementResourceDTO(_resource.id,_resource.version, _resource.name,_resource.identifier, _resource.description) FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND TYPE(_authority) = :authorityType AND _authority.id = :authorityId");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", PageElementResource.class);
		conditionVals.put("authorityType", Role.class);
		conditionVals.put("authorityId", roleId);
		assemblePageElementResourceJpqlAndConditionValues(queryPageElementResourceCondition, jpql, "_resource", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId, PermissionDTO queryPermissionCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name,_permission.identifier, _permission.description) FROM Permission _permission JOIN _permission.roles _role WHERE _role.id = :roleId");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("roleId", roleId);
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_permission", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PageElementResourceDTO> pagingQueryNotGrantPageElementResourcesByRoleId(int pageIndex, int pageSize, Long roleId, PageElementResourceDTO queryPageElementResourceCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PageElementResourceDTO(_pageElementResource.id,_pageElementResource.version, _pageElementResource.name, _pageElementResource.identifier, _pageElementResource.description) FROM PageElementResource _pageElementResource WHERE _pageElementResource.id NOT IN(SELECT _resource.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND _authority.id = :authorityId ) ");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", PageElementResource.class);
		conditionVals.put("authorityId", roleId);
		assemblePageElementResourceJpqlAndConditionValues(queryPageElementResourceCondition, jpql, "_pageElementResource", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByRoleId(int pageIndex, int pageSize, Long roleId, PermissionDTO queryPermissionCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name,_permission.identifier, _permission.description)");
		jpql.append(" FROM Permission _permission WHERE _permission.id NOT IN(SELECT _permission.id FROM Permission _permission JOIN _permission.roles _role WHERE _role.id = :roleId)");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("roleId", roleId);
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_permission", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByMenuResourceId(int pageIndex, int pageSize, Long menuResourceId, PermissionDTO queryPermissionCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name,_authority.identifier, _authority.description) FROM Authority _authority WHERE _authority.id NOT IN(SELECT _authority.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_authority) = :authorityType AND TYPE(_resource) = :resourceType) AND TYPE(_authority) = :authorityType");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", MenuResource.class);
		conditionVals.put("authorityType", Permission.class);
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_authority", conditionVals);
		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByPageElementResourceId(int pageIndex, int pageSize, Long pageElementResourceId, PermissionDTO queryPermissionCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_permission.id, _permission.name,_permission.identifier, _permission.description) FROM Permission _permission WHERE _permission.id NOT IN(SELECT _authority.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND TYPE(_authority) = :authorityType)");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", PageElementResource.class);
		conditionVals.put("authorityType", Permission.class);
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_permission", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	@Override
	public Page<PermissionDTO> pagingQueryNotGrantPermissionsByUrlAccessResourceId(int pageIndex, int pageSize, Long urlAccessResourceId, PermissionDTO queryPermissionCondition) {
		StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.PermissionDTO(_authority.id, _authority.name, _authority.identifier,_authority.description) FROM Authority _authority WHERE _authority.id NOT IN(SELECT _authority.id FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND TYPE(_authority) = :authorityType) AND TYPE(_authority) = :authorityType");
		Map<String, Object> conditionVals = new HashMap<String, Object>();
		conditionVals.put("resourceType", UrlAccessResource.class);
		conditionVals.put("authorityType", Permission.class);
		assemblePermissionJpqlAndConditionValues(queryPermissionCondition, jpql, "_authority", conditionVals);

		return getQueryChannelService()
				.createJpqlQuery(jpql.toString())
				.setParameters(conditionVals)
				.setPage(pageIndex, pageSize)
				.pagedList();
	}

	/*------------- Private helper methods  -----------------*/

    private void assembleUserJpqlAndConditionValues(UserDTO queryUserCondition, StringBuilder jpql, String conditionPrefix, Map<String, Object> conditionVals) {
        String andCondition = " AND " + conditionPrefix;
        if (null != queryUserCondition.getDisabled() && !"".equals(queryUserCondition.getDisabled())) {
            jpql.append(andCondition);
            jpql.append(".disabled = :disabled");
            conditionVals.put("disabled", queryUserCondition.getDisabled());
        }
        if (!StringUtils.isBlank(queryUserCondition.getName())) {
            jpql.append(andCondition);
            jpql.append(".name LIKE :name");
            conditionVals.put("name", MessageFormat.format("%{0}%", queryUserCondition.getName()));
        }
        if (!StringUtils.isBlank(queryUserCondition.getUserAccount())) {
            jpql.append(andCondition);
            jpql.append(".userAccount LIKE :userAccount");
            conditionVals.put("userAccount", MessageFormat.format("%{0}%", queryUserCondition.getUserAccount()));
        }
        if (!StringUtils.isBlank(queryUserCondition.getEmail())) {
            jpql.append(andCondition);
            jpql.append(".email LIKE :email");
            conditionVals.put("email", MessageFormat.format("%{0}%", queryUserCondition.getEmail()));
        }
        if (!StringUtils.isBlank(queryUserCondition.getTelePhone())) {
            jpql.append(andCondition);
            jpql.append(".telePhone LIKE :telePhone");
            conditionVals.put("telePhone", MessageFormat.format("%{0}%", queryUserCondition.getTelePhone()));
        }
        if (!StringUtils.isBlank(queryUserCondition.getDescription())) {
            jpql.append(andCondition);
            jpql.append(".description LIKE :description");
            conditionVals.put("description", MessageFormat.format("%{0}%", queryUserCondition.getDescription()));
        }
    }

    private void assembleRoleJpqlAndConditionValues(RoleDTO queryRoleCondition, StringBuilder jpql, String conditionPrefix, Map<String, Object> conditionVals) {
        String andCondition = " AND " + conditionPrefix;
        if (!StringUtils.isBlank(queryRoleCondition.getName())) {
            jpql.append(andCondition);
            jpql.append(".name LIKE :name");
            conditionVals.put("name", MessageFormat.format("%{0}%", queryRoleCondition.getName()));
        }
        if (!StringUtils.isBlank(queryRoleCondition.getDescription())) {
            jpql.append(andCondition);
            jpql.append(".description LIKE :description");
            conditionVals.put("description", MessageFormat.format("%{0}%", queryRoleCondition.getDescription()));
        }
    }

    private void assemblePermissionJpqlAndConditionValues(PermissionDTO queryPermissionCondition, StringBuilder jpql, String conditionPrefix, Map<String, Object> conditionVals) {
        String andCondition = " AND " + conditionPrefix;
        if (!StringUtils.isBlank(queryPermissionCondition.getName())) {
            jpql.append(andCondition);
            jpql.append(".name LIKE :name");
            conditionVals.put("name", MessageFormat.format("%{0}%", queryPermissionCondition.getName()));
        }
        if (!StringUtils.isBlank(queryPermissionCondition.getIdentifier())) {
            jpql.append(andCondition);
            jpql.append(".identifier LIKE :identifier");
            conditionVals.put("identifier", MessageFormat.format("%{0}%", queryPermissionCondition.getIdentifier()));
        }
        if (!StringUtils.isBlank(queryPermissionCondition.getDescription())) {
            jpql.append(andCondition);
            jpql.append(".description LIKE :description");
            conditionVals.put("description", MessageFormat.format("%{0}%", queryPermissionCondition.getDescription()));
        }
    }

    private void assemblePageElementResourceJpqlAndConditionValues(PageElementResourceDTO queryPageElementResourceCondition, StringBuilder jpql, String conditionPrefix, Map<String, Object> conditionVals) {
        String andCondition = " AND " + conditionPrefix;
        if (!StringUtils.isBlank(queryPageElementResourceCondition.getName())) {
            jpql.append(andCondition);
            jpql.append(".name LIKE :name");
            conditionVals.put("name", MessageFormat.format("%{0}%", queryPageElementResourceCondition.getName()));
        }
        if (!StringUtils.isBlank(queryPageElementResourceCondition.getIdentifier())) {
            jpql.append(andCondition);
            jpql.append(".identifier LIKE :identifier");
            conditionVals.put("identifier", MessageFormat.format("%{0}%", queryPageElementResourceCondition.getIdentifier()));
        }
        if (!StringUtils.isBlank(queryPageElementResourceCondition.getDescription())) {
            jpql.append(andCondition);
            jpql.append(".description LIKE :description");
            conditionVals.put("description", MessageFormat.format("%{0}%", queryPageElementResourceCondition.getDescription()));
        }
    }

    private void assembleUrlAccessResourceJpqlAndConditionValues(UrlAccessResourceDTO queryUrlAccessResourceCondition, StringBuilder jpql, String conditionPrefix, Map<String, Object> conditionVals) {
        String andCondition = " AND " + conditionPrefix;
        if (!StringUtils.isBlank(queryUrlAccessResourceCondition.getName())) {
            jpql.append(andCondition);
            jpql.append(".name LIKE :name");
            conditionVals.put("name", MessageFormat.format("%{0}%", queryUrlAccessResourceCondition.getName()));
        }
        if (!StringUtils.isBlank(queryUrlAccessResourceCondition.getDescription())) {
            jpql.append(andCondition);
            jpql.append(".description LIKE :description");
            conditionVals.put("description", MessageFormat.format("%{0}%", queryUrlAccessResourceCondition.getDescription()));
        }
        if (!StringUtils.isBlank(queryUrlAccessResourceCondition.getUrl())) {
            jpql.append(andCondition);
            jpql.append(".url LIKE :url");
            conditionVals.put("url", MessageFormat.format("%{0}%", queryUrlAccessResourceCondition.getUrl()));
        }
    }

    /**
     * 顶级菜单
     */
    private List<MenuResourceDTO> findTopMenuResourceByUserAccountAsRole(Set<Authority> authorities) {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.MenuResourceDTO(_resource.id,_resource.name, _resource.url, _resource.menuIcon, _resource.description, _resource.parent.id,_resource.level)")
                .append(" FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource")
                .append(" WHERE TYPE(_resource) = MenuResource")
                .append(" AND _authority IN (:authorities)") // 用户拥有的Authority
                .append(" AND _resource.parent IS NULL") // 顶级
                .append(" AND _resource.level = :level") // 顶级
                .append(" GROUP BY _resource.id")
                .append(" ORDER BY _resource.id"); // 必须有排序

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("authorities", authorities)
                .addParameter("level", 0)
                .list();
    }

    /**
     * 所有菜单不包含顶级菜单
     *
     * @param authorities
     * @return
     */
    private List<MenuResourceDTO> findAllMenuResourceByUserAccountAsRole(Set<Authority> authorities) {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.MenuResourceDTO(_resource.id,_resource.name, _resource.url, _resource.menuIcon, _resource.description, _resource.parent.id,_resource.level)")
            .append(" FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource")
            .append(" WHERE TYPE(_resource) = MenuResource")
            .append(" AND _authority IN (:authorities)")   // 用户拥有的Authority
            .append(" AND _resource.level > :level")
            .append(" GROUP BY _resource.id")
            .append(" ORDER BY _resource.id"); // 必须有排序

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("authorities", authorities)
                .addParameter("level", 0)
                .list();
    }

    private void addMenuChildrenToParent(List<MenuResourceDTO> all) {
        LinkedHashMap<Long, MenuResourceDTO> map = new LinkedHashMap<Long, MenuResourceDTO>();
        for (MenuResourceDTO menuResourceDTO : all) {
            map.put(menuResourceDTO.getId(), menuResourceDTO);
        }
        for (MenuResourceDTO menuResourceDTO : map.values()) {
            Long parentId = menuResourceDTO.getParentId();
            if (!StringUtils.isBlank(parentId + "") && map.get(parentId) != null) {
                map.get(parentId).getChildren().add(menuResourceDTO);
            }
        }
    }

    private List<MenuResourceDTO> findChidrenMenuResource() {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.MenuResourceDTO(_resource.id,_resource.name, _resource.url, _resource.menuIcon, _resource.description, _resource.parent.id,_resource.level,_resource.parent.name) FROM MenuResource _resource")
            .append(" WHERE _resource.level > :level")
            .append(" GROUP BY _resource.id")
            .append(" ORDER BY _resource.id"); // 必须有排序

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("level", 0)
                .list();
    }

    private List<MenuResourceDTO> findTopMenuResource() {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.MenuResourceDTO(_resource.id, _resource.name, _resource.url, _resource.menuIcon, _resource.description, _resource.parent.id,_resource.level) FROM MenuResource _resource")
                .append(" WHERE _resource.parent IS NULL")
                .append(" AND _resource.level = :level")
                .append(" GROUP BY _resource.id")
                .append(" ORDER BY _resource.id"); // 必须有排序

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("level", 0)
                .list();
    }

    /**
     * 检查JPQL里面是否包含WHERE 关键字，如果没有就加上。
     *
     * @param jpql
     */
    private void jpqlHasWhereCondition(StringBuilder jpql) {
        if (jpql.indexOf("WHERE") != -1) {
            jpql.append(" AND ");
        } else {
            jpql.append(" WHERE ");
        }
    }

    /**
     * 去除重复的URL
     *
     * @return
     */
    private List<UrlAuthorityDTO> findAllUrls() {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UrlAuthorityDTO(_resource.url)");
        jpql = fromResourceAssigment(jpql);
        jpql.append(" GROUP BY _resource.url");

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("resourceType", UrlAccessResource.class)
                .list();
    }

    /**
     * URL-ROLE
     *
     * @return
     */
    private List<UrlRoleDTO> findAllUrlRoles() {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UrlRoleDTO(_resource.url, _authority.name)");
        jpql = fromResourceAssigment(jpql);
        jpql.append(" AND TYPE(_authority) = :authorityType");

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("authorityType", Role.class)
                .addParameter("resourceType", UrlAccessResource.class)
                .list();
    }

    /**
     * 查询findAllUrlRoles和findAllUrlPermissions方法中都有一样的查询条件，抽取出来。
     *
     * @param jpql
     * @return
     */
    private StringBuilder fromResourceAssigment(StringBuilder jpql) {
        return jpql.append(" FROM ResourceAssignment _resourceAssignment  JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource")
                   .append(" WHERE TYPE(_resource) = :resourceType");
    }

    /**
     * URL-Permission
     *
     * @return
     */
    private List<UrlPermissionDTO> findAllUrlPermissions() {
        StringBuilder jpql = new StringBuilder("SELECT NEW org.openkoala.security.facade.dto.UrlPermissionDTO(_resource.url, _authority.identifier)");
        jpql = fromResourceAssigment(jpql);
        jpql.append(" AND TYPE(_authority) = :authorityType");

        return getQueryChannelService()
                .createJpqlQuery(jpql.toString())
                .addParameter("authorityType", Permission.class)
                .addParameter("resourceType", UrlAccessResource.class)
                .list();
    }
}