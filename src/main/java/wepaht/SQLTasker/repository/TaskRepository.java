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

@RestResource(exported = false)
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByNameOrderByNameDesc(String name);
}
