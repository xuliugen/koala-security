package org.openkoala.security.core.domain;

import java.util.Set;

import javax.persistence.*;

/**
 * 代表某种范围，例如组织机构范围。
 *
 * @author lucas
 */
@Entity
@Table(name = "KS_SCOPES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
public abstract class Scope extends SecurityAbstractEntity {

    private static final long serialVersionUID = -7219997981491797461L;

    @Column(name = "NAME")
    private String name;

    @Transient
    public abstract Scope getParent();

    @Transient
    public abstract Set<? extends Scope> getChildren();

    protected Scope() {
    }

    public Scope(String name) {
        this.name = name;
    }

    public static <T extends Scope> T getBy(Long scopeId) {
        return (T) Scope.get(Scope.class, scopeId);
    }

    public boolean contains(Scope scope) {
        if (scope == null) {
            return false;
        }
        if (equals(scope)) {
            return true;
        }
        if (getChildren().contains(scope)) {
            return true;
        }
        return contains(scope.getParent());
    }

    public String getName() {
        return name;
    }
}