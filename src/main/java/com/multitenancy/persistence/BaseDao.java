package com.multitenancy.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDao<T, PK extends Serializable> {
    protected static final Logger log = LoggerFactory.getLogger(BaseDao.class);

    protected Class<T> persistentClass;
    protected @PersistenceContext EntityManager entityManager;

    public BaseDao(final Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public T find(PK id) {
        return entityManager.find(persistentClass, id);
    }

    public void persist(T entity) {
        entityManager.persist(entity);
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public void remove(PK id) {
        entityManager.remove(find(id));
    }

    public List<T> findAll() {
        log.debug("finding all {} instances", this.persistentClass);

        String sql = "from " + this.persistentClass.getName() + " model order by model.id desc";

        Query query = entityManager.createQuery(sql, persistentClass);

        @SuppressWarnings("unchecked")
        List<T> list = query.getResultList();

        return list;
    }

    public List<T> findByProperty(String propertyName, Object value) {
        log.debug("finding {} instance with property: {}, value : {}", this.persistentClass, propertyName, value);

        String sql = "from " + this.persistentClass.getName() + " model where model." + propertyName + " = :"
                + propertyName;

        Query query = entityManager.createQuery(sql, persistentClass);
        query.setParameter(propertyName, value);

        @SuppressWarnings("unchecked")
        List<T> list = query.getResultList();

        return list;
    }

    public T findUniqueByProperty(String propertyName, Object value) {
        log.debug("finding Unique {} instance with property: {}, value : {}", this.persistentClass, propertyName, value);

        T result = null;
        List<T> list = findByProperty(propertyName, value);
        if (null != list && list.size() == 1) {
            result = list.get(0);
        }

        return result;
    }

    public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams, Integer page, Integer size) {
        TypedQuery<T> namedQuery = entityManager.createNamedQuery(queryName, persistentClass);
        if (null != queryParams) {
            for (String s : queryParams.keySet()) {
                Object obj = queryParams.get(s);
                namedQuery.setParameter(s, obj);
            }
        }
        if (page != null && page > 0 && size != null) {
            namedQuery.setFirstResult((page - 1) * size);
            namedQuery.setMaxResults(size);
        }

        return namedQuery.getResultList();
    }

    public List<T> findByNamedQuery(String queryName) {
        return findByNamedQuery(queryName, null, null, null);
    }

    public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
        return findByNamedQuery(queryName, queryParams, null, null);
    }

}