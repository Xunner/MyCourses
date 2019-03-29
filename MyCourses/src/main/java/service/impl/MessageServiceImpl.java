package service.impl;

import dao.ClassDao;
import dao.MessageDao;
import dao.UserDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.ClassPO;
import po.MessagePO;
import po.UserPO;
import service.MessageService;
import vo.NewMessagesVO;

import java.time.LocalDateTime;
import java.util.*;

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
	private final ClassDao classDao;
	private final UserDao userDao;

	@Autowired
	public MessageServiceImpl(MessageDao messageDao, ClassDao classDao, UserDao userDao) {
		this.messageDao = messageDao;
		this.classDao = classDao;
		this.userDao = userDao;
	}

	@Override
	public List<String> sendMessages(NewMessagesVO newMessagesVO) {
		List<String> failedEmails = new ArrayList<>();
		Set<UserPO> receivers = new HashSet<>();
		// 把所有选中班级的学生加入收件人集合里
		for (Long classId : newMessagesVO.classes) {
			ClassPO classPO = classDao.findOne(classId);
			receivers.addAll(classPO.getStudentScores().keySet());
		}
		// 把所有单列的用户加入收件人集合里
		for (Map<String, String> email : newMessagesVO.users) {
			UserPO userPO = userDao.findByEmailAndDeletedFalse(email.get("email"));
			if (userPO == null) {
				failedEmails.add(email.get("email"));
			} else {
				receivers.add(userPO);
			}
		}
		// 遍历收件人集合发消息
		for (UserPO userPO : receivers) {
			messageDao.save(new MessagePO(newMessagesVO.senderId, userPO.getId(), LocalDateTime.now(),
					newMessagesVO.title, newMessagesVO.message, true));
		}
		return failedEmails;
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
