package org.openkoala.security.core.domain;

import org.dayatang.utils.Assert;
import org.openkoala.security.core.UrlIsExistedException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

/**
 * URL访问权限资源，他表示页面请求服务端的URL。
 * @author lucas
 */
@Entity
@DiscriminatorValue("URL_ACCESS_RESOURCE")
public class UrlAccessResource extends SecurityResource {

    private static final long serialVersionUID = -9116913523532845475L;

    @Column(name = "URL")
    private String url;

    protected UrlAccessResource() {
    }

    public UrlAccessResource(String name, String url) {
        super(name);
        checkUrl(url);
        isExistUrl(url);
        this.url = url;
    }

    private void checkUrl(String url) {
        Assert.notBlank(url, "url cannot be empty.");
    }

    @Override
    public void save() {
        super.save();
    }

    public void changeUrl(String url) {
        checkUrl(url);
        if (!url.equals(this.getUrl())) {
            isExistUrl(url);
            this.url = url;
            this.save();
        }
    }

    /**
     * TODO IllegalArgumentException hibernate 本身就有 直接捕获？
     * @param id
     * @return
     */
    public static UrlAccessResource getBy(Long id) {
        return UrlAccessResource.get(UrlAccessResource.class, id);
    }

    public static List<UrlAccessResource> findAllUrlAccessResources() {
        return getRepository()
                .createNamedQuery("SecurityResource.findAllByType")
                .addParameter("securityResourceType", UrlAccessResource.class)
                .list();
    }

    public static List<Role> findRoleBySecurityResource(UrlAccessResource resource) {
        return ResourceAssignment.findRoleBySecurityResource(resource);
    }

    /**
     * @param url url of the UrlAccessResource, can't be null.
     * @return
     */
    protected UrlAccessResource findByUrl(String url) {
        checkUrl(url);
        return getRepository()
                .createCriteriaQuery(UrlAccessResource.class)
                .eq("url", url)
                .singleResult();
    }

    private void isExistUrl(String url) {
        if (findByUrl(url) != null) {
            throw new UrlIsExistedException("url existed.");
        }
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"name", "url"};
    }

}