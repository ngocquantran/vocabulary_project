package com.example.myvocab.filter;

import com.example.myvocab.model.Orders;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OderSpecificationBuilder {
    private final List<SearchCriteria> params;

    public OderSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final OderSpecificationBuilder with(SearchCriteria
                                                       searchCriteria) {
        params.add(searchCriteria);
        return this;
    }


    public OderSpecificationBuilder with(String key, String operation, Object value) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        params.add(new SearchCriteria(key, op, value));

        return this;
    }

    public Specification<Orders> build() {
        if (params.size() == 0) {
            return null;
        }
        List<Specification> specs = params.stream()
                .map(OrderSpecification::new)
                .collect(Collectors.toList());

        Specification<Orders> result = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
            result = Specification.where(result).or(specs.get(i));
        }
        return result;
    }


}
