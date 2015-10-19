
package org.openkoala.example.facade.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.dayatang.domain.InstanceFactory;
import org.dayatang.utils.Page;
import org.dayatang.querychannel.QueryChannelService;
import org.openkoala.example.application.PersonInfoApplication;
import org.openkoala.example.domain.PersonInfo;
import org.openkoala.example.facade.PersonInfoFacade;
import org.openkoala.example.facade.dto.PersonInfoDTO;
import org.openkoala.example.facade.impl.assembler.PersonInfoAssembler;
import org.openkoala.koala.commons.InvokeResult;

@Named
public class PersonInfofacadeImpl implements PersonInfoFacade {

	@Inject
	private PersonInfoApplication personInfoApplication;
		
	private QueryChannelService queryChannel;

    private QueryChannelService getQueryChannelService(){
       if(queryChannel==null){
          queryChannel = InstanceFactory.getInstance(QueryChannelService.class,"queryChannel");
       }
     return queryChannel;
    }
	
	public InvokeResult getPersonInfo(Long id) {
		PersonInfo personInfo = personInfoApplication.getPersonInfo(id);
		return InvokeResult.success(PersonInfoAssembler.toDTO(personInfo));
	}
	
	public InvokeResult savePersonInfo(PersonInfoDTO personInfoDTO) {
		PersonInfo personInfo = PersonInfoAssembler.toEntity(personInfoDTO);
		personInfoApplication.savePersonInfo(personInfo);
		personInfoDTO.setId((java.lang.Long)personInfo.getId());
		return InvokeResult.success(personInfoDTO);
	}
	
	public InvokeResult updatePersonInfo(PersonInfoDTO personInfoDTO) {
		PersonInfo personInfo = PersonInfoAssembler.toEntity(personInfoDTO);
		personInfoApplication.updatePersonInfo(personInfo);
		return InvokeResult.success();
	}
	
	public InvokeResult removePersonInfo(Long id) {
		PersonInfo personInfo = personInfoApplication.getPersonInfo(id);
		if(personInfo == null){
			return InvokeResult.failure("人员信息不存在！");
		}
		personInfoApplication.removePersonInfo(personInfo);
		return InvokeResult.success("删除成功");
	}
	
	public InvokeResult removePersonInfos(Long[] ids) {
		List<PersonInfo> personInfos = new ArrayList<PersonInfo>();
		for (long id : ids) {
			PersonInfo personInfo = personInfoApplication.getPersonInfo(id);
			if(personInfo != null){
				personInfos.add(personInfo);
			}
		}
		personInfoApplication.removePersonInfos(personInfos);
		return InvokeResult.success();
	}
	
	public List<PersonInfoDTO> findAllPersonInfo() {
		return PersonInfoAssembler.toDTOs(personInfoApplication.findAllPersonInfo());
	}	
	
	public Page<PersonInfoDTO> pageQueryPersonInfo(PersonInfoDTO personInfoDTO, int currentPage, int pageSize) {
		List<PersonInfoDTO> result = new ArrayList<PersonInfoDTO>();
		List<Object> conditionVals = new ArrayList<Object>();
	   	StringBuilder jpql = new StringBuilder("select _personInfo from PersonInfo _personInfo   where 1=1 ");
	
	
	   	if (personInfoDTO.getName() != null && !"".equals(personInfoDTO.getName())) {
	   		jpql.append(" and _personInfo.name like ?");
	   		conditionVals.add(MessageFormat.format("%{0}%", personInfoDTO.getName()));
	   	}		
	
	
	   	if (personInfoDTO.getSex() != null && !"".equals(personInfoDTO.getSex())) {
	   		jpql.append(" and _personInfo.sex like ?");
	   		conditionVals.add(MessageFormat.format("%{0}%", personInfoDTO.getSex()));
	   	}		
	
	   	if (personInfoDTO.getIdentityCardNumber() != null && !"".equals(personInfoDTO.getIdentityCardNumber())) {
	   		jpql.append(" and _personInfo.identityCardNumber like ?");
	   		conditionVals.add(MessageFormat.format("%{0}%", personInfoDTO.getIdentityCardNumber()));
	   	}		
	
	   	if (personInfoDTO.getBirthday() != null) {
	   		jpql.append(" and _personInfo.birthday between ? and ? ");
	   		conditionVals.add(personInfoDTO.getBirthday());
	   		conditionVals.add(personInfoDTO.getBirthdayEnd());
	   	}	
	
	   	if (personInfoDTO.getMarried() != null) {
		   	jpql.append(" and _personInfo.married is ?");
		   	conditionVals.add(personInfoDTO.getMarried());
	   	}	
	   	
	   	if (personInfoDTO.getProceeds() != null) {
		   	jpql.append(" and _personInfo.proceeds is ?");
		   	conditionVals.add(personInfoDTO.getProceeds());
	   	}	
	
        Page<PersonInfo> pages = getQueryChannelService().createJpqlQuery(jpql.toString()).setParameters(conditionVals).setPage(currentPage, pageSize).pagedList();
        for (PersonInfo personInfo : pages.getData()) {
            result.add(PersonInfoAssembler.toDTO(personInfo));
        }
        return new Page<PersonInfoDTO>(pages.getPageIndex(), pages.getResultCount(), pageSize, result);
	}

	
}
