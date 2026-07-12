package com.ms3.shippingservice.infrastructure.persistency.specification;

import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ShippingSpecs {

    public static Specification<ShippingEntity> search(String branch, Object state, String category, String term, String name) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (branch != null && !branch.isEmpty()) {
                predicates.add(cb.or(
                        cb.equal(root.get("originBranch"), branch),
                        cb.equal(root.get("destinationBranch"), branch)
                ));
            }
            if (state != null) {
                predicates.add(cb.equal(root.get("currentState"), state));
            }
            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.join("categories").get("name"), category));
            }
            if (term != null && !term.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("trackingNumber")), "%" + term.toLowerCase() + "%"));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("remitente")), "%" + name.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("destinatario")), "%" + name.toLowerCase() + "%")
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}