package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import po.MessagePO;

import java.util.List;

/**
 * 消息Dao
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Repository
public interface MessageDao extends JpaRepository<MessagePO, Long> {
	List<MessagePO> findAllByReceiverId(Long receiverId);
}
