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
import org.springframework.data.repository.query.Param;
import wepaht.SQLTasker.domain.TmcAccount;

@RestResource(exported = false)
public interface TaskRepository extends JpaRepository<Task,Long> {
    @Override
    @Query("SELECT t FROM Task t WHERE t.id=:id AND t.deleted=false")
    Task findOne(@Param("id") Long id);
    
    @Override
    @Query("SELECT t FROM Task t WHERE t.deleted=false")
    List<Task> findAll();
    
    List<Task> findByNameAndDeletedFalseOrderByNameDesc(String name);    

    @Query("SELECT t.id FROM Task t WHERE t.deleted=false")
    List<Long> findAllTaskIds();

    public List<Task> findByOwnerAndDeletedFalse(TmcAccount authenticatedUser);
}
