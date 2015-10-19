package org.openkoala.security.facade.impl.assembler;

import org.openkoala.security.core.domain.UrlAccessResource;
import org.openkoala.security.facade.command.CreateUrlAccessResourceCommand;
import org.openkoala.security.facade.dto.UrlAccessResourceDTO;

public class UrlAccessResourceAssembler {

    public static UrlAccessResource toUrlAccessResource(CreateUrlAccessResourceCommand command) {
        UrlAccessResource result = new UrlAccessResource(command.getName(), command.getUrl());
        result.setDescription(command.getDescription());
        return result;
    }

    public static UrlAccessResourceDTO toUrlAccessResourceDTO(UrlAccessResource urlAccessResource) {
        UrlAccessResourceDTO result = new UrlAccessResourceDTO(urlAccessResource.getId(), urlAccessResource.getName(), urlAccessResource.getUrl());
        result.setVersion(urlAccessResource.getVersion());
        result.setDescription(urlAccessResource.getDescription());
        return result;
    }

}
