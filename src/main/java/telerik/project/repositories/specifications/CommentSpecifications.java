package telerik.project.repositories.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import telerik.project.models.Comment;
import telerik.project.models.filters.CommentFilterOptions;

import java.util.ArrayList;
import java.util.List;

public class CommentSpecifications {
    public static Specification<Comment> withFilters(CommentFilterOptions filterOptions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            filterOptions.getPostId().ifPresent(postId ->
                    predicates.add(postIdEquals(postId).toPredicate(root, query, cb)));

            filterOptions.getAuthorId().ifPresent(authorId ->
                    predicates.add(authorIdEquals(authorId).toPredicate(root, query, cb)));

            filterOptions.getParentCommentId().ifPresent(parentCommentId ->
                    predicates.add(parentCommentIdEquals(parentCommentId).toPredicate(root, query, cb)));

            filterOptions.getIsDeleted().ifPresent(isDeleted ->
                    predicates.add(isDeletedEquals(isDeleted).toPredicate(root, query, cb)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Comment> postIdEquals(Long postId) {
        return (root, query, cb) ->
                cb.equal(root.get("post").get("id"), postId);
    }

    private static Specification<Comment> authorIdEquals(Long authorId) {
        return (root, query, cb) ->
                cb.equal(root.get("author").get("id"), authorId);
    }

    private static Specification<Comment> parentCommentIdEquals(Long parentCommentId) {
        return (root, query, cb) ->
                cb.equal(root.get("parentComment").get("id"), parentCommentId);
    }

    private static Specification<Comment> isDeletedEquals(Boolean isDeleted) {
        return (root, query, cb) ->
                cb.equal(root.get("isDeleted"), isDeleted);
    }

    public static Sort buildSort(CommentFilterOptions filterOptions) {
        if (filterOptions.getSortBy().isEmpty()) {
            return Sort.unsorted();
        }

        String sortBy = filterOptions.getSortBy().get();
        Sort.Direction direction = filterOptions.getSortOrder()
                .filter(order -> order.equalsIgnoreCase("desc"))
                .map(order -> Sort.Direction.DESC).orElse(Sort.Direction.ASC);

        return switch(sortBy) {
            case "createdAt", "updatedAt" -> Sort.by(direction, sortBy);
            default -> Sort.unsorted();
        };
    }
}
