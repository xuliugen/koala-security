package org.openkoala.security.core.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.domain.InstanceFactory;
import org.dayatang.utils.Assert;
import org.openkoala.security.core.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * 位于系统外部，与系统交互的人，是使用软件的人。
 * 系统的登录，即认证。
 * 可以对其授予角色 {@link Role}、权限 {@link Permission}和用户组 <code> UserGroup </code>
 * @author lucas
 */
@Entity
@DiscriminatorValue("USER")
@NamedQueries({
        @NamedQuery(name = "User.loginByUserAccount", query = "SELECT _user FROM User _user WHERE _user.userAccount = :userAccount AND _user.password = :password"),
        @NamedQuery(name = "User.count", query = "SELECT COUNT(_user.id) FROM User _user")
})
public class User extends Actor {

    private static final long serialVersionUID = 7849700468353029794L;

    private static final String INIT_PASSWORD = "888888";

    @NotNull
    @Column(name = "USER_ACCOUNT")
    private String userAccount;

    @Column(name = "PASSWORD")
    private String password = INIT_PASSWORD;

    //    @Email
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "DISABLED")
    private boolean disabled = false;

    @Column(name = "TELE_PHONE")
    private String telePhone;

    /**
     * 加密盐值
     */
    @Column(name = "SALT")
    private String salt;

    protected User() {
    }

    /**
     * TODO 验证规则，账号。
     */
    public User(String name, String userAccount) {
        super(name);
        checkUserAccount(userAccount);
        isExistUserAccount(userAccount);
        this.userAccount = userAccount;
        this.salt = UUID.randomUUID().toString();
        this.password = encryptPassword(INIT_PASSWORD);
    }


    public void disable() {
        disabled = true;
    }

    public void enable() {
        disabled = false;
    }

    @Override
    public void save() {
        super.save();
    }

    public boolean updatePassword(String userPassword, String oldUserPassword) {
        String encryptOldUserPassword = encryptPassword(oldUserPassword);
        if (this.getPassword().equals(encryptOldUserPassword)) {
            this.password = encryptPassword(userPassword);
            return true;
        }
        return false;
    }

    public void resetPassword() {
        User user = User.get(User.class, this.getId());
        user.password = encryptPassword(INIT_PASSWORD);
    }

    /**
     * 更改账户 TODO 更加严格的验证
     * @param userAccount
     */
    public void changeUserAccount(String userAccount, String userPassword) {
        checkUserAccount(userAccount);
        verifyPassword(userPassword);
        if (!this.getUserAccount().equals(userAccount)) {
            isExistUserAccount(userAccount);
            this.userAccount = userAccount;
            save();
        }
    }

    /**
     * 更改邮箱
     * @param email
     */
    public void changeEmail(String email, String userPassword) {
        verifyPassword(userPassword);
        checkEmail(email);
        // TODO 邮箱验证规则。
        if (!email.equals(this.getEmail())) {
            isExistEmail(email);
            this.email = email;
            save();
        }
    }

    public List<Role> findAllRoles() {
        return findAuthorityConditions()
                .addParameter("actor", this)
                .addParameter("authorityType", Role.class)
                .list();
    }

    public List<Permission> findAllPermissions() {
        return findAuthorityConditions()
                .addParameter("authorityType", Permission.class)
                .list();
    }

    private org.dayatang.domain.NamedQuery findAuthorityConditions() {
        return getRepository()
                .createNamedQuery("Authorization.findAuthoritiesByActor")
                .addParameter("actor", this);
    }

    /**
     * 更改联系电话
     * @param telePhone
     */
    public void changeTelePhone(String telePhone, String userPassword) {
        checkTelePhone(telePhone);
        verifyPassword(userPassword);

        // TODO 联系电话验证
        if (!telePhone.equals(this.getTelePhone())) {
            isExistTelePhone(telePhone);
            this.telePhone = telePhone;
            save();
        }
    }

    /**
     * 根据账户查找拥有的所有角色Role
     * @param userAccount
     * @return
     */
    public static List<Role> findAllRolesBy(String userAccount) {
        List<Role> results = getRepository()
                .createNamedQuery("Authority.findAllAuthoritiesByUserAccount")
                .addParameter("userAccount", userAccount)
                .addParameter("authorityType", Role.class)
                .list();
        if (results.isEmpty()) {
            throw new UserNotHasRoleException("user do have not a role");
        }
        return results;
    }

    /**
     * 根据账户查找拥有的所有权限Permission
     * @param userAccount
     * @return
     */
    public static List<Permission> findAllPermissionsBy(String userAccount) {
        return getRepository()
                .createNamedQuery("Authority.findAllAuthoritiesByUserAccount")
                .addParameter("userAccount", userAccount)
                .addParameter("authorityType", Permission.class)
                .list();
    }

    public static User getById(Long userId) {
        return User.get(User.class, userId);
    }

    /**
     * TODO 校验规则~~正则表达式
     * @param userAccount
     * @return
     */
    public static User getByUserAccount(String userAccount) {
        return getRepository()
                .createCriteriaQuery(User.class)
                .eq("userAccount", userAccount)
                .singleResult();
    }

    /**
     * TODO 校验规则~~正则表达式
     * @param email
     * @return
     */
    public static User getByEmail(String email) {
        return getRepository()
                .createCriteriaQuery(User.class)
                .eq("email", email)
                .singleResult();
    }

    /**
     * TODO 校验规则~~正则表达式
     * @param telePhone
     * @return
     */
    public static User getByTelePhone(String telePhone) {
        return getRepository()
                .createCriteriaQuery(User.class)
                .eq("telePhone", telePhone)
                .singleResult();
    }

    /**
     * 检查仓储中用户是否有数据。
     * @return
     */
    public static boolean hasUserExisted() {
        Long result = getRepository()
                .createNamedQuery("User.count")
                .singleResult();
        return result > 0;
    }

    protected static EncryptService passwordEncryptService;

    protected static EncryptService getPasswordEncryptService() {
        if (passwordEncryptService == null) {
            passwordEncryptService = InstanceFactory.getInstance(EncryptService.class);
        }
        return passwordEncryptService;
    }

    protected static void setPasswordEncryptService(EncryptService passwordEncryptService) {
        User.passwordEncryptService = passwordEncryptService;
    }

    protected String encryptPassword(String password) {
        return getPasswordEncryptService().encryptPassword(password, salt + userAccount);
    }

	/*------------- Private helper methods  -----------------*/

    private void isExistTelePhone(String telePhone) {
        User user = getRepository().createCriteriaQuery(User.class)
                .eq("telePhone", telePhone)
                .singleResult();
        if (user != null) {
            throw new TelePhoneIsExistedException("user telePhone is existed.");
        }
    }

    private void isExistEmail(String email) {
        User user = getRepository().createCriteriaQuery(User.class)
                .eq("email", email)
                .singleResult();
        if (user != null) {
            throw new EmailIsExistedException("user email is existed.");
        }
    }

    private void isExistUserAccount(String userAccount) {
        if (getByUserAccount(userAccount) != null) {
            throw new UserAccountIsExistedException("user userAccount is existed.");
        }
    }

    private void verifyPassword(String userPassword) {
        if (!encryptPassword(userPassword).equals(this.getPassword())) {
            throw new UserPasswordException("user password is not right.");
        }
    }

    private void checkUserAccount(String userAccount) {
        Assert.notBlank(userAccount, "userAccount cannot be empty.");
    }

    private void checkEmail(String email) {
        Assert.notBlank(email, "email cannot be empty.");
    }

    private void checkTelePhone(String telePhone) {
        Assert.notBlank(telePhone, "telePhone cannot be empty.");
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"userAccount"};
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(getId())
                .append(getUserAccount())
                .append(getEmail())
                .append(getTelePhone())
                .append(getName())
                .build();
    }

    public String getUserAccount() {
        return userAccount;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getTelePhone() {
        return telePhone;
    }

    public String getSalt() {
        return salt;
    }
}