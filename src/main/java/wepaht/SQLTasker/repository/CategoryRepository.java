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

//    @Query("SELECT o FROM ConcreteOperation o WHERE o.beginTimestamp BETWEEN :from AND :to AND o.status = :status AND o.terminal.deviceId = :deviceId AND o.trainingMode = :trainingMode")
//    Collection<ConcreteOperation> findOperations(
//            @Param("from") @Temporal(TemporalType.TIMESTAMP) Date startDay,
//            @Param("to") @Temporal(TemporalType.TIMESTAMP) Date endDay,
//            @Param("status") OperationStatus status,
//            @Param("deviceId") String deviceId,
//            @Param("trainingMode") boolean trainingMode
//    );
}
