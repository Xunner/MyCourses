package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "message")
public class MessagePO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sender_id")
	private Long senderId;

	@Column(name = "receiver_id")
	private Long receiverId;

	private LocalDateTime time;

	private String title;

	private String message;

	/** 是否未读 */
	private boolean unread;

	public MessagePO() {
	}

	public MessagePO(Long senderId, Long receiverId, LocalDateTime time, String title, String message, boolean unread) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.time = time;
		this.title = title;
		this.message = message;
		this.unread = unread;
	}
}
