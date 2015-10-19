package org.openkoala.security.facade.command;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CreateMenuResourceCommand {

    private String name;

    private String url;

    private String menuIcon;

    private String description;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)//
                .append(name)//
                .append(url)//
                .append(menuIcon)//
                .append(description)//
                .build();
    }

}
