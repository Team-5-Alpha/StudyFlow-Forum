package telerik.project.services.contracts;

import telerik.project.models.Tag;
import telerik.project.models.User;

import java.util.List;

public interface TagService {

    List<Tag> getAll();

    Tag getById(Long id);

    Tag getByName(String name);

    Tag createIfNotExists(String name);

    void update(Long id, Tag updatedTag);

    void delete(Long id, User actingUser);
}
