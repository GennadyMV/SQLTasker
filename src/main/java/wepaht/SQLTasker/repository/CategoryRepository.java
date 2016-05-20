package wepaht.SQLTasker.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import wepaht.SQLTasker.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@RestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByStartsBefore(LocalDate date);

    List<Category> findByStarts(LocalDate date);

    List<Category> findByStartsBeforeAndExpiresAfter(LocalDate date1, LocalDate date2);

//    @Query("SELECT o FROM ConcreteOperation o WHERE o.beginTimestamp BETWEEN :from AND :to AND o.status = :status AND o.terminal.deviceId = :deviceId AND o.trainingMode = :trainingMode")
//    Collection<ConcreteOperation> findOperations(
//            @Param("from") @Temporal(TemporalType.TIMESTAMP) Date startDay,
//            @Param("to") @Temporal(TemporalType.TIMESTAMP) Date endDay,
//            @Param("status") OperationStatus status,
//            @Param("deviceId") String deviceId,
//            @Param("trainingMode") boolean trainingMode
//    );

    @Query("SELECT c FROM Category c"
            + " WHERE :today BETWEEN c.starts AND c.expires")
    List<Category> getActiveCategories(@Param("today") LocalDate today);
}
