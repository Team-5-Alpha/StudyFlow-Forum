package telerik.project.repositories.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import telerik.project.models.Post;
import telerik.project.models.Tag;
import telerik.project.models.filters.PostFilterOptions;

import java.util.ArrayList;
import java.util.List;

public class PostSpecifications {

    public static Specification<Post> withFilters(PostFilterOptions filterOptions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getTitle().ifPresent(title ->
                    predicates.add(titleContains(title).toPredicate(root, query, cb)));

            filterOptions.getContentKeyword().ifPresent(content ->
                    predicates.add(contentContains(content).toPredicate(root, query, cb)));

            filterOptions.getAuthorId().ifPresent(authorId ->
                    predicates.add(authorIdEquals(authorId).toPredicate(root, query, cb)));

            filterOptions.getTag().ifPresent(tag ->
                    predicates.add(hasTag(tag).toPredicate(root, query, cb)));

            filterOptions.getIsDeleted().ifPresent(isDeleted ->
                    predicates.add(isDeletedEquals(isDeleted).toPredicate(root, query, cb)));

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Post> titleContains(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Post> contentContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
    }

    private static Specification<Post> authorIdEquals(Long authorId) {
        return (root, query, cb) ->
                cb.equal(root.get("author").get("id"), authorId);
    }

    private static Specification<Post> hasTag(String tagName) {
        return (root, query, cb) -> {
            Join<Post, Tag> join = root.join("tags");
            return cb.equal(cb.lower(join.get("name")), tagName.toLowerCase());
        };
    }

    private static Specification<Post> isDeletedEquals(Boolean isDeleted) {
        return (root, query, cb) ->
                cb.equal(root.get("isDeleted"), isDeleted);
    }

    public static Sort buildSort(PostFilterOptions filterOptions) {

        if (filterOptions.getSortBy().isEmpty()) {
            return Sort.unsorted();
        }

        String sortBy = filterOptions.getSortBy().get();
        Sort.Direction direction = filterOptions.getSortOrder()
                .filter(order -> order.equalsIgnoreCase("desc"))
                .map(order -> Sort.Direction.DESC).orElse(Sort.Direction.ASC);

        return switch (sortBy) {
            case "title", "createdAt", "updatedAt" -> Sort.by(direction, sortBy);
            default -> Sort.unsorted();
        };
    }

}
