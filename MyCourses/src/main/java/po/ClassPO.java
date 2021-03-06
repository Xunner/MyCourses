package po;

import enums.ClassState;
import enums.PublishMethod;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 开课、班次
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "class")
public class ClassPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "course_id")
	private Long courseId;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	/** 班次，如1班、2班，默认从1开始 */
	@Column(name = "class_order")
	private Integer classOrder;

	/** 第几学期的班次，默认从1开始 */
	private Integer term;

	@Enumerated
	@Column(name = "class_state")
	private ClassState classState;

	/** 选课人数上限 */
	@Column(name = "max_number")
	private Integer maxNumber;

	/** 教师公布考试成绩的方式 */
	@Enumerated
	@Column(name = "publish_method")
	private PublishMethod publishMethod;

	@OneToMany(targetEntity = HomeworkPO.class, cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JoinColumn(name = "class_id")
	private Set<HomeworkPO> homework;

	/** 本班所有学生以及考试成绩 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="student_class_score")
	@MapKeyJoinColumn(name="StudentPO_id")
	@Column(name="score")
	private Map<StudentPO, Double> studentScores;

	public ClassPO() {
	}

	public ClassPO(Long courseId, LocalDateTime startTime, LocalDateTime endTime, Integer classOrder, Integer term, ClassState classState, Integer maxNumber) {
		this.courseId = courseId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.classOrder = classOrder;
		this.term = term;
		this.classState = classState;
		this.maxNumber = maxNumber;
		this.publishMethod = PublishMethod.NOT_YET;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassPO classPO = (ClassPO) o;
		return Objects.equals(getId(), classPO.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
