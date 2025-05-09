package ir.iau.library.specification;

import ir.iau.library.dto.BookFilterDto;
import ir.iau.library.entity.Book;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookSpecification {
    public static Specification<Book> filter(BookFilterDto criteria) {
        return (Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getTitle() != null && !criteria.getTitle().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getTitle().toLowerCase() + "%"));
            }
            if (criteria.getAuthor() != null && !criteria.getAuthor().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("author")), "%" + criteria.getAuthor().toLowerCase() + "%"));
            }
            if (criteria.getSubject() != null && !criteria.getSubject().isEmpty()) {
                predicates.add(cb.equal(root.get("subject"), criteria.getSubject()));
            }
            if (criteria.getPublishedAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publicationDate"), criteria.getPublishedAfter()));
            }
            if (criteria.getPublishedBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publicationDate"), criteria.getPublishedBefore()));
            }
            if (criteria.getAvailable() != null) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Book> subRoot = sub.from(Book.class);
                Join<?, ?> loans = subRoot.join("loans", JoinType.LEFT);
                sub.select(subRoot.get("id"))
                        .where(cb.and(
                                cb.equal(subRoot.get("id"), root.get("id")),
                                cb.equal(loans.get("status"), ir.iau.library.entity.LoanStatus.ON_LOAN)
                        ));
                if (criteria.getAvailable()) {
                    predicates.add(cb.not(cb.exists(sub)));
                } else {
                    predicates.add(cb.exists(sub));
                }
            }

            predicates.add(cb.equal(root.get("active"), true));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}