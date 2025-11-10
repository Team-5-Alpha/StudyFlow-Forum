package telerik.project.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.models.User;
import telerik.project.repositories.contracts.UserRepository;

import java.util.List;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImp(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User");
            }
            return user;
        }
    }

    @Override
    public List<User> getAll() {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            return query.list();
        }
    }

    @Override
    public User getByUsername(String username) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("username");
            }
            return result.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try(Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            List<User> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("email");
            }
            return result.get(0);
        }
    }

    @Override
    public void create(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(User user) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        User userToDelete = getById(id);
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(userToDelete);
            session.getTransaction().commit();
        }
    }
}
