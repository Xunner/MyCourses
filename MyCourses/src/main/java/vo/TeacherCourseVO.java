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
	public Long courseId;
	public String name;
	public Integer grade;
	public boolean passReview;
	public List<PostVO> posts;
	public List<CoursewareVO> coursewares;
	public List<TeacherClassVO> classes;

	public TeacherCourseVO(Long courseId, String name, Integer grade, boolean passReview, List<PostVO> posts, List<CoursewareVO> coursewares, List<TeacherClassVO> classes) {
		this.courseId = courseId;
		this.name = name;
		this.grade = grade;
		this.passReview = passReview;
		this.posts = posts;
		this.coursewares = coursewares;
		this.classes = classes;
	}
}
