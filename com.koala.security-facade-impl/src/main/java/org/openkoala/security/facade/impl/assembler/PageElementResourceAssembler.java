package org.openkoala.security.facade.impl.assembler;

import org.openkoala.security.core.domain.PageElementResource;
import org.openkoala.security.facade.command.CreatePageElementResourceCommand;
import org.openkoala.security.facade.dto.PageElementResourceDTO;

public class PageElementResourceAssembler {

	public static PageElementResource toPageElementResource(CreatePageElementResourceCommand command) {
		PageElementResource result = new PageElementResource(command.getName(), command.getIdentifier());
		result.setDescription(command.getDescription());
		return result;
	}

    public static PageElementResourceDTO toPageElementResourceDTO(PageElementResource pageElementResource) {
        PageElementResourceDTO result = new PageElementResourceDTO(pageElementResource.getName(),pageElementResource.getDescription());
        result.setId(pageElementResource.getId());
        result.setVersion(pageElementResource.getVersion());
        result.setIdentifier(pageElementResource.getIdentifier());
        return result;
    }
}
