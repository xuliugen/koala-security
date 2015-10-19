package org.openkoala.security.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.utils.Assert;
import org.openkoala.security.core.CorrelationException;
import org.openkoala.security.core.NameIsExistedException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * 授权是是一个抽象的概念，是角色{@link Role}和权限{@link Permission}共同的基类。
 * 它代表某种权限（Permission）或权限集合（Role），可被授予Actor(即对Actor授予Authority)。
 * 它代表一系列的可执行操作或责任，用于限定您再软件系统中能做什么。不能做什么。
 * @author lucas
 */
@Entity
@Table(name = "KS_AUTHORITIES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
        @NamedQuery(name = "Authority.findAllAuthoritiesByUserAccount", query = "SELECT _authority FROM Authorization _authorization JOIN  _authorization.actor _actor JOIN _authorization.authority _authority WHERE _actor.userAccount = :userAccount AND TYPE(_authority) = :authorityType GROUP BY _authority.id"),
        @NamedQuery(name = "Authority.getAuthorityByName", query = "SELECT _authority FROM Authority _authority WHERE TYPE(_authority) = :authorityType AND _authority.name = :name")
})
public abstract class Authority extends SecurityAbstractEntity {

    private static final long serialVersionUID = -5570169700634882013L;

    /**
     * 名称
     */
    @NotNull
    @Column(name = "NAME")
    private String name;

    /**
     * 描述
     */
    @Column(name = "DESCRIPTION")
    private String description;

    protected Authority() {
    }

    public Authority(String name) {
        Assert.notBlank(name, "name cannot be empty.");
        isExistedName(name);
        this.name = name;
    }

    /**
     * 如果授权中心里面有授权，表示授权中心中有参与者和授权。那么授权就不能撤销。
     * 如果资源分配中有授权，那么需要撤销资源分配。最后才是撤销自己。
     */
    @Override
    public void remove() {
        if (!Authorization.findByAuthority(this).isEmpty()) {
            throw new CorrelationException("authority has actor, so can't remove authority.");
        }
        for (ResourceAssignment resourceAssignment : ResourceAssignment.findByAuthority(this)) {
            resourceAssignment.remove();
        }
        super.remove();
    }

    /**
     * 通过授权名称和授权的真实类型得到授权或者授权子类。
     * @param name 授权名称
     * @param <T>  授权子类
     * @return 授权或者授权子类
     */
    public <T extends Authority> T getBy(String name) {
        return getRepository()
                .createNamedQuery("Authority.getAuthorityByName")
                .addParameter("authorityType", this.getClass())
                .addParameter("name", name)
                .singleResult();
    }

    /**
     * 授权分配权限资源。
     * @param securityResource 权限资源
     */
    public void addSecurityResource(SecurityResource securityResource) {
        new ResourceAssignment(this, securityResource).save();
    }

    /**
     * 授权分配多个权限资源。
     * @param securityResources 权限资源集合
     */
    public void addSecurityResources(List<? extends SecurityResource> securityResources) {
        for (SecurityResource securityResource : securityResources) {
            this.addSecurityResource(securityResource);
        }
    }

    /**
     * 从授权中撤销权限资源。
     * @param securityResource 权限资源
     */
    public void terminateSecurityResource(SecurityResource securityResource) {
        ResourceAssignment resourceAssignment = ResourceAssignment.findByResourceInAuthority(this, securityResource);
        if (resourceAssignment != null) {
            resourceAssignment.remove();
        }
    }

    /**
     * 从授权中撤销多个权限资源。
     * @param securityResources 权限资源集合 {@link SecurityResource}
     */
    public void terminateSecurityResources(Set<? extends SecurityResource> securityResources) {
        for (SecurityResource securityResource : securityResources) {
            this.terminateSecurityResource(securityResource);
        }
    }

    /**
     * 改变授权的名称。
     * @param name 授权名称
     */
    public void changeName(String name) {
        Assert.notBlank(name, "authority name cannot be empty.");
        if (!name.equals(this.getName())) {
            isExistedName(name);
            this.name = name;
            this.save();
        }
    }

    public List<MenuResource> findMenuResourceByAuthority() {
        return ResourceAssignment.findMenuResourceByAuthority(this);
    }

    public List<UrlAccessResource> findUrlAccessResourceByAuthority() {
        return ResourceAssignment.findUrlAccessResourcesByAuthority(this);
    }

    public static List<MenuResource> findMenuResourceByAuthorities(Set<Authority> authorities) {
        return ResourceAssignment.findMenuResourceByAuthorities(authorities);
    }

    public static <T extends Authority> T getBy(Long authorityId) {
        return (T) Authority.get(Authority.class, authorityId);
    }

    public static boolean checkHasPageElementResource(Set<Authority> authorities, String identifier) {
        return !getRepository()
                .createNamedQuery("ResourceAssignment.checkHasSecurityResource")
                .addParameter("authorities", authorities)
                .addParameter("securityResourceType", PageElementResource.class)
                .addParameter("identifier", identifier)
                .list().isEmpty();
    }

	/*------------- Private helper methods  -----------------*/

    private void isExistedName(String name) {
        if (getBy(name) != null) {
            throw new NameIsExistedException("authority name existed.");
        }
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"name"};
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(name)
                .append(description)
                .build();
    }

    /*-------------- getter setter methods  ------------------*/

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}