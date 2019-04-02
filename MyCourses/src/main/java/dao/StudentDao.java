package dao;

import enums.StudentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.StudentPO;

/**
 * 学生Dao
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Repository
public interface StudentDao extends JpaRepository<StudentPO, Long> {
	int countByDeletedFalseAndStudentType(StudentType studentType);
}
