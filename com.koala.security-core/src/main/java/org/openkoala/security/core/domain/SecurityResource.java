package org.openkoala.security.core.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.utils.Assert;
import org.openkoala.security.core.CorrelationException;
import org.openkoala.security.core.NameIsExistedException;

/**
 * 权限资源,它是{@link MethodInvocationResource}、{@link MenuResource}、{@link PageElementResource}、{@link UrlAccessResource}共同的基类。
 * 它是所有纳入系统管理的信息实体，如进销存系统中的进货订单，页面上的任何一个元素,菜单功能,方法调用,权限资源就是这些。
 *
 * @author lucas
 */
@Entity
@Table(name = "KS_SECURITYRESOURCES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
        @NamedQuery(name = "SecurityResource.findAllByType", query = "SELECT _securityResource  FROM SecurityResource _securityResource WHERE TYPE(_securityResource) = :securityResourceType"),
        @NamedQuery(name = "SecurityResource.findByName", query = "SELECT _securityResource  FROM SecurityResource _securityResource WHERE TYPE(_securityResource) = :securityResourceType AND _securityResource.name = :name")
})
public abstract class SecurityResource extends SecurityAbstractEntity {

    private static final long serialVersionUID = 6064565786784560656L;

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

    protected SecurityResource() {
    }

    public SecurityResource(String name) {
        checkName(name);
        isNameExisted(name);
        this.name = name;
    }

    @Override
    public void remove() {
        if (!ResourceAssignment.findByResource(this).isEmpty()) {
            throw new CorrelationException("securityResource has authority, cannot remove it.");
        }
        super.remove();
    }

    public static void batchSave(List<? extends SecurityResource> securityResources) {
        for (SecurityResource securityResource : securityResources) {
            securityResource.save();
        }
    }

    public SecurityResource findByName(String name) {
        return getRepository()
                .createNamedQuery("SecurityResource.findByName")
                .addParameter("securityResourceType", this.getClass())
                .addParameter("name", name)
                .singleResult();
    }

    public void changeName(String name) {
        checkName(name);
        if (!name.equals(this.getName())) {
            isNameExisted(name);
            this.name = name;
            this.save();
        }
    }

    protected void isNameExisted(String name) {
        if (findByName(name) != null) {
            throw new NameIsExistedException("securityResource name existed.");
        }
    }

    private void checkName(String name) {
        Assert.notBlank(name, "name cannot be empty.");
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