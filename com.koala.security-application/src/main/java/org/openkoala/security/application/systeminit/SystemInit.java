package org.openkoala.security.application.systeminit;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "user",
    "role",
    "menuResource",
    "urlAccessResource",
    "pageElementResource"
})
@XmlRootElement(name = "systemInit")
public class SystemInit {

    @XmlElement(required = true)
    protected SystemInit.User user;

    @XmlElement(required = true)
    protected SystemInit.Role role;

    @XmlElement(required = true)
    protected List<SystemInit.MenuResource> menuResource;

    @XmlElement(required = true)
    protected List<SystemInit.UrlAccessResource> urlAccessResource;

    @XmlElement(required = true)
    protected List<SystemInit.PageElementResource> pageElementResource;

    public SystemInit.User getUser() {
        return user;
    }

    public void setUser(SystemInit.User value) {
        this.user = value;
    }

    public SystemInit.Role getRole() {
        return role;
    }

    public void setRole(SystemInit.Role value) {
        this.role = value;
    }

    public List<SystemInit.MenuResource> getMenuResource() {
        if (menuResource == null) {
            menuResource = new ArrayList<SystemInit.MenuResource>();
        }
        return this.menuResource;
    }

    public List<SystemInit.UrlAccessResource> getUrlAccessResource() {
        if (urlAccessResource == null) {
            urlAccessResource = new ArrayList<SystemInit.UrlAccessResource>();
        }
        return this.urlAccessResource;
    }

    public List<SystemInit.PageElementResource> getPageElementResource() {
        if (pageElementResource == null) {
            pageElementResource = new ArrayList<SystemInit.PageElementResource>();
        }
        return this.pageElementResource;
    }

    public org.openkoala.security.core.domain.User createUser() {
        SystemInit.User initUser = getUser();
        org.openkoala.security.core.domain.User user = new org.openkoala.security.core.domain.User(getUser().getName(), getUser().getUsername());
        user.setCreateOwner(initUser.getCreateOwner());
        user.setDescription(initUser.getDescription());
        user.save();
        return user;
    }

    public org.openkoala.security.core.domain.Role createRole() {
        SystemInit.Role initRole = getRole();
        org.openkoala.security.core.domain.Role role = new org.openkoala.security.core.domain.Role(initRole.getName());
        role.setDescription(initRole.getDescription());
        role.save();
        return role;
    }

    public List<org.openkoala.security.core.domain.MenuResource> createMenuResourceAndReturnNeedGrant() {

        List<org.openkoala.security.core.domain.MenuResource> menuResources = new ArrayList<org.openkoala.security.core.domain.MenuResource>();
        for (SystemInit.MenuResource each : getParentMenuResources()) {
            org.openkoala.security.core.domain.MenuResource menuResource = transformMenuResourceEntity(each);
            menuResource.save();
            if (!each.isNotGrant()) {
                menuResources.add(menuResource);
            }
            createChildrenMenuResource(menuResource, each, menuResources);
        }
        return menuResources;
    }

    public List<org.openkoala.security.core.domain.UrlAccessResource> createUrlAccessResources() {
        List<org.openkoala.security.core.domain.UrlAccessResource> results = new ArrayList<org.openkoala.security.core.domain.UrlAccessResource>();
        for (SystemInit.UrlAccessResource each : getUrlAccessResource()) {
            org.openkoala.security.core.domain.UrlAccessResource resource =  new org.openkoala.security.core.domain.UrlAccessResource(each.getName(), each.getUrl());
            resource.save();
            if(!each.isNotGrant()){
                results.add(resource);
            }
        }
        return results;
    }

    public List<org.openkoala.security.core.domain.PageElementResource> createPageElementResources() {
        List<org.openkoala.security.core.domain.PageElementResource> results = new ArrayList<org.openkoala.security.core.domain.PageElementResource>();
        for (SystemInit.PageElementResource each : getPageElementResource()) {
            org.openkoala.security.core.domain.PageElementResource resource = new org.openkoala.security.core.domain.PageElementResource(each.getName(), each.getUrl());
            resource.save();
            if(!each.isNotGrant()){
                results.add(resource);
            }
        }
        return results;
    }

    /*------------- Private helper methods  -----------------*/

