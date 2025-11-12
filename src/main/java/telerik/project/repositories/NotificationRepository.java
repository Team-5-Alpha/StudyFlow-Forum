package telerik.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import telerik.project.models.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    List<Notification> findByRecipient_Id(Long recipientId);

    List<Notification> findByRecipient_IdAndIsReadFalse(Long recipientId);

    long countByRecipient_IdAndIsReadFalse(Long recipientId);
}
