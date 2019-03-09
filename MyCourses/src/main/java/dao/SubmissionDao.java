package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.SubmissionPO;

/**
 * 作业提交Dao
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Repository
public interface SubmissionDao extends JpaRepository<SubmissionPO, Long> {
}
