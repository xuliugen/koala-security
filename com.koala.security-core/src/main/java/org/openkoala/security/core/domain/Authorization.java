package org.openkoala.security.core.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dayatang.domain.CriteriaQuery;
import org.dayatang.utils.Assert;
import org.openkoala.security.core.AuthorizationIsNotExisted;

/**
 * 授权中心，在指定范围{@link Scope}将授权{@link Authority}授予参与者{@link Actor}。
 *
 * @author lucas
 */
@Entity
@Table(name = "KS_AUTHORIZATIONS")
@NamedQueries({
        @NamedQuery(name = "Authorization.findAuthoritiesByActor", query = "SELECT _authority FROM Authorization _authorization JOIN _authorization.authority _authority JOIN _authorization.actor _actor WHERE _actor = :actor AND TYPE(_authority) = :authorityType")
})
public class Authorization extends SecurityAbstractEntity {

    private static final long serialVersionUID = -7604610067031217444L;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ACTOR_ID")
    private Actor actor;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "AUTHORITY_ID")
    private Authority authority;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "SCOPE_ID")
    private Scope scope;

    protected Authorization() {
    }

    public Authorization(Actor actor, Authority authority) {
        Assert.notNull(actor, "actor cannot be empty.");
        Assert.notNull(authority, "authority cannot be empty.");
        this.actor = actor;
        this.authority = authority;
    }

    public Authorization(Actor actor, Authority authority, Scope scope) {
        this(actor, authority);
        Assert.notNull(scope, "scope cannot be empty.");
        this.scope = scope;
    }

    /**
     * 保存授权中心。
     * 如果存在就直接返回。
     */
    @Override
    public void save() {
        if (exists(actor, authority, scope)) {
            return;
        }
        super.save();
    }

    /**
     * 更改授权中心的范围。
     *
     * @param scope 范围
     */
    public void changeScope(Scope scope) {
        Assert.notNull(scope,null);
        this.scope = scope;
        this.save();
    }

    public static List<Authorization> findByActor(Actor actor) {
        return getRepository().createCriteriaQuery(Authorization.class)
                .eq("actor", actor)
                .list();
    }

    public static List<Authorization> findByAuthority(Authority authority) {
        return getRepository().createCriteriaQuery(Authorization.class)
                .eq("authority", authority)
                .list();
    }

    public static Set<Authority> findAuthoritiesByActorInScope(Actor actor, Scope scope) {
        Set<Authority> results = new HashSet<Authority>();
        Set<Authorization> authorizations = findAuthorizationsByActor(actor);
        for (Authorization authorization : authorizations) {
            if (authorization.getScope().contains(scope)) {
                results.add(authorization.getAuthority());
            }
        }
        return results;
    }

    public static Set<Authority> findAuthoritiesByActor(Actor actor) {
        Set<Authority> results = new HashSet<Authority>();
        Set<Authorization> authorizations = findAuthorizationsByActor(actor);
        for (Authorization authorization : authorizations) {
            if (authorization.getAuthority() instanceof Role) {
                Role role = (Role) authorization.getAuthority();
                results.addAll(role.getPermissions());
            }
            results.add(authorization.getAuthority());
        }
        return results;
    }

    public static Authorization findByActorInAuthority(Actor actor, Authority authority) {
        return getRepository()
                .createCriteriaQuery(Authorization.class)
                .eq("actor", actor)
                .eq("authority", authority)
                .singleResult();
    }

    public static void checkAuthorization(Actor actor, Authority authority) {
        if (!exists(actor, authority)) {
            throw new AuthorizationIsNotExisted();
        }
    }

    public static Authorization findByActorOfAuthorityInScope(Actor actor, Authority authority, Scope scope) {
        return getRepository()
                .createCriteriaQuery(Authorization.class)
                .eq("actor", actor)
                .eq("authority", authority)
                .eq("scope", scope)
                .singleResult();
    }

    /**
     * 判断参与者是否已经被授予了在某个范围下的授权。
     */
    protected static boolean exists(Actor actor, Authority authority, Scope scope) {
        CriteriaQuery criteriaQuery = new CriteriaQuery(getRepository(), Authorization.class);
        criteriaQuery.eq("actor", actor);
        criteriaQuery.eq("authority", authority);
        if (scope != null) {
            criteriaQuery.eq("scope", scope);
        }
        return criteriaQuery.singleResult() != null;
    }

    protected static boolean exists(Actor actor, Authority authority) {
        return exists(actor, authority, null);
    }

    private static Set<Authorization> findAuthorizationsByActor(Actor actor) {
        Set<Authorization> results = new HashSet<Authorization>();
        List<Authorization> authorizations = getRepository().createCriteriaQuery(Authorization.class)
                .eq("actor", actor)
                .list();
        results.addAll(authorizations);
        return results;
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"actor", "authority"};
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(actor)
                .append(authority)
                .append(scope)
                .build();
    }

    public Actor getActor() {
        return actor;
    }

    public Authority getAuthority() {
        return authority;
    }

    public Scope getScope() {
        return scope;
    }
}
