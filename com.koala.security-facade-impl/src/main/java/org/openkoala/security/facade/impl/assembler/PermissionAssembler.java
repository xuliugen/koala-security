package org.openkoala.security.facade.impl.assembler;

import org.openkoala.security.core.domain.Permission;
import org.openkoala.security.facade.command.CreatePermissionCommand;
import org.openkoala.security.facade.dto.PermissionDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PermissionAssembler {

	public static Permission toPermission(CreatePermissionCommand command) {
		Permission result = new Permission(command.getName(), command.getIdentifier());
		result.setDescription(command.getDescription());
		return result;
	}

    public static PermissionDTO toPermissionDTO(Permission permission) {
        PermissionDTO result = new PermissionDTO(permission.getId(),permission.getName(),permission.getIdentifier(),permission.getDescription());
        result.setVersion(permission.getVersion());
        return result;
    }

    public static List<PermissionDTO> toPermissionDTOs(Collection<Permission> permissions) {
        List<PermissionDTO> results = new ArrayList<PermissionDTO>();
        for (Permission permission : permissions) {
            results.add(PermissionAssembler.toPermissionDTO(permission));
        }
        return results;
    }
}
