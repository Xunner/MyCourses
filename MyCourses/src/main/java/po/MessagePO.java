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

	private Long sender;

	@Column(name = "receiver_id")
	private Long receiverId;

	private LocalDateTime time;

	private String message;

	/** 是否未读 */
	private boolean unread;
}
