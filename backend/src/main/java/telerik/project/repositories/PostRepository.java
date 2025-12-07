package telerik.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import telerik.project.models.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthor_Id(Long authorId);

    List<Post> findByTags_Name(String tagName);

    List<Post> findByLikedByUsers_Id(Long userId);

    @Query("""
       SELECT p
       FROM Post p
       LEFT JOIN p.comments c
       GROUP BY p
       ORDER BY COUNT(c) DESC
       """)
    List<Post> findMostCommented();

    long countByAuthor_Id(Long authorId);
}
