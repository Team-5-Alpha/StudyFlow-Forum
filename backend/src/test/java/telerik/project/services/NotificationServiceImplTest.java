package telerik.project.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.NotificationFactory;
import telerik.project.helpers.validators.NotificationValidationHelper;
import telerik.project.models.Notification;
import telerik.project.models.User;
import telerik.project.repositories.NotificationRepository;
import telerik.project.security.auth.SecurityContextUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private MockedStatic<SecurityContextUtil> securityContextUtilMock;
    private MockedStatic<AuthorizationHelper> authorizationHelperMock;
    private MockedStatic<NotificationValidationHelper> notificationValidationHelperMock;
    private MockedStatic<NotificationFactory> notificationFactoryMock;

    @BeforeEach
    void setUp() {
        securityContextUtilMock = mockStatic(SecurityContextUtil.class);
        authorizationHelperMock = mockStatic(AuthorizationHelper.class);
        notificationValidationHelperMock = mockStatic(NotificationValidationHelper.class);
        notificationFactoryMock = mockStatic(NotificationFactory.class);
    }

    @AfterEach
    void tearDown() {
        securityContextUtilMock.close();
        authorizationHelperMock.close();
        notificationValidationHelperMock.close();
        notificationFactoryMock.close();
    }

    @Test
    void getById_WhenExists_ReturnsNotification() {
        Notification n = new Notification();
        n.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        Notification result = notificationService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_WhenMissing_Throws() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> notificationService.getById(1L));
    }

    @Test
    void delete_DeletesNotification() {
        Notification n = new Notification();
        n.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.delete(1L);

        verify(notificationRepository).delete(n);
    }

    @Test
    void markAsRead_SetsFlagAndSaves() {
        Notification n = new Notification();
        n.setId(1L);
        n.setIsRead(false);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));
        notificationValidationHelperMock.when(() ->
                        NotificationValidationHelper.validateNotAlreadyRead(n))
                .then(inv -> null);

        notificationService.markAsRead(1L);

        assertTrue(n.getIsRead());
        verify(notificationRepository).save(n);
    }

    @Test
    void markAllAsRead_FindsUnreadAndSavesAll() {
        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(5L);

        Notification n1 = new Notification();
        n1.setIsRead(false);
        Notification n2 = new Notification();
        n2.setIsRead(false);

        when(notificationRepository.findByRecipient_IdAndIsReadFalse(5L))
                .thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead();

        assertTrue(n1.getIsRead());
        assertTrue(n2.getIsRead());
        verify(notificationRepository).saveAll(List.of(n1, n2));
    }

    @Test
    void send_UsesNotificationFactoryAndSaves() {
        User actor = new User();
        actor.setId(1L);
        User recipient = new User();
        recipient.setId(2L);

        Notification generated = new Notification();
        generated.setId(10L);

        notificationFactoryMock.when(() ->
                        NotificationFactory.create(actor, recipient, 5L, "POST", "LIKE"))
                .thenReturn(generated);

        notificationService.send(actor, recipient, 5L, "POST", "LIKE");

        verify(notificationRepository).save(generated);
    }
}