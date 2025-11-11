package telerik.project.repositories.contracts;

import telerik.project.models.Notification;

import java.util.List;

public interface NotificationRepository {

    List<Notification> getAll();

    Notification getById(Long id);

    void create(Notification notification);

    void update(Notification notification);

    void delete(Long id);
}
