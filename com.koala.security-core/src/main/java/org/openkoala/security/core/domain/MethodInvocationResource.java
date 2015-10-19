package org.openkoala.security.core.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 方法调用权限资源,它表示服务端的方法，暂未实现。
 *
 * @author lucas
 */
@Entity
@DiscriminatorValue("METHOD_INVOCATION_RESOURCE")
public class MethodInvocationResource extends SecurityResource {

}