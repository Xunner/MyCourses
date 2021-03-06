package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.AdminPO;

/**
 * 主管Dao
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Repository
public interface AdminDao extends JpaRepository<AdminPO, Long> {
}
