package telerik.project.services.contracts;

import telerik.project.models.Tag;
import telerik.project.models.User;

import java.util.List;

public interface TagService {

    List<Tag> getAll();

    Tag getById(Long targetTagId);

    Tag getByName(String name);

    Tag createIfNotExists(String name);

    void update(Long targetTagId, Tag updatedTag);

    void delete(Long targetTagId);
}
