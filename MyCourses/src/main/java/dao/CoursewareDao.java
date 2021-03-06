package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.CoursewarePO;

import java.util.List;

/**
 * 课程Dao
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Repository
public interface CoursewareDao extends JpaRepository<CoursewarePO, Long> {
	List<CoursewarePO> findAllByCourseId(Long courseId);
}
