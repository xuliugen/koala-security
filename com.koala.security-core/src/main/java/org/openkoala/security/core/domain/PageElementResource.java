package org.openkoala.security.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.utils.Assert;
import org.openkoala.security.core.IdentifierIsExistedException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 页面元素全权限资源，它用于页面上的元素（按钮Button，标题title等等）。
 *
 * @author lucas
 */
@Entity
@DiscriminatorValue("PAGE_ELEMENT_RESOURCE")
public class PageElementResource extends SecurityResource {

    private static final long serialVersionUID = 8933589588651981397L;

    @Column(name = "IDENTIFIER")
    private String identifier;

    protected PageElementResource() {
    }

    public PageElementResource(String name, String identifier) {
        super(name);
        Assert.notEmpty(identifier, "identifier cannot be empty.");
        isIdentifierExisted(identifier);
        this.identifier = identifier;
    }

    @Override
    public void save() {
        super.save();
    }

    public void changeIdentifier(String identifier) {
        Assert.notEmpty(identifier, "identifier cannot be empty.");
        if (!identifier.equals(this.getIdentifier())) {
            isIdentifierExisted(identifier);
            this.identifier = identifier;
            save();
        }
    }

    public static PageElementResource getBy(String securityResourceName) {
        return getRepository().createCriteriaQuery(PageElementResource.class)
                .eq("name", securityResourceName)
                .singleResult();
    }

    public static PageElementResource getBy(Long id) {
        return PageElementResource.get(PageElementResource.class, id);
    }

    public static boolean hasIdentifier(String identifier) {
        return getby(identifier) != null;
    }

    private static PageElementResource getby(String identifier) {
        return getRepository()
                .createCriteriaQuery(PageElementResource.class)
                .eq("identifier", identifier)
                .singleResult();
    }

    private void isIdentifierExisted(String identifier) {
        if (null != getby(identifier)) {
            throw new IdentifierIsExistedException("pageElemntResource identifier existed.");
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(getName())
                .append(getIdentifier())
                .append(getDescription())
                .build();
    }

    public String getIdentifier() {
        return identifier;
    }
}