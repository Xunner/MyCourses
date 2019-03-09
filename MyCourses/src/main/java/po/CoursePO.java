package po;

import lombok.Data;

import javax.persistence.*;

/**
 * 课程
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "course")
public class CoursePO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "teacher_id")
	private Long teacherId;

	private String name;

	/** 本课属于几年级的课程，默认从1开始 */
	private Integer grade;

	public CoursePO() {
	}

	public CoursePO(Long teacherId, String name, Integer grade) {
		this.teacherId = teacherId;
		this.name = name;
		this.grade = grade;
	}
}
