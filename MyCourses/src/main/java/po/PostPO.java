package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "post")
public class PostPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	private String title;

	private String text;

	private LocalDateTime time;

	@OneToMany(targetEntity = ReplyPO.class, cascade=CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private List<ReplyPO> replies;
}
