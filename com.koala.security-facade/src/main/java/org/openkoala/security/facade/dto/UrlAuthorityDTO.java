package org.openkoala.security.facade.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by luzhao on 14-8-21.
 */
public class UrlAuthorityDTO {

    private String url;

    private Set<String> roles = new HashSet<String>();

    private Set<String> permissions = new HashSet<String>();

    public UrlAuthorityDTO(String url) {
        this.url = url;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
