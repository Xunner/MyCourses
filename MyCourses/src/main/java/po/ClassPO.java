package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

	/** 本班所有学生 */
	@ManyToMany(mappedBy = "classes")
	private List<StudentPO> students;
}
