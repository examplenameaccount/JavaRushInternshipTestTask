package com.space.controller;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSpecificationsBuilder {

    private final List<SearchCriteria> params;

    public UserSpecificationsBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public UserSpecificationsBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<Ship> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(UserSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);
        for (int i = 1; i < params.size(); i++) {
            params.get(i).setOrPredicate(true);
            result = Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}
