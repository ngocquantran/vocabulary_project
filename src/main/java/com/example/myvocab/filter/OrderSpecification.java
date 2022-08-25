package com.example.myvocab.filter;

import com.example.myvocab.model.Orders;
import com.example.myvocab.model.Package;
import com.example.myvocab.model.Users;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class OrderSpecification implements Specification<Orders> {
    private final SearchCriteria criteria;

    public OrderSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Orders> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                if (criteria.getKey().equals("fullName") || criteria.getKey().equals("email")) {
                    Join<Orders, Users> usersJoin = root.join("user");
                    return builder.like(builder.lower(usersJoin.get(criteria.getKey())), "%" + criteria.getValue().toString() + "%");
                } else if (criteria.getKey().equals("packageTitle")) {
                    Join<Orders, Package> usersJoin = root.join("aPackage");
                    return builder.like(builder.lower(usersJoin.get("title")), "%" + criteria.getValue().toString() + "%");
                }
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(root.get(
                        criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(root.get(
                        criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.get(
                        criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                if (criteria.getKey().equals("fullName") || criteria.getKey().equals("email")) {
                    Join<Orders, Users> usersJoin = root.join("user");
                    return builder.like(builder.lower(usersJoin.get(criteria.getKey())), "%" + criteria.getValue().toString() + "%");
                } else if (criteria.getKey().equals("PackageTitle")) {
                    Join<Orders, Package> usersJoin = root.join("aPackage");
                    return builder.like(builder.lower(usersJoin.get(criteria.getKey())), "%" + criteria.getValue().toString() + "%");
                }
                return builder.like(root.get(
                        criteria.getKey()), "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
