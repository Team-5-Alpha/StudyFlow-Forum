package telerik.project.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Tag;
import telerik.project.repositories.contracts.TagRepository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tag getById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            return session.byId(Tag.class)
                    .loadOptional(id)
                    .orElseThrow(() -> new EntityNotFoundException("id"));
        }
    }

    @Override
    public Tag getByName(String name) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Tag where name = :name", Tag.class)
                    .setParameter("name", name)
                    .uniqueResultOptional()
                    .orElseThrow(() -> new EntityNotFoundException("name"));
        }
    }

    @Override
    public List<Tag> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("from Tag", Tag.class);
            return query.list();
        }
    }

    @Override
    public void create(Tag tag) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Tag tag) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        Tag tagToDelete = getById(id);
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(tagToDelete);
            session.getTransaction().commit();
        }
    }
}
