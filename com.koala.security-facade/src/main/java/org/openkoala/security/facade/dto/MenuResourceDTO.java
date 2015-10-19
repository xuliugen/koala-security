package org.openkoala.security.facade.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuResourceDTO implements Serializable {

    private static final long serialVersionUID = 3329310376086930435L;

    private Long id;

    private int version;

    private String name;

    private String url;

    private String menuIcon;

    private String description;

    private Long parentId;

    private String parentName;

    private int level;

    private boolean checked;

    private String roles;

    private String permissions;

    private List<MenuResourceDTO> children = new ArrayList<MenuResourceDTO>();

    protected MenuResourceDTO() {
    }

    public MenuResourceDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MenuResourceDTO(Long id, String name, String url, String menuIcon, String description, Long parentId, int level) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.menuIcon = menuIcon;
        this.description = description;
        this.parentId = parentId;
        this.level = level;
    }

    public MenuResourceDTO(Long id, String name, String url, String menuIcon, String description, Long parentId, int level, String parentName) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.menuIcon = menuIcon;
        this.description = description;
        this.parentId = parentId;
        this.level = level;
        this.parentName = parentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<MenuResourceDTO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuResourceDTO> children) {
        this.children = children;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean isChecked) {
        this.checked = isChecked;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()//
                .append(name)//
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MenuResourceDTO)) {
            return false;
        }
        MenuResourceDTO that = (MenuResourceDTO) other;
        return new EqualsBuilder()//
                .append(this.getName(), that.getName())//
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)//
                .append(getId())//
                .append(getName())//
                .append(getDescription())//
                .append(getMenuIcon())//
                .append(getLevel())//
                .append(getUrl())//
                .append(getParentId())//
                .build();//
    }
}
