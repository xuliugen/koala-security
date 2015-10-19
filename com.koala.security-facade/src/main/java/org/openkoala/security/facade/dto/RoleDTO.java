package org.openkoala.security.facade.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openkoala.security.facade.SecurityAccessFacade;

import java.io.Serializable;
import java.util.*;

public class RoleDTO implements Serializable {

    private static final long serialVersionUID = -8008875711416716934L;

    private Long id;

    private int version;

    private String name;

    private String description;

    private String url;

    private Set<PermissionDTO> permissionDTOs = new HashSet<PermissionDTO>();

    private List<MenuResourceDTO> menuResources = new ArrayList<MenuResourceDTO>();

    private List<UrlAccessResourceDTO> urlAccessResources = new ArrayList<UrlAccessResourceDTO>();

    private List<PageElementResourceDTO> pageElementResources = new ArrayList<PageElementResourceDTO>();


    protected RoleDTO() {
    }

    public RoleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public RoleDTO(Long id, String name, String description, String url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public void add(PermissionDTO permissionDTO) {
        this.permissionDTOs.add(permissionDTO);
    }

    public void add(Collection<PermissionDTO> permissionDTOs) {
        this.permissionDTOs.addAll(permissionDTOs);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<PermissionDTO> getPermissionDTOs() {
        return permissionDTOs;
    }

    public void setPermissionDTOs(Set<PermissionDTO> permissionDTOs) {
        this.permissionDTOs = permissionDTOs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MenuResourceDTO> getMenuResources() {
        return menuResources;
    }

    public void setMenuResources(List<MenuResourceDTO> menuResources) {
        this.menuResources = menuResources;
    }

    public List<UrlAccessResourceDTO> getUrlAccessResources() {
        return urlAccessResources;
    }

    public void setUrlAccessResources(List<UrlAccessResourceDTO> urlAccessResources) {
        this.urlAccessResources = urlAccessResources;
    }

    public List<PageElementResourceDTO> getPageElementResources() {
        return pageElementResources;
    }

    public void setPageElementResources(List<PageElementResourceDTO> pageElementResources) {
        this.pageElementResources = pageElementResources;
    }

    /**
     * 因为{@link SecurityAccessFacade}findRolesByMenuOrUrl需要url 所以比领域层多一个url。
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()//
                .append(name)//
                .append(url).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RoleDTO)) {
            return false;
        }
        RoleDTO that = (RoleDTO) other;
        return new EqualsBuilder()//
                .append(this.getName(), that.getName())//
                .append(this.getUrl(), that.getUrl()).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)//
                .append(getId())//
                .append(getName())//
                .append(getDescription())//
                .append(getUrl())//
                .build();
    }

}
