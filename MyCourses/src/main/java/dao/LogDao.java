package dao;

import enums.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.LogPO;

import java.util.List;

/**
 * 日志Dao
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
@Repository
public interface LogDao extends JpaRepository<LogPO, Long> {
	List<LogPO> findAllByUserIdAndObjectClassAndOperationType(Long userId, String objectClass, OperationType operationType);
}
