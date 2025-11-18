package telerik.project.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private PaginationUtils() {}

    public static Pageable createPageable(Integer page, Integer size, Sort sort) {
        int safePage = (page == null || page < 0) ? DEFAULT_PAGE : page;
        int safeSize = (size == null || size <= 0) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);

        return PageRequest.of(
                safePage,
                safeSize,
                sort == null ? Sort.unsorted() : sort
        );
    }
}
