package service.impl;

import dao.MessageDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.MessagePO;
import service.MessageService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息服务实现类
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
@Service
@Transactional
public class MessageServiceImpl implements MessageService {
	private final MessageDao messageDao;

	@Autowired
	public MessageServiceImpl(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	@Override
	public Result sendMessage(Long senderId, List<Long> receiverIds, String title, String message) {
		for (Long receiverId : receiverIds) {
			messageDao.save(new MessagePO(senderId, receiverId, LocalDateTime.now(), title, message, true));
		}
		return Result.SUCCESS;
	}

	@Override
	public Result readMessage(Long messageId) {
		MessagePO message = messageDao.findOne(messageId);
		if (message == null) {
			return Result.NOT_EXIST;
		}
		message.setUnread(false);
		messageDao.save(message);
		return Result.SUCCESS;
	}

	@Override
	public Result deleteMessages(List<Long> messageIds) {
		for (Long messageId : messageIds) {
			messageDao.delete(messageId);
		}
		return Result.SUCCESS;
	}

	@Override
	public List<MessagePO> getAllMessagesByReceiverId(Long receiverId) {
		return messageDao.findAllByReceiverId(receiverId);
	}
}
