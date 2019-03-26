package vo;

import java.util.List;

/**
 * 教师课程VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class TeacherCourseVO {
	Long courseId;
	String name;
	Integer grade;
	boolean passReview;
	List<TeacherClassVO> classes;

	public TeacherCourseVO(Long courseId, String name, Integer grade, boolean passReview, List<TeacherClassVO> classes) {
		this.courseId = courseId;
		this.name = name;
		this.grade = grade;
		this.passReview = passReview;
		this.classes = classes;
	}
}
