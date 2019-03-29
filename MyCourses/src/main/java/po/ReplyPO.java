package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 回帖
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "reply")
public class ReplyPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "post_id")
	private Long postId;

	private String text;

	private LocalDateTime time;

	public ReplyPO() {
	}

	public ReplyPO(Long userId, Long postId, String text, LocalDateTime time) {
		this.userId = userId;
		this.postId = postId;
		this.text = text;
		this.time = time;
	}
}
