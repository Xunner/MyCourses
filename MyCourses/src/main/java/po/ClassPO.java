package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

	@OneToMany(targetEntity = HomeworkPO.class, cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JoinColumn(name = "class_id")
	private List<HomeworkPO> homework;

	/** 本班所有学生以及考试成绩 */
	@ElementCollection
	@CollectionTable(name="student_class_score")
	@MapKeyJoinColumn(name="StudentPO_id")
	@Column(name="score")
	private Map<StudentPO, Double> studentScores;

	public ClassPO() {
	}

	public ClassPO(LocalDateTime startTime, LocalDateTime endTime, Integer classOrder, Integer term) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.classOrder = classOrder;
		this.term = term;
	}
}
