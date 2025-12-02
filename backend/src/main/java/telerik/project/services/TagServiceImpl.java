package telerik.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import telerik.project.exceptions.EntityDuplicateException;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.models.Tag;
import telerik.project.repositories.TagRepository;
import telerik.project.services.contracts.TagService;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getById(Long targetTagId) {
        return tagRepository.findById(targetTagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag", targetTagId));
    }

    @Override
    public Tag getByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Tag", "name", name));
    }

    @Override
    public Tag createIfNotExists(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    return tagRepository.save(newTag);
                });
    }

    @Override
    public void update(Long targetTagId, Tag updatedTag) {
        AuthorizationHelper.validateAdmin();

        Tag existing = getById(targetTagId);

        if(tagRepository.existsByNameIgnoreCase(updatedTag.getName())
                && !existing.getName().equalsIgnoreCase(updatedTag.getName())) {
            throw new EntityDuplicateException("Tag", "name", updatedTag.getName());
        }

        existing.setName(updatedTag.getName());
        tagRepository.save(existing);
    }

    @Override
    public void delete(Long targetTagId) {
        AuthorizationHelper.validateAdmin();
        tagRepository.delete(getById(targetTagId));
    }
}
