package telerik.project.repositories.specifications;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import telerik.project.models.User;
import telerik.project.models.filters.UserFilterOptions;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

    public static Specification<User> withFilters(UserFilterOptions filterOptions) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getUsername().ifPresent(username ->
                    predicates.add(usernameContains(username).toPredicate(root, query, criteriaBuilder)));

            filterOptions.getEmail().ifPresent(email ->
                    predicates.add(emailContains(email).toPredicate(root, query, criteriaBuilder)));

            filterOptions.getFirstName().ifPresent(firstName ->
                    predicates.add(firstNameContains(firstName).toPredicate(root, query, criteriaBuilder)));

            filterOptions.getIsBlocked().ifPresent(isBlocked ->
                    predicates.add(isBlockedEquals(isBlocked).toPredicate(root, query, criteriaBuilder)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<User> usernameContains(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%");
    }

    private static Specification<User> emailContains(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> firstNameContains(String firstName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%");
    }

    private static Specification<User> isBlockedEquals(Boolean isBlocked) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isBlocked"), isBlocked);
    }

    public static Sort buildSort(UserFilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return Sort.unsorted();
        }

        String sortBy = filterOptions.getSortBy().get();
        Sort.Direction direction = filterOptions.getSortOrder()
                .filter(order -> order.equalsIgnoreCase("desc"))
                .map(order -> Sort.Direction.DESC).orElse(Sort.Direction.ASC);

        return switch (sortBy) {
            case "username", "email", "firstName", "createdAt" -> Sort.by(direction, sortBy);
            default -> Sort.unsorted();
        };
    }
}
