package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.UserPO;

import java.util.ArrayList;

/**
 * 用户Dao
 * <br>
 * created on 2019/03/05
 *
 * @author 巽
 **/
@Repository
public interface UserDao extends JpaRepository<UserPO, Long> {
	UserPO findById(Long id);

	ArrayList<UserPO> findAllByNameOrStudentIdOrEmail(String name, String studentId, String email);
}
