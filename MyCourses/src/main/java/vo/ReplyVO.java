package vo;

import java.time.LocalDateTime;

/**
 * 回帖VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class ReplyVO {
	public Long id;
	public String replies;
	public String text;
	public LocalDateTime time;

	public ReplyVO(Long id, String replies, String text, LocalDateTime time) {
		this.id = id;
		this.replies = replies;
		this.text = text;
		this.time = time;
	}
}
