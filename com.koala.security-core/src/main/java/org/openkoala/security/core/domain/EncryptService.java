package org.openkoala.security.core.domain;

/**
 * 密码加密服务。
 *
 * @author lucas
 */
public interface EncryptService {

    /**
     * 加密密码。
     *
     * @param password 密码
     * @param salt     盐值
     * @return 加密之后的密码
     * @throws IllegalArgumentException 如果password参数为空，那么就抛出{@link IllegalArgumentException}
     */
    String encryptPassword(String password, String salt) throws IllegalArgumentException;

    /**
     * 获取加密策略 例如： MD5。
     *
     * @return 加密策略
     */
    String getCredentialsStrategy();

    /**
     * 获取加密次数。
     *
     * @return 加密次数
     */
    int getHashIterations();

    /**
     * 是否开启盐值加密。
     *
     * @return 返回<code>false</code>，表示需要盐值加密，返回<code>true</code>,表示不需要盐值加密
     */
    boolean saltDisabled();
}