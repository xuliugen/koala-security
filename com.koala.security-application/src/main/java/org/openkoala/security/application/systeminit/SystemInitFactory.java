package org.openkoala.security.application.systeminit;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.openkoala.koalacommons.resourceloader.Resource;
import org.openkoala.koalacommons.resourceloader.impl.classpath.ClassPathResource;

public class SystemInitFactory {

    public static final SystemInitFactory INSTANCE = new SystemInitFactory();
    private static final String CONTEXT_PATH = "org.openkoala.security.application.systeminit";

    public SystemInit getSystemInit(String xmlPath) {
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(CONTEXT_PATH);
            Unmarshaller u = jc.createUnmarshaller();
            return (SystemInit) u.unmarshal(getSystemInitXml(xmlPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getSystemInitXml(String xmlPath) throws IOException {
        Resource resource = new ClassPathResource(xmlPath, SystemInitFactory.class);
        return resource.getInputStream();
    }
}
