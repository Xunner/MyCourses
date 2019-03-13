package vo;

import java.time.LocalDateTime;

/**
 * 消息VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class MessageVO {
	public Long messageId;
	public String title;
	public String sender;
	public LocalDateTime time;
	public String message;

	public MessageVO(Long messageId, String title, String sender, LocalDateTime time, String message) {
		this.messageId = messageId;
		this.title = title;
		this.sender = sender;
		this.time = time;
		this.message = message;
	}
}
