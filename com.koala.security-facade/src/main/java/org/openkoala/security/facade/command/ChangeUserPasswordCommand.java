package org.openkoala.security.facade.command;

/**
 * 修改用户密码
 * @author luzhao
 */
public class ChangeUserPasswordCommand {

    private String userAccount;

    private String userPassword;

    private String oldUserPassword;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getOldUserPassword() {
        return oldUserPassword;
    }

    public void setOldUserPassword(String oldUserPassword) {
        this.oldUserPassword = oldUserPassword;
    }
}
