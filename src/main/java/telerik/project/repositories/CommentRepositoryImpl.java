package telerik.project.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Comment;
import telerik.project.repositories.contracts.CommentRepository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery(
                    "from Comment c where c.isDeleted = false", Comment.class);
            return query.list();
        }
    }

    @Override
    public Comment getById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, id);
            if (comment == null) {
                throw new EntityNotFoundException("Comment", id);
            }
            return comment;
        }
    }

    @Override
    public long countByPostId(Long postId) {
        try(Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "select count(c) from Comment c where c.post.id = :postId", Long.class)
                    .setParameter("postId", postId);
            Long result = query.uniqueResult();
            return result != null ? result : 0L;
        }
    }

    @Override
    public void create(Comment comment) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Comment comment) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        Comment commentToDelete = getById(id);
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(commentToDelete);
            session.getTransaction().commit();
        }
    }
}
