package telerik.project.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Post;
import telerik.project.repositories.contracts.PostRepository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Post getById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post");
            }
            return post;
        }
    }

    @Override
    public List<Post> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post p where p.isDeleted = false", Post.class);
            return query.list();
        }
    }

    @Override
    public long countByAuthorId(Long authorId) {
        try(Session session = sessionFactory.openSession()) {
           Query<Long> query = session.createQuery(
                   "select count(p) from Post p where p.author.id = :authorId", Long.class);
           query.setParameter("authorId", authorId);
           return query.uniqueResult();
        }
    }

    @Override
    public void create(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post");
            }
            session.remove(post);
            session.getTransaction().commit();
        }
    }

    private Post getSingleResultByField(String fieldName, String value) {
        try(Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post where " + fieldName + " = :value", Post.class);
            query.setParameter("value", value);
            return query.uniqueResultOptional().orElseThrow(() -> new EntityNotFoundException(fieldName));
        }
    }
}
