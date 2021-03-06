package dao;

import enums.ClassState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.ClassPO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班次Dao
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Repository
public interface ClassDao extends JpaRepository<ClassPO, Long> {
	List<ClassPO> findAllByClassStateAndStartTimeBefore(ClassState classState, LocalDateTime startTime);

	List<ClassPO> findAllByClassState(ClassState classState);

	List<ClassPO> findAllByCourseId(Long courseId);
}
