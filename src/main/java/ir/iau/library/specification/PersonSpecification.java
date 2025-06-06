package ir.iau.library.specification;


import ir.iau.library.dto.PersonFilterDto;
import ir.iau.library.entity.Person;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PersonSpecification {
    public static Specification<Person> filter(PersonFilterDto criteria) {
        return (Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getActive() != null) {
                predicates.add(cb.equal(root.get("active"), criteria.getActive()));
            }

            if (StringUtils.hasText(criteria.getFirstName())) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + criteria.getFirstName().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.getLastName())) {
                predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + criteria.getLastName().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.getEmail())) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + criteria.getEmail().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.getPhone())) {
                predicates.add(cb.like(root.get("phone"), "%" + criteria.getPhone() + "%"));
            }
            if (StringUtils.hasText(criteria.getNationalId())) {
                predicates.add(cb.like(root.get("nationalId"), "%" + criteria.getNationalId() + "%"));
            }
            if (StringUtils.hasText(criteria.getMembershipType())) {
                predicates.add(cb.equal(cb.lower(root.get("membershipType")), criteria.getMembershipType().toLowerCase()));
            }
            if (StringUtils.hasText(criteria.getAddress())) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + criteria.getAddress().toLowerCase() + "%"));
            }
            if (criteria.getMembershipDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("membershipDate"), criteria.getMembershipDateFrom()));
            }
            if (criteria.getMembershipDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("membershipDate"), criteria.getMembershipDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}