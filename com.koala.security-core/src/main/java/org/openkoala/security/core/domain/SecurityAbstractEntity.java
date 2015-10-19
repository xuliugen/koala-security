package org.openkoala.security.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.dayatang.domain.Entity;
import org.dayatang.domain.EntityRepository;
import org.dayatang.domain.InstanceFactory;
import org.dayatang.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 抽象实体类，可作为所有领域实体的基类，提供ID和版本属性。
 * @author yang
 */
@MappedSuperclass
public abstract class SecurityAbstractEntity implements Entity {

    private static final long serialVersionUID = 8882145540383345037L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAbstractEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Version
    @Column(name = "VERSION")
    private int version;


    /**
     * 获得实体的标识
     * @return 实体的标识
     */

    public Long getId() {
        return id;
    }

    /**
     * 设置实体的标识
     * @param id 要设置的实体标识
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获得实体的版本号。持久化框架以此实现乐观锁。
     * @return 实体的版本号
     */
    public int getVersion() {
        return version;
    }

    /**
     * 设置实体的版本号。持久化框架以此实现乐观锁。
     * @param version 要设置的版本号
     */
    public void setVersion(int version) {
        this.version = version;
    }

    @Transient
    public boolean isNew() {
        return id == null || id.intValue() == 0;
    }

    @Override
    public boolean existed() {
        if (isNew()) {
            return false;
        }
        return getRepository().exists(getClass(), id);
    }

    @Override
    public boolean notExisted() {
        return !existed();
    }

    public boolean existed(String propertyName, Object propertyValue) {
        List<?> entities = getRepository().createCriteriaQuery(getClass()).eq(propertyName, propertyValue).list();
        return !(entities.isEmpty());
    }

    private static EntityRepository repository;

    public static EntityRepository getRepository() {
        if (repository == null) {
            repository = InstanceFactory.getInstance(EntityRepository.class, "repository_security");
        }
        return repository;
    }

    public static void setRepository(EntityRepository repository) {
        SecurityAbstractEntity.repository = repository;
    }

    public void save() {
        getRepository().save(this);
    }

    public void remove() {
        getRepository().remove(this);
    }

    /**
     * 请改用每个实体对象的实例方法的existed()方法。
     * @param clazz
     * @param id
     * @return
     */
    @Deprecated
    public static <T extends Entity> boolean exists(Class<T> clazz, Serializable id) {
        return getRepository().exists(clazz, id);
    }

    public static <T extends Entity> T get(Class<T> clazz, Serializable id) {
        return getRepository().get(clazz, id);
    }

    public static <T extends Entity> T getUnmodified(Class<T> clazz, T entity) {
        return getRepository().getUnmodified(clazz, entity);
    }

    public static <T extends Entity> T load(Class<T> clazz, Serializable id) {
        return getRepository().load(clazz, id);
    }

    public static <T extends Entity> List<T> findAll(Class<T> clazz) {
        return getRepository().findAll(clazz);
    }

    /**
     * 获取业务主键。业务主键是判断相同类型的两个实体在业务上的等价性的依据。如果相同类型的两个
     * 实体的业务主键相同，则认为两个实体是相同的，代表同一个实体。
     * <p>业务主键由实体的一个或多个属性组成。
     * @return 组成业务主键的属性的数组。
     */
    public abstract String[] businessKeys();

    /**
     * 依据业务主键获取哈希值。用于判定两个实体是否等价。
     * 等价的两个实体的hashCode相同，不等价的两个实体hashCode不同。
     * @return 实体的哈希值
     */
    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(13, 37);
        Map<String, Object> propValues = new BeanUtils(this).getPropValues();

        for (String businessKey : businessKeys()) {
            builder = builder.append(propValues.get(businessKey));
        }
        return builder.toHashCode();
    }

    /**
     * 依据业务主键判断两个实体是否等价。
     * @param other 另一个实体
     * @return 如果本实体和other等价则返回true, 否则返回false
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (businessKeys() == null || businessKeys().length == 0) {
            return false;
        }
        if (!(this.getClass().isAssignableFrom(other.getClass()))) {
            return false;
        }
        Map<String, Object> thisPropValues = new BeanUtils(this).getPropValuesExclude(Transient.class);
        Map<String, Object> otherPropValues = new BeanUtils(other).getPropValuesExclude(Transient.class);
        EqualsBuilder builder = new EqualsBuilder();
        for (String businessKey : businessKeys()) {
            builder.append(thisPropValues.get(businessKey), otherPropValues.get(businessKey));
        }
        return builder.isEquals();
    }
}