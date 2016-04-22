package com.multitenancy.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.multitenancy.entity.CustomerEntity;
import com.multitenancy.persistence.BaseDao;

@Repository
public class CustomerDao extends BaseDao<CustomerEntity, Long> {
    public static final String EMAIL = "email";

    public CustomerDao() {
        super(CustomerEntity.class);
    }

    public CustomerEntity findByJsonb1() {
        CustomerEntity customerEntity = null;
        
        List<CustomerEntity> result = findByNamedQuery("findByJsonB1");
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }
    
    public CustomerEntity findByJsonb2() {
        CustomerEntity customerEntity = null;
        List<CustomerEntity> result = findByNamedQuery("findByJsonB2");
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }
    
    public CustomerEntity findByEmail(String email) {
        
        Query cb = entityManager.createQuery("select id, firstName from CustomerEntity where email = :email", CustomerEntity.class);
        cb.setParameter("email", email);
        return (CustomerEntity) cb.getSingleResult();
    }
}
