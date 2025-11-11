package telerik.project.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.Notification;
import telerik.project.repositories.contracts.NotificationRepository;

import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public NotificationRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Notification getById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            return session.byId(Notification.class)
                    .loadOptional(id)
                    .orElseThrow(() -> new EntityNotFoundException("id"));
        }
    }

    @Override
    public List<Notification> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<Notification> query = session.createQuery("from Notification", Notification.class);
            return query.list();
        }
    }

    @Override
    public void create(Notification notification) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(notification);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Notification notification) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(notification);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        Notification notification = getById(id);
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(notification);
            session.getTransaction().commit();
        }
    }
}
