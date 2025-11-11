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
    public List<Post> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post p where p.isDeleted = false", Post.class);
            return query.list();
        }
    }

    @Override
    public Post getById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            return session.byId(Post.class)
                    .loadOptional(id)
                    .orElseThrow(() -> new EntityNotFoundException("Post", id));
        }
    }

    @Override
    public long countByAuthorId(Long authorId) {
        try(Session session = sessionFactory.openSession()) {
           Long result = session.createQuery(
                   "select count(p) from Post p where p.author.id = :authorId", Long.class)
                   .setParameter("authorId", authorId)
                   .uniqueResult();
           return result != null ? result : 0L;
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
        Post postToDelete = getById(id);
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(postToDelete);
            session.getTransaction().commit();
        }
    }
}
