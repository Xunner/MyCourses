package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.PostPO;

import java.util.List;

/**
 * 帖子Dao
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Repository
public interface PostDao extends JpaRepository<PostPO, Long> {
	List<PostPO> findAllByCourseId(Long courseId);
}
