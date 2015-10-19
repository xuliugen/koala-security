package org.openkoala.security.facade.dto;

/**
 * URL-ROLE
 * Created by luzhao on 14-8-21.
 */
public class UrlRoleDTO {

    private String url;

    private String role;

    protected UrlRoleDTO() {
    }

    public UrlRoleDTO(String url, String role) {
        this.url = url;
        this.role = role;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
