package service;

import enums.Result;
import po.MessagePO;

import java.util.List;

/**
 * 消息服务接口
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public interface MessageService {
	Result sendMessages(Long senderId, List<Long> receiverIds, String title, String message);

	Result readMessage(Long messageId);

	Result deleteMessages(List<Long> messageIds);

	List<MessagePO> getAllMessagesByReceiverId(Long receiverId);
}
