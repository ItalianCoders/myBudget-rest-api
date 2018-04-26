package it.italiancoders.mybudget.dao.movement.impl;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.dao.category.CategoryDao;
import it.italiancoders.mybudget.dao.movement.MovementDao;
import it.italiancoders.mybudget.model.api.ScheduledMovementSettings;
import it.italiancoders.mybudget.model.api.Movement;
import it.italiancoders.mybudget.model.api.Page;
import it.italiancoders.mybudget.model.api.mybatis.MovementSummaryResultType;
import it.italiancoders.mybudget.utils.DateUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MovementDaoImpl extends SqlSessionDaoSupport implements MovementDao {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AccountDao accountDao;

    @Value("${paginationSize}")
    private Integer pageSize;

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    private Map<String, Object> toHashMap(Movement movement){
        Map<String,Object> params = new HashMap<>();
        params.put("id", movement.getId());
        params.put("type", movement.getType().getValue());
        params.put("amount", movement.getAmount());
        params.put("username", movement.getExecutedBy().getUsername());
        params.put("executedAt", movement.getExecutedAt());
        params.put("note",  movement.getNote());
        params.put("accountId", movement.getAccount().getId());
        params.put("categoryId", movement.getCategory().getId());
        params.put("isAuto", movement.isAuto());
        return params;
    }

    private Map<String, Object> toHashMap(ScheduledMovementSettings movement){
        Map<String,Object> params = new HashMap<>();
        params.put("id", movement.getId());
        params.put("account", movement.getAccount().getId());
        params.put("name", movement.getName());
        params.put("description", movement.getDescription());
        params.put("from_date", movement.getStart());
        params.put("end_date", movement.getEnd());
        params.put("frequency", movement.getFrequency().getValue());
        params.put("type", movement.getType().getValue());
        params.put("username", movement.getUser().getUsername());
        params.put("categoryId", movement.getCategory().getId());
        params.put("amount", movement.getAmount());
        return params;
    }


    @Override
    public void inserMovement(Movement movement) {
        Map<String,Object> params = toHashMap(movement);
        getSqlSession().insert("it.italiancoders.mybudget.dao.Movement.insertMovement", params);

    }

    @Override
    public Movement findMovement(String accountId, String id) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("movementId", id);

        return getSqlSession().selectOne("it.italiancoders.mybudget.dao.Movement.findMovements", params);

    }

    @Override
    public List<Movement> findLastMovements(String accountId, Date date, RowBounds limit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("month", month+1);
        params.put("year", year);


        List<Movement> retval =  getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.findMovements", params,limit);

        retval.forEach(movement -> {
            categoryDao.solveTitle(movement.getCategory());
            accountDao.solveTitle(movement.getAccount());
        });

        return retval;
    }

    @Override
    public List<MovementSummaryResultType> calculateSummaryMovements(String accountId, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("month", month+1);
        params.put("year", year);


        List<MovementSummaryResultType> movementSummaryResultTypes = getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.calculateSummaryMovements", params);
        return movementSummaryResultTypes;
    }

    @Override
    public void updateMovement(Movement movement) {
        Map<String,Object> params = toHashMap(movement);
        getSqlSession().update("it.italiancoders.mybudget.dao.Movement.updateMovement", params);

    }

    @Override
    public void deleteMovement(String movementId) {
        Map<String,Object> params = new HashMap<>();
        params.put("id", movementId);
        getSqlSession().delete("it.italiancoders.mybudget.dao.Movement.deleteMovement", params);

    }

    @Override
    public Page<Movement> findMovements(String accountId, Integer year, Integer month, Integer day, String user,String categoryId, Integer page) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("month", month);
        params.put("year", year);
        params.put("day", day);
        params.put("user", user);
        params.put("categoryId", categoryId);

        List<Movement> result = new ArrayList<>();

        Integer count = getSqlSession().selectOne("it.italiancoders.mybudget.dao.Movement.findMovementsCount", params);

        if(count > 0){
            RowBounds rowBounds =new RowBounds(page * pageSize, pageSize);
            result = getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.findMovements", params, rowBounds);
            result.forEach(movement -> {
                categoryDao.solveTitle(movement.getCategory());
                accountDao.solveTitle(movement.getAccount());
            });
        }
        return new Page<Movement>(result, page, pageSize, count);
    }

    @Override
    public List<ScheduledMovementSettings> findAutoMovementToGenerate(Date inDate) {
        Map<String,Object> params = new HashMap<>();
        params.put("inDate", DateUtils.getUnixTime(inDate));
        return getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.findAutoMovementToGenerate", params);
    }

    @Override
    public void setExecutedMovementSettings(ScheduledMovementSettings scheduledMovementSettings, Date execDate) {
        Map<String,Object> params = new HashMap<>();
        params.put("inDate", DateUtils.getUnixTime(execDate));
        params.put("id", scheduledMovementSettings.getId());
        getSqlSession().update("it.italiancoders.mybudget.dao.Movement.setExecutedMovementSettings", params);
    }

    @Override
    public List<ScheduledMovementSettings> getScheduledMovements(String accountId, Date date) {
        Map<String,Object> params = new HashMap<>();
        params.put("inDate", DateUtils.getUnixTime(date));
        params.put("accountId", accountId);
        List<ScheduledMovementSettings> retval = getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.findScheduledMovements", params);
        retval.forEach(movement -> {
            categoryDao.solveTitle(movement.getCategory());
        });

        return retval;
    }

    @Override
    public boolean existScheduleMovement(String accountId, String id, String name) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("name", name);
        params.put("id", id);

        List<ScheduledMovementSettings> retval =
                getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.findScheduledMovements", params);


        return retval != null && retval.size() > 0 ? true : false;
    }


    @Override
    public boolean isValidScheduledMovementUpdate(String accountId, String id, ScheduledMovementSettings newValue) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("name", newValue.getName());
        params.put("currentId", id);

        List<ScheduledMovementSettings> retval =
                getSqlSession().selectList("it.italiancoders.mybudget.dao.Movement.findScheduledMovements", params);


        return retval != null && retval.size() > 0 ? false : true;
    }

    @Override
    public void insertScheduledMovements(ScheduledMovementSettings scheduledMovementSettings) {
        scheduledMovementSettings.setId(UUID.randomUUID().toString());
        Map<String,Object> params = toHashMap(scheduledMovementSettings);
        getSqlSession().insert("it.italiancoders.mybudget.dao.Movement.insertScheduledMovements", params);

    }

    @Override
    public void deleteScheduledMovements(String id) {
        Map<String,Object> params = new HashMap<>();
        params.put("id", id);
        getSqlSession().delete("it.italiancoders.mybudget.dao.Movement.deleteScheduledMovements", params);

    }

    @Override
    public void updateScheduledMovement(ScheduledMovementSettings scheduledMovementSettings) {
        Map<String,Object> params = toHashMap(scheduledMovementSettings);
        getSqlSession().update("it.italiancoders.mybudget.dao.Movement.updateScheduledMovement", params);

    }
}
