package service;

import enums.Result;
import po.MessagePO;
import vo.NewMessagesVO;

import java.util.List;

/**
 * 消息服务接口
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public interface MessageService {
	List<String> sendMessages(NewMessagesVO newMessagesVO);

	Result readMessage(Long messageId);

	Result deleteMessages(List<Long> messageIds);

	List<MessagePO> getAllMessagesByReceiverId(Long receiverId);
}
