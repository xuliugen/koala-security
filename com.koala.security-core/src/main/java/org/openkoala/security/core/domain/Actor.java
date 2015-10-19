package org.openkoala.security.core.domain;

import org.dayatang.utils.Assert;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 参与者, 它是一个抽象的概念。
 * 是 {@link User} 和  <code>UserGroup </code>的共同基类。
 * 可以对该类进行扩展，以达到您所希望的业务。
 * 可以对其授予角色 {@link Role} 和 权限 {@link Permission}。
 * @author lucas
 */
@Entity
@Table(name = "KS_ACTORS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
public abstract class Actor extends SecurityAbstractEntity {

    private static final long serialVersionUID = -6279345771754150467L;

    /**
     * 名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 最后更新时间
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_MODIFY_TIME")
    private Date lastModifyTime;

    /**
     * 创建者
     */
    @Column(name = "CREATE_OWNER")
    private String createOwner;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATE_DATE")
    private Date createDate = new Date();

    /**
     * 描述
     */
    @Column(name = "DESCRIPTION")
    private String description;

    public Actor() {
    }

    public Actor(String name) {
        checkName(name);
        this.name = name;
    }

    /**
     * 撤销~级联撤销{@link Authorization }。
     * 因为参与者都已经被撤销，那么Authorization中只要有参与者就应该被撤销。
     */
    @Override
    public void remove() {
        for (Authorization authorization : Authorization.findByActor(this)) {
            authorization.remove();
        }
        super.remove();
    }

    /**
     * 在某个范围下{@link Scope}为参与者{@link Actor}分配授权{@link Authority}。
     * 如果Authorization中已经存在Actor和Authority,就不需要授权，如果范围不为空，那么就更改范围。
     * 不存在就直接创建Authorization并保存。
     * @param authority 可授权体
     * @param scope     范围
     */
    public void grant(Authority authority, Scope scope) {
        if (Authorization.exists(this, authority)) {
            if (scope != null) {
                Authorization authorization = Authorization.findByActorInAuthority(this, authority);
                authorization.changeScope(scope);
            }
            return;
        }
        new Authorization(this, authority, scope).save();
    }

    /**
     * 从参与者中撤销在某个范围下的授权。即撤销授权中心 {@link Authorization}。
     * @param authority 授权 {@like Authority}
     * @param scope     范围 {@link Scope}
     */
    public void terminateAuthorityInScope(Authority authority, Scope scope) {
        Authorization authorization = Authorization.findByActorOfAuthorityInScope(this, authority, scope);
        authorization.remove();
    }

    /**
     * 为参与者分配授权。
     * 如果存在就直接返回，不存在就创建一个Authorization并保存。
     * @param authority 授权 {@like Authority}
     */
    public void grant(Authority authority) {
        if (Authorization.exists(this, authority)) {
            return;
        }
        new Authorization(this, authority).save();
    }

    /**
     * 从参与者中撤销授权，即撤销授权中心 {@link Authorization}。
     * @param authority 授权 {@like Authority}
     */
    public void terminate(Authority authority) {
        Authorization authorization = Authorization.findByActorInAuthority(this, authority);
        authorization.remove();
    }

    /**
     * 得到在某个范围下{@link Scope}参与者{@link Actor}的所有不重复的权限集合{@link Permission}
     * @param scope 范围
     * @return 不重复的权限集合 {@link Permission}
     */
    public Set<Permission> getPermissions(Scope scope) {
        Set<Permission> results = new HashSet<Permission>();
        for (Authority authority : getAuthorities(scope)) {
            if (authority instanceof Permission) {
                results.add((Permission) authority);
            } else {
                Role role = (Role) authority;
                results.addAll(role.getPermissions());
            }
        }
        return results;
    }

    /**
     * 更改最后修改时间。
     */
    public void changeLastModifyTime() {
        this.lastModifyTime = new Date();
    }

    /**
     * 根据参与者ID得到参与者
     * @param actorId 参与者ID
     * @param <T>     继承参与者
     * @return 参与者或者其子类
     */
    public static <T extends Actor> T getActorBy(Long actorId) {
        return (T) Actor.get(Actor.class, actorId);
    }

    protected void checkName(String name) {
        Assert.notBlank(name, "name cannot be empty.");
    }

	/*------------- Private helper methods  -----------------*/

    /**
     * 根据范围获取到所有不重复的授权集合。
     * @param scope 范围 {@link Scope}
     * @return 不重复的授权集合 {@link Authorization}
     */
    private Set<Authority> getAuthorities(Scope scope) {
        return Authorization.findAuthoritiesByActorInScope(this, scope);
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"name"};
    }

    /*-------------- getter setter methods  ------------------*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public String getCreateOwner() {
        return createOwner;
    }

    public void setCreateOwner(String createOwner) {
        this.createOwner = createOwner;
    }

    public Date getCreateDate() {
        return createDate;
    }
}
