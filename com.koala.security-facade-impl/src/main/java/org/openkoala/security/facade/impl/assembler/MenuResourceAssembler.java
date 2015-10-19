package org.openkoala.security.facade.impl.assembler;

import org.openkoala.security.core.domain.MenuResource;
import org.openkoala.security.facade.command.CreateChildMenuResourceCommand;
import org.openkoala.security.facade.command.CreateMenuResourceCommand;
import org.openkoala.security.facade.dto.MenuResourceDTO;

public class MenuResourceAssembler {

    public static MenuResource toMenuResource(CreateMenuResourceCommand command) {
        MenuResource result = new MenuResource(command.getName());
        result.setUrl(command.getUrl());
        result.setMenuIcon(command.getMenuIcon());
        result.setDescription(command.getDescription());
        return result;
    }

    public static MenuResource toMenuResource(CreateChildMenuResourceCommand command) {
        MenuResource result = new MenuResource(command.getName());
        result.setUrl(command.getUrl());
        result.setMenuIcon(command.getMenuIcon());
        result.setDescription(command.getDescription());
        return result;
    }

    public static MenuResourceDTO toMenuResourceDTO(MenuResource menuResource) {
        MenuResourceDTO result = new MenuResourceDTO(menuResource.getId(), menuResource.getName());
        result.setVersion(menuResource.getVersion());
        result.setMenuIcon(menuResource.getMenuIcon());
        result.setLevel(menuResource.getLevel());
        result.setUrl(menuResource.getUrl());
        result.setDescription(menuResource.getDescription());
        if (menuResource.getParent() != null && !menuResource.getParent().equals(null)) {
            result.setParentName(menuResource.getParent().getName());
        }
        return result;
    }
}
