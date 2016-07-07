package wepaht.SQLTasker.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaht.SQLTasker.domain.Tag;
import wepaht.SQLTasker.domain.Task;
import wepaht.SQLTasker.repository.TagRepository;

@Service
public class TagService {
    
    @Autowired
    TagRepository repository;
    
    public Tag createTag(String tagName, Task task) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setTask(task);
        return repository.save(tag);
    }

    public List<Tag> getTagsByTask(Task task) {
        return repository.findByTask(task);
    }

    Tag getTagByNameAndTask(String name, Task task) {
        return repository.findByNameAndTask(name, task);
    }

    void deleteTag(Tag tag) {
        repository.delete(tag);
    }
}
