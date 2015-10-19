package org.openkoala.example.application;

import java.util.List;

import org.openkoala.example.domain.PersonInfo;

public interface PersonInfoApplication {

	 PersonInfo getPersonInfo(Long id);
	
	 void savePersonInfo(PersonInfo personInfo);
	
	 void updatePersonInfo(PersonInfo personInfo);
	
	 void removePersonInfo(PersonInfo personInfo);
	
	 void removePersonInfos(List<PersonInfo> personInfos);
	
	 List<PersonInfo> findAllPersonInfo();
}

