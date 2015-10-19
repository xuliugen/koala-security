package org.openkoala.example.facade.impl.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openkoala.example.domain.PersonInfo;
import org.openkoala.example.facade.dto.PersonInfoDTO;

public class PersonInfoAssembler {

	public static PersonInfo toEntity(PersonInfoDTO personInfoDTO){
		if(personInfoDTO == null){
			return null;
		}
		PersonInfo result = new PersonInfo();
		result.setBirthday(personInfoDTO.getBirthday());
		result.setId(personInfoDTO.getId());
		result.setVersion(personInfoDTO.getVersion());
		result.setIdentityCardNumber(personInfoDTO.getIdentityCardNumber());
		result.setMarried(personInfoDTO.getMarried());
		result.setName(personInfoDTO.getName());
		result.setProceeds(personInfoDTO.getProceeds());
		result.setSex(personInfoDTO.getSex());		
		return result;
	}
	
	public static List<PersonInfo> toEntitys(Collection<PersonInfoDTO> personInfoDTOs){
		if(personInfoDTOs == null){
			return null;
		}
		List<PersonInfo> results = new ArrayList<PersonInfo>();
		for(PersonInfoDTO each : personInfoDTOs){
			results.add(PersonInfoAssembler.toEntity(each));
		}
		return results;
	}
	
	public static PersonInfoDTO toDTO(PersonInfo personInfo){
		if(personInfo == null){
			return null;
		}
		PersonInfoDTO result = new PersonInfoDTO();
		result.setBirthday(personInfo.getBirthday());
		result.setId(personInfo.getId());
		result.setVersion(personInfo.getVersion());
		result.setIdentityCardNumber(personInfo.getIdentityCardNumber());
		result.setMarried(personInfo.getMarried());
		result.setName(personInfo.getName());
		result.setProceeds(personInfo.getProceeds());
		result.setSex(personInfo.getSex());		
		return result;
	}
	
	public static List<PersonInfoDTO> toDTOs(Collection<PersonInfo> personInfos){
		if(personInfos == null){
			return null;
		}
		List<PersonInfoDTO> results = new ArrayList<PersonInfoDTO>();
		for(PersonInfo each : personInfos){
			results.add(PersonInfoAssembler.toDTO(each));
		}
		return results;
	}
	
}
