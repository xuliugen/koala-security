package org.openkoala.security.facade.command;

/**
 * 创建用户
 * @author luzhao
 */
public class CreateUserCommand {

    private String name;

    private String userAccount;

    private String createOwner;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getCreateOwner() {
        return createOwner;
    }

    public void setCreateOwner(String createOwner) {
        this.createOwner = createOwner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CreateUserCommand{" +
                "name='" + name + '\'' +
                ", userAccount='" + userAccount + '\'' +
                ", createOwner='" + createOwner + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
