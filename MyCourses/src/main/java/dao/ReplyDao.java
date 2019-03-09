package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.ReplyPO;

/**
 * 回帖Dao
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Repository
public interface ReplyDao extends JpaRepository<ReplyPO, Long> {
}
