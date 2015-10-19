package org.openkoala.security.facade.dto;

/**URL-PERMISSION
 * Created by luzhao on 14-8-21.
 */
public class UrlPermissionDTO {

    private String url;

    private String permission;

    protected UrlPermissionDTO() {}

    public UrlPermissionDTO(String url, String permission) {
        this.url = url;
        this.permission = permission;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
