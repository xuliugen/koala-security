package org.openkoala.security.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.utils.Assert;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 资源分配，它表示系统中将要在权限资源的基础上进行授权的控制。
 * @author lucas
 */
@Entity
@Table(name = "KS_RESOURCEASSIGNMENTS")
@NamedQueries({
        @NamedQuery(name = "ResourceAssignment.findSecurityResourcesByAuthorities", query = "SELECT _resource FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _authority in (:authorities) AND TYPE(_resource)= :resourceType GROUP BY _resource.id ORDER BY _resource.id"),
        @NamedQuery(name = "ResourceAssignment.findSecurityResourcesByAuthority", query = "SELECT _resource FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _authority = :authority AND TYPE(_resource)= :resourceType AND TYPE(_authority) = :authorityType GROUP BY _resource.id ORDER BY _resource.id"),
        @NamedQuery(name = "ResourceAssignment.findSecurityResourcesByAuthorityNoResourcesType", query = "SELECT _resource FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _authority = :authority GROUP BY _resource.id ORDER BY _resource.id"),
        @NamedQuery(name = "ResourceAssignment.findAuthoritiesBySecurityResource", query = "SELECT _authority FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _resource = :resource AND TYPE(_authority)= :authorityType GROUP BY _authority.id ORDER BY _authority.id"),
        @NamedQuery(name = "ResourceAssignment.checkHasSecurityResource", query = "SELECT _resourceAssignment FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE _authority IN (:authorities) AND TYPE(_resource) = :securityResourceType  AND _resource.identifier = :identifier"),
        @NamedQuery(name = "ResourceAssignment.findByResourceTypeAndAuthority", query = "SELECT _resourceAssignment FROM ResourceAssignment _resourceAssignment JOIN _resourceAssignment.authority _authority JOIN _resourceAssignment.resource _resource WHERE TYPE(_resource) = :resourceType AND TYPE(_authority) = :authorityType")
})
public class ResourceAssignment extends SecurityAbstractEntity {

    @ManyToOne
    @JoinColumn(name = "AUTHORITY_ID")
    private Authority authority;

    @ManyToOne
    @JoinColumn(name = "SECURITYRESOURCE_ID")
    private SecurityResource resource;

    protected ResourceAssignment() {
    }

    public ResourceAssignment(Authority authority, SecurityResource resource) {
        Assert.notNull(authority, "authority cannot be empty.");
        Assert.notNull(resource, "resource cannot be empty.");
        this.authority = authority;
        this.resource = resource;
    }

    @Override
    public void save() {
        if (existed(authority, resource)) {
            return;
        }
        super.save();
    }

    @Override
    public void remove() {
        super.remove();
    }

    public static ResourceAssignment findByResourceInAuthority(Authority authority, SecurityResource resource) {
        return getRepository()
                .createCriteriaQuery(ResourceAssignment.class)
                .eq("authority", authority)
                .eq("resource", resource)
                .singleResult();
    }

    public static List<ResourceAssignment> findByAuthority(Authority authority) {
        Set<Authority> authorities = getAuthoritiesByAuthority(authority);
        return getRepository()
                .createCriteriaQuery(ResourceAssignment.class)
                .in("authority", authorities)
                .asc("id")
                .list();
    }

    /**
     * 很奇怪~ 排序规则是变化的，所以强制使用id升序返回。
     */
    public static List<ResourceAssignment> findByResource(SecurityResource resource) {
        return getRepository()
                .createCriteriaQuery(ResourceAssignment.class)
                .eq("resource", resource)
                .asc("id")
                .list();
    }

    public static List<MenuResource> findMenuResourceByAuthorities(Set<? extends Authority> authorities) {
        return getRepository()
                .createNamedQuery("ResourceAssignment.findSecurityResourcesByAuthorities")
                .addParameter("authorities", authorities)
                .addParameter("resourceType", MenuResource.class)
                .list();
    }

    public static List<MenuResource> findMenuResourceByAuthority(Authority authority) {
        Set<Authority> authorities = getAuthoritiesByAuthority(authority);
        return findMenuResourceByAuthorities(authorities);
    }

    public static List<UrlAccessResource> findUrlAccessResourcesByAuthority(Authority authority) {
        Set<Authority> authorities = getAuthoritiesByAuthority(authority);
        return findUrlAccessResourcesByAuthorities(authorities);
    }

    public static List<UrlAccessResource> findUrlAccessResourcesByAuthorities(Set<? extends Authority> authorities) {
        return getRepository()
                .createNamedQuery("ResourceAssignment.findSecurityResourcesByAuthorities")
                .addParameter("authorities", authorities)
                .addParameter("resourceType", UrlAccessResource.class)
                .list();
    }

    public static List<Role> findRoleBySecurityResource(SecurityResource resource) {
        return getRepository().createNamedQuery("ResourceAssignment.findAuthoritiesBySecurityResource")
                .addParameter("resource", resource)
                .addParameter("authorityType", Role.class)
                .list();
    }

    public static List<Permission> findPermissionBySecurityResource(SecurityResource resource) {
        return getRepository().createNamedQuery("ResourceAssignment.findAuthoritiesBySecurityResource")
                .addParameter("resource", resource)
                .addParameter("authorityType", Permission.class)
                .list();
    }

    /**
     * @param resourceAssignmentId id for ResourceAssignment cannot null.
     */
    public static ResourceAssignment getById(Long resourceAssignmentId) {
        return ResourceAssignment.get(ResourceAssignment.class, resourceAssignmentId);
    }

    private static Set<Authority> getAuthoritiesByAuthority(Authority authority) {
        Set<Authority> results = new HashSet<Authority>();
        results.add(authority);
        if (authority instanceof Role) {
            Role role = (Role) authority;
            results.addAll(role.getPermissions());
        }
        return results;
    }

    private boolean existed(Authority authority, SecurityResource resource) {
        return findByResourceInAuthority(authority, resource) != null;
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"resource", "authority"};
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(resource)
                .append(authority)
                .toString();
    }

    public SecurityResource getResource() {
        return resource;
    }

    public Authority getAuthority() {
        return authority;
    }
}
