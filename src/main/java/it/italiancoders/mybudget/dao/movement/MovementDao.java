package it.italiancoders.mybudget.dao.movement;

import it.italiancoders.mybudget.model.api.ScheduledMovementSettings;
import it.italiancoders.mybudget.model.api.Movement;
import it.italiancoders.mybudget.model.api.Page;
import it.italiancoders.mybudget.model.api.mybatis.MovementSummaryResultType;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;

public interface MovementDao {
    void inserMovement(Movement movement);

    Movement findMovement(String accountId, String id);

    List<Movement> findLastMovements(String accountId, Date date, RowBounds limit);

    List<MovementSummaryResultType> calculateSummaryMovements(String accountId, Date date);

    void updateMovement(Movement movement);

    void deleteMovement(String movementId);

    Page<Movement> findMovements(String accountId, Integer year, Integer month, Integer day, String user, String categoryId, Integer page);

    List<ScheduledMovementSettings> findAutoMovementToGenerate(Date inDate);

    void setExecutedMovementSettings(ScheduledMovementSettings scheduledMovementSettings, Date execDate);

    List<ScheduledMovementSettings> getScheduledMovements(String accountId, Date date);

    boolean existScheduleMovement(String accountId, String id, String name);

    void insertScheduledMovements(ScheduledMovementSettings scheduledMovementSettings);

    void deleteScheduledMovements(String id);

    void updateScheduledMovement(ScheduledMovementSettings scheduledMovementSettings);

    boolean isValidScheduledMovementUpdate(String accountId, String id, ScheduledMovementSettings newValue);
}
