package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

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

	@Column(name = "course_id")
	private Long courseId;

	private String title;

	private String text;

	private LocalDateTime time;

	@OneToMany(targetEntity = ReplyPO.class, cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JoinColumn(name = "post_id")
	private Set<ReplyPO> replies;

	public PostPO() {
	}

	public PostPO(Long userId, Long courseId, String title, String text, LocalDateTime time) {
		this.userId = userId;
		this.courseId = courseId;
		this.title = title;
		this.text = text;
		this.time = time;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PostPO postPO = (PostPO) o;
		return Objects.equals(getId(), postPO.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
