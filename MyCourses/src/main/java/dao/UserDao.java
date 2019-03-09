package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.UserPO;

/**
 * 用户Dao
 * <br>
 * created on 2019/03/05
 *
 * @author 巽
 **/
@Repository
public interface UserDao extends JpaRepository<UserPO, Long> {
	UserPO findByEmailAndPasswordAndDeletedFalse(String email, String password);

	boolean existsByEmailAndDeletedFalse(String email);
}
