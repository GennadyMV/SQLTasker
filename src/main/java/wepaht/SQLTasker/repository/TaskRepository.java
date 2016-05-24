/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wepaht.SQLTasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Task;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@RestResource(exported = false)
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByNameOrderByNameDesc(String name);
    
//        @Query("SELECT username, COUNT(*) AS points FROM PastQuery WHERE awarded=true AND correct=true GROUP BY username")

    @Query("SELECT id FROM Task")
    List<Long> findAllTaskIds();
}
