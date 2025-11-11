package telerik.project.repositories.contracts;

import telerik.project.models.Notification;

import java.util.List;

public interface NotificationRepository {
    Notification getById(Long id);

    List<Notification> getAll();

//  List<Notification> getByFilter(NotificationFilter filter);

    void create(Notification notification);

    void update(Notification notification);

    void delete(Long id);
}
