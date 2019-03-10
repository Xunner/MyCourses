package vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * getClass方法 数据
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class ClassInfo {
	public Long id;
	public String name;
	public String teacherName;
	public Integer grade;
	public Integer term;
	public Integer classOrder;
	public LocalDateTime startTime;
	public LocalDateTime endTime;
	public List<CoursewareVO> coursewares;
	public List<HomeworkVO> homework;
	public List<PostVO> posts;

//	public ClassInfo(Long id, String name, String teacherName, Integer grade, Integer term, Integer classOrder, LocalDateTime startTime, LocalDateTime endTime, List<CoursewareVO> courseware, List<HomeworkVO> homework, List<PostVO> post) {
//		this.id = id;
//		this.name = name;
//		this.teacherName = teacherName;
//		this.grade = grade;
//		this.term = term;
//		this.classOrder = classOrder;
//		this.startTime = startTime;
//		this.endTime = endTime;
//		this.courseware = courseware;
//		this.homework = homework;
//		this.post = post;
//	}
}
