package org.openkoala.example.application.impl;

import org.openkoala.example.application.PersonInfoApplication;
import org.openkoala.example.domain.PersonInfo;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.List;

@Named
@Transactional
public class PersonInfoApplicationImpl implements PersonInfoApplication {

    public PersonInfo getPersonInfo(Long id) {
        return PersonInfo.get(PersonInfo.class, id);
    }

    public void updatePersonInfo(PersonInfo personInfo) {
        personInfo.save();
    }

    public void savePersonInfo(PersonInfo personInfo) {
        if (personInfo != null) {
            personInfo.save();
        }
    }

    public void removePersonInfo(PersonInfo personInfo) {
        personInfo.remove();
    }

    public void removePersonInfos(List<PersonInfo> personInfos) {
        for (PersonInfo personInfo : personInfos) {
            personInfo.remove();
        }
    }

    public List<PersonInfo> findAllPersonInfo() {
        return PersonInfo.findAll(PersonInfo.class);
    }

}
