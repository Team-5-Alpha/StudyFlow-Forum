package telerik.project.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telerik.project.exceptions.EntityDuplicateException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.repositories.TagRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.TagService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getById(Long targetTagId) {
        return tagRepository.findById(targetTagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag", targetTagId));
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Tag", "name", name));
    }

    @Override
    @Transactional
    public Tag createIfNotExists(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    return tagRepository.save(newTag);
                });
    }

    @Override
    @Transactional
    public void update(Long targetTagId, Tag updatedTag) {
        User actingUser = getActingUser();
        AuthorizationHelper.validateAdmin(actingUser);

        Tag existing = getById(targetTagId);

        boolean nameTakenByAnother =
                tagRepository.existsByNameIgnoreCase(updatedTag.getName())
                        && !existing.getName().equalsIgnoreCase(updatedTag.getName());

        if (nameTakenByAnother) {
            throw new EntityDuplicateException("Tag", "name", updatedTag.getName());
        }

        existing.setName(updatedTag.getName());
        tagRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long targetTagId) {
        User actingUser = getActingUser();
        AuthorizationHelper.validateAdmin(actingUser);

        Tag target = getById(targetTagId);
        tagRepository.delete(target);
    }

    private User getActingUser() {
        Long id = SecurityContextUtil.getCurrentUserId();
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }
}