    private void createChildrenMenuResource(org.openkoala.security.core.domain.MenuResource menuResource, SystemInit.MenuResource parentMenuResource, List<org.openkoala.security.core.domain.MenuResource> menuResources) {
        for (SystemInit.MenuResource each : getMenuResource()) {
            if (Integer.valueOf(parentMenuResource.getId()).equals(each.getPid())) {
                org.openkoala.security.core.domain.MenuResource children = transformMenuResourceEntity(each);
                menuResource.addChild(children);
                if (!each.isNotGrant()) {
                    menuResources.add(children);
                }
                createChildrenMenuResource(children, each, menuResources);
            }
        }
    }

    private List<SystemInit.MenuResource> getParentMenuResources() {
        List<SystemInit.MenuResource> parentMenuResources = new ArrayList<SystemInit.MenuResource>();
        for (SystemInit.MenuResource each : getMenuResource()) {
            if (each.getPid() == null) {
                parentMenuResources.add(each);
            }
        }
        return parentMenuResources;
    }

    private org.openkoala.security.core.domain.MenuResource transformMenuResourceEntity(SystemInit.MenuResource initMenuResource) {
        org.openkoala.security.core.domain.MenuResource menuResource = new org.openkoala.security.core.domain.MenuResource(initMenuResource.getName());
        menuResource.setDescription(initMenuResource.getDescription());
        menuResource.setMenuIcon(initMenuResource.getMenuIcon());
        menuResource.setUrl(initMenuResource.getUrl());
        return menuResource;
    }

    /*--------------  Java Inner Class  ------------------*/

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MenuResource {

        @XmlAttribute(name = "id", required = true)
        protected int id;

        @XmlAttribute(name = "name", required = true)
        protected String name;

        @XmlAttribute(name = "description")
        protected String description;

        @XmlAttribute(name = "menuIcon")
        protected String menuIcon;

        @XmlAttribute(name = "url")
        protected String url;

        @XmlAttribute(name = "pid")
        protected Integer pid;

        @XmlAttribute(name = "notGrant")
        protected Boolean notGrant = false;

        /*-------------- getter setter methods  ------------------*/

        public int getId() {
            return id;
        }

        public void setId(int value) {
            this.id = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String value) {
            this.description = value;
        }

        public String getMenuIcon() {
            if (menuIcon == null) {
                return "glyphicon  glyphicon-list-alt";
            } else {
                return menuIcon;
            }
        }

        public void setMenuIcon(String value) {
            this.menuIcon = value;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String value) {
            this.url = value;
        }

        public Integer getPid() {
            return pid;
        }

        public void setPid(Integer value) {
            this.pid = value;
        }

        public boolean isNotGrant() {
            if (notGrant == null) {
                return true;
            } else {
                return notGrant;
            }
        }

        public void setNotGrant(Boolean value) {
            this.notGrant = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PageElementResource {

        @XmlAttribute(name = "name", required = true)
        protected String name;

        @XmlAttribute(name = "url", required = true)
        protected String url;

        @XmlAttribute(name = "notGrant")
        protected Boolean notGrant = false;

        /*-------------- getter setter methods  ------------------*/

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String value) {
            this.url = value;
        }

        public boolean isNotGrant() {
            if (notGrant == null) {
                return true;
            } else {
                return notGrant;
            }
        }

        public void setNotGrant(Boolean value) {
            this.notGrant = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Role {

        @XmlAttribute(name = "name", required = true)
        protected String name;

        @XmlAttribute(name = "description")
        protected String description;

        /*-------------- getter setter methods  ------------------*/

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String value) {
            this.description = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UrlAccessResource {

        @XmlAttribute(name = "name", required = true)
        protected String name;

        @XmlAttribute(name = "url", required = true)
        protected String url;

        @XmlAttribute(name = "notGrant")
        protected Boolean notGrant = false;

        /*-------------- getter setter methods  ------------------*/

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String value) {
            this.url = value;
        }

        public boolean isNotGrant() {
            if (notGrant == null) {
                return true;
            } else {
                return notGrant;
            }
        }

        public void setNotGrant(Boolean value) {
            this.notGrant = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class User {

        @XmlAttribute(name = "name", required = true)
        protected String name;

        @XmlAttribute(name = "username", required = true)
        protected String username;

        @XmlAttribute(name = "createOwner")
        protected String createOwner;

        @XmlAttribute(name = "description")
        protected String description;

        /*-------------- getter setter methods  ------------------*/

        public String getName() {
            return name;
        }

        public void setName(String value) {
            this.name = value;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String value) {
            this.username = value;
        }

        public String getCreateOwner() {
            return createOwner;
        }

        public void setCreateOwner(String value) {
            this.createOwner = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String value) {
            this.description = value;
        }
    }
}
