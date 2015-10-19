package org.openkoala.security.facade.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PermissionDTO {

    private Long id;

    private int version;

    private String name;

    private String userAccount;

    private String identifier;

    private String description;

    private String url;

    private MenuResourceDTO menuResource;

    private UrlAccessResourceDTO urlAccessResource;

    private PageElementResourceDTO pageElementResource;

    protected PermissionDTO() {
    }

    public PermissionDTO(Long id, String name, String identifier, String description) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.description = description;
    }

    public PermissionDTO(Long id, String name, String identifier, String description, String url) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.description = description;
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String permissionName) {
        this.name = permissionName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long permissionId) {
        this.id = permissionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MenuResourceDTO getMenuResource() {
        return menuResource;
    }

    public void setMenuResource(MenuResourceDTO menuResource) {
        this.menuResource = menuResource;
    }

    public UrlAccessResourceDTO getUrlAccessResource() {
        return urlAccessResource;
    }

    public void setUrlAccessResource(UrlAccessResourceDTO urlAccessResource) {
        this.urlAccessResource = urlAccessResource;
    }

    public PageElementResourceDTO getPageElementResource() {
        return pageElementResource;
    }

    public void setPageElementResource(PageElementResourceDTO pageElementResource) {
        this.pageElementResource = pageElementResource;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(identifier)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PermissionDTO)) {
            return false;
        }
        PermissionDTO that = (PermissionDTO) other;
        return new EqualsBuilder()
                .append(this.getName(), that.getName())
                .append(this.getIdentifier(), that.getIdentifier())
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(getId())
                .append(getName())
                .append(getIdentifier())
                .append(getDescription())
                .build();
    }
}
