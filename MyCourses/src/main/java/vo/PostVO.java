package vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class PostVO implements Comparable<PostVO> {
	public Long id;
	public String poster;
	public String title;
	public String text;
	public LocalDateTime time;
	public List<ReplyVO> replies;

	public PostVO(Long id, String poster, String title, String text, LocalDateTime time) {
		this.id = id;
		this.poster = poster;
		this.title = title;
		this.text = text;
		this.time = time;
	}

	@Override
	public int compareTo(PostVO o) {
		return time.isBefore(o.time) ? -1 : 1;
	}
}
