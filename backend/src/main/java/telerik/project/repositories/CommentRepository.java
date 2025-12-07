package telerik.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import telerik.project.models.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    List<Comment> findByPostId(Long postId);

    List<Comment> findByAuthor_Id(Long authorId);

    List<Comment> findByParentCommentId(Long parentCommentId);

    long countByPostId(Long postId);

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}
