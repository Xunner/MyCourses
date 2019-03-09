package po;

import lombok.Data;

import javax.persistence.*;

/**
 * 课件
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "courseware")
public class CoursewarePO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "course_id")
	private Long courseId;

	private String name;
}
