
package org.openkoala.example.web.controller;

import org.dayatang.utils.Page;
import org.openkoala.example.facade.PersonInfoFacade;
import org.openkoala.example.facade.dto.PersonInfoDTO;
import org.openkoala.koala.commons.InvokeResult;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/PersonInfo")
public class PersonInfoController {

    @Inject
    private PersonInfoFacade personInfoFacade;

    @ResponseBody
    @RequestMapping("/add")
    public InvokeResult add(PersonInfoDTO personInfoDTO) {
        return personInfoFacade.savePersonInfo(personInfoDTO);
    }

    @ResponseBody
    @RequestMapping("/update")
    public InvokeResult update(PersonInfoDTO personInfoDTO) {
        return personInfoFacade.updatePersonInfo(personInfoDTO);
    }

    @ResponseBody
    @RequestMapping("/pageJson")
    public Page<PersonInfoDTO> pageJson(PersonInfoDTO personInfoDTO, @RequestParam int page, @RequestParam int pagesize) {
        return personInfoFacade.pageQueryPersonInfo(personInfoDTO, page, pagesize);
    }

    @ResponseBody
    @RequestMapping("/delete")
    public InvokeResult delete(@RequestParam String ids) {
        String[] value = ids.split(",");
        Long[] idArrs = new Long[value.length];
        for (int i = 0; i < value.length; i++) {
            idArrs[i] = Long.parseLong(value[i]);
        }
        return personInfoFacade.removePersonInfos(idArrs);
    }

    @ResponseBody
    @RequestMapping("/get/{id}")
    public InvokeResult get(@PathVariable Long id) {
        return personInfoFacade.getPersonInfo(id);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        //CustomDateEditor 可以换成自己定义的编辑器。  
        //注册一个Date 类型的绑定器 。
        binder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
    }

}
