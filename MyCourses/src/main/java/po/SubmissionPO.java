package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * （学生）提交的作业
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "submission")
public class SubmissionPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "homework_id")
	private Long homeworkId;

	@Column(name = "student_id")
	private Long studentId;

	private String name;

	private LocalDateTime time;

	public SubmissionPO() {
	}

	public SubmissionPO(Long homeworkId, Long studentId, String name, LocalDateTime time) {
		this.homeworkId = homeworkId;
		this.studentId = studentId;
		this.name = name;
		this.time = time;
	}
}
