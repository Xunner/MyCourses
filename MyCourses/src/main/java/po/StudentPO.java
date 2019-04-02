package po;

import enums.StudentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Map;

/**
 * 学生
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("S")
public class StudentPO extends UserPO {
	@Column(name = "student_id")
	private String studentId;

	@Enumerated
	@Column(name = "student_type")
	private StudentType studentType;

	/** 学生参加的所有班级及其成绩 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="student_class_score")
	@MapKeyJoinColumn(name="ClassPO_id")
	@Column(name="score")
	private Map<ClassPO, Double> classScores;

	public StudentPO() {
	}

	public StudentPO(String email, String name, String password, boolean deleted, String studentId, StudentType studentType) {
		super(email, name, password, deleted);
		this.studentId = studentId;
		this.studentType = studentType;
	}
}
