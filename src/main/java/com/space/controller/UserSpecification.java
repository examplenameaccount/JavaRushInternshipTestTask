package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;

public class UserSpecification implements Specification<Ship> {
    private SearchCriteria criteria;

    public UserSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            if (root.get(criteria.getKey()).getJavaType() == (Date.class)) {
                Date date = new Date(Long.parseLong(criteria.getValue().toString()));
                return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), date);
            }
            else {
                return builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            }

        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            if (root.get(criteria.getKey()).getJavaType() == (Date.class)) {
                Date date = new Date(Long.parseLong(criteria.getValue().toString()));
                return builder.lessThanOrEqualTo(root.get(criteria.getKey()), date);
            }
            else
                return builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            }
            else if (root.get(criteria.getKey()).getJavaType() == Date.class) {
                Date date = new Date(Long.parseLong(criteria.getValue().toString()));
                return builder.equal(root.get(criteria.getKey()), date);
            }
            else if (root.get(criteria.getKey()).getJavaType() == Boolean.class) {
                Boolean bool = new Boolean(criteria.getValue().toString());
                return builder.equal(root.get(criteria.getKey()), bool);
            }
            else if (root.get(criteria.getKey()).getJavaType() == ShipType.class) {
                ShipType shipType = ShipType.valueOf(criteria.getValue().toString());
                return builder.equal(root.get(criteria.getKey()), shipType);
            }
        }
        return null;
    }
}