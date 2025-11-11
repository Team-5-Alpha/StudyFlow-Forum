package telerik.project.repositories.contracts;

import telerik.project.models.Tag;

import java.util.List;

public interface TagRepository {

    List<Tag> getAll();

    Tag getById(Long id);

    Tag getByName(String name);

    void create(Tag tag);

    void update(Tag tag);

    void delete(Long id);
}
