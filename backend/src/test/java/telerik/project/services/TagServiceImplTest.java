package telerik.project.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import telerik.project.exceptions.EntityDuplicateException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.models.Role;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.repositories.TagRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.security.auth.SecurityContextUtil;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private MockedStatic<SecurityContextUtil> securityContextUtilMock;
    private MockedStatic<AuthorizationHelper> authorizationHelperMock;

    @BeforeEach
    void setUp() {
        securityContextUtilMock = mockStatic(SecurityContextUtil.class);
        authorizationHelperMock = mockStatic(AuthorizationHelper.class);
    }

    @AfterEach
    void tearDown() {
        securityContextUtilMock.close();
        authorizationHelperMock.close();
    }

    private User buildAdmin(Long id) {
        User u = new User();
        u.setId(id);
        u.setRole(Role.ADMIN);
        return u;
    }

    @Test
    void getAll_ReturnsListFromRepository() {
        when(tagRepository.findAll()).thenReturn(List.of(new Tag(), new Tag()));

        var result = tagService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getById_WhenExists_ReturnsTag() {
        Tag t = new Tag();
        t.setId(1L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(t));

        Tag result = tagService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_WhenMissing_Throws() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.getById(1L));
    }

    @Test
    void getByName_WhenExists_ReturnsTag() {
        Tag t = new Tag();
        t.setName("java");

        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(t));

        Tag result = tagService.getByName("java");

        assertEquals("java", result.getName());
    }

    @Test
    void getByName_WhenMissing_Throws() {
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> tagService.getByName("java"));
    }

    @Test
    void createIfNotExists_WhenTagExists_ReturnsExisting() {
        Tag existing = new Tag();
        existing.setName("java");

        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.of(existing));

        Tag result = tagService.createIfNotExists("java");

        assertSame(existing, result);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void createIfNotExists_WhenMissing_CreatesNew() {
        when(tagRepository.findByNameIgnoreCase("java"))
                .thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        Tag result = tagService.createIfNotExists("java");

        assertEquals("java", result.getName());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void update_AdminChangesName_WhenNameNotTaken() {
        User admin = buildAdmin(1L);
        Tag existing = new Tag();
        existing.setId(10L);
        existing.setName("old");

        Tag updated = new Tag();
        updated.setName("new");

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        authorizationHelperMock.when(() -> AuthorizationHelper.validateAdmin(admin))
                .then(inv -> null);

        when(tagRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(tagRepository.existsByNameIgnoreCase("new")).thenReturn(false);

        tagService.update(10L, updated);

        assertEquals("new", existing.getName());
        verify(tagRepository).save(existing);
    }

    @Test
    void update_WhenNameTakenByAnother_ThrowsDuplicateException() {
        User admin = buildAdmin(1L);
        Tag existing = new Tag();
        existing.setId(10L);
        existing.setName("old");

        Tag updated = new Tag();
        updated.setName("new");

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        authorizationHelperMock.when(() -> AuthorizationHelper.validateAdmin(admin))
                .then(inv -> null);

        when(tagRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(tagRepository.existsByNameIgnoreCase("new")).thenReturn(true);

        assertThrows(EntityDuplicateException.class,
                () -> tagService.update(10L, updated));
    }

    @Test
    void delete_AdminDeletesTag() {
        User admin = buildAdmin(1L);
        Tag tag = new Tag();
        tag.setId(10L);

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        authorizationHelperMock.when(() -> AuthorizationHelper.validateAdmin(admin))
                .then(inv -> null);
        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));

        tagService.delete(10L);

        verify(tagRepository).delete(tag);
    }
}