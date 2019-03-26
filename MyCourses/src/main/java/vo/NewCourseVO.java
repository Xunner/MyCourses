package vo;

/**
 * 新课程VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class NewCourseVO {
	public Long courseId;
	public Integer grade;
	public String name;
	public String teacher;

	public NewCourseVO(Long courseId, Integer grade, String name, String teacher) {
		this.courseId = courseId;
		this.grade = grade;
		this.name = name;
		this.teacher = teacher;
	}
}
