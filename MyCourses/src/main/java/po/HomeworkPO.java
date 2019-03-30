package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * （老师发布的）作业
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "homework")
public class HomeworkPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "class_id")
	private Long classId;

	private String name;

	private String description;

	private LocalDateTime deadline;

	/** 提交文件大小上限(MB) */
	@Column(name = "size_limit")
	private Integer sizeLimit;

	/** 提交文件类型限制，格式形如：""(无限制), "txt", "txt/doc" */
	@Column(name = "type_restriction")
	private String typeRestriction;

	@OneToMany(targetEntity = SubmissionPO.class, cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JoinColumn(name = "homework_id")
	private Set<SubmissionPO> submissions;

	/** 本作业所有学生及其成绩 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="student_homework_score")
	@MapKeyJoinColumn(name="student_id")
	@Column(name="score")
	private Map<StudentPO, Double> studentScores;

	public HomeworkPO() {
	}

	public HomeworkPO(Long classId, String name, String description, LocalDateTime deadline, Integer sizeLimit, String typeRestriction) {
		this.classId = classId;
		this.name = name;
		this.description = description;
		this.deadline = deadline;
		this.sizeLimit = sizeLimit;
		this.typeRestriction = typeRestriction;
	}
}